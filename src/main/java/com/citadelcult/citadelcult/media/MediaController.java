package com.citadelcult.citadelcult.media;

import com.citadelcult.citadelcult.media.entities.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Media>> uploadFile(@RequestParam(value = "file") MultipartFile[] file) {
        List<Media> uploadedMedia = mediaService.uploadFile(file);

        if (!uploadedMedia.isEmpty()) {
            return new ResponseEntity<>(uploadedMedia, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Media>> findAll() {
        List<Media> media = mediaService.findAll();
        return new ResponseEntity<>(media, HttpStatus.OK);
    }

    @DeleteMapping("/{fileName:.+}")
    public ResponseEntity<Void> deleteFile(@PathVariable("fileName") String fileName) {
        mediaService.deleteFile(fileName);
        return ResponseEntity.ok().build();
    }
}