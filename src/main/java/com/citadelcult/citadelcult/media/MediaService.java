package com.citadelcult.citadelcult.media;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.citadelcult.citadelcult.media.entities.Media;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MediaService {

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;
    private final MediaRepository mediaRepository;

    public MediaService(AmazonS3 s3Client, MediaRepository mediaRepository) {
        this.s3Client = s3Client;
        this.mediaRepository = mediaRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Media uploadFile(MultipartFile multipartFile) {
        File file = convertMultiPartFileToFile(multipartFile);
        String fileName = System.currentTimeMillis() + "-" + multipartFile.getOriginalFilename();

        PutObjectResult result = s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
        if (result != null && result.getMetadata() != null) {
            String imageUrl = generateS3Url(fileName);

            Media media = new Media();
            media.setName(fileName);
            media.setAlt(fileName);
            media.setUrl(imageUrl);

            Media savedMedia = mediaRepository.save(media);
            file.delete();

            log.info("File '{}' uploaded successfully. URL: '{}'", fileName, imageUrl);
            return savedMedia;
        } else {
            file.delete();
            log.error("Failed to upload file: '{}'", fileName);
            return null;
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Media> uploadFile(MultipartFile[] files) {
        List<Media> uploadedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            Media uploadedImage = uploadFile(file);
            if (uploadedImage != null) {
                uploadedImages.add(uploadedImage);
            }
        }

        return uploadedImages;
    }

    public List<Media> findAll() {
        return mediaRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }

    public Media getMediaByUrl(String url) {
        return mediaRepository.findByUrl(url);
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

    private String generateS3Url(String fileName) {
        String region = s3Client.getRegionName();
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }
}
