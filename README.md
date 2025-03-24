# E-Commerce Backend API (Spring Boot)

## Overview
This is the backend service for an E-Commerce platform built with Spring Boot. It provides RESTful APIs to support online transactions, user authentication, order processing, and integration with external services such as AWS S3, Facebook Marketing API, and GeoIP MaxMind.

## Features

- **User Authentication & Authorization**: Secure authentication using JWT.
- **Multi-Currency Support**: Currency conversion for global transactions.
- **AWS S3 Integration**: Storage for product images and user uploads.
- **Facebook Marketing API**: Advertising and audience management.
- **GeoIP MaxMind**: Location-based services.

## Environment Variables
To run this project, configure the following environment variables:

```env
# Database Configuration
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=${DATABASE_USERNAME}
SPRING_DATASOURCE_PASSWORD=${DATABASE_PASSWORD}

# Hibernate Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# AWS S3 Configuration
CLOUD_AWS_CREDENTIALS_ACCESS_KEY=${AWS_ACCESS_KEY}
CLOUD_AWS_CREDENTIALS_SECRET_KEY=${AWS_SECRET_KEY}
CLOUD_AWS_REGION=${AWS_REGION}
CLOUD_AWS_BUCKET_NAME=${AWS_BUCKET_NAME}

# Telegram Notifications
TELEGRAM_BOT_TOKEN=your_telegram_bot_token
TELEGRAM_CHAT_ID=your_chat_id

# Frontend & Backend URLs
FRONTEND_URL=http://localhost:3000
BACKEND_URL=http://localhost:8080

# Fondy Payment Gateway
FONDY_MERCHANT_ID=your_merchant_id
FONDY_MERCHANT_PASSWORD=your_merchant_password

# JWT Authentication
JWT_SECRET_KEY=your_jwt_secret_key
JWT_EXPIRATION=900000
JWT_COOKIE_NAME=jwt-cookie

# Facebook Marketing API
FACEBOOK_ACCESS_TOKEN=your_facebook_access_token
FACEBOOK_PIXEL_ID=your_pixel_id
```

## Installation & Running

1. Clone the repository:
   ```sh
   git clone https://github.com/kolyageshko/ecommerce-platform.git
   cd citadelcult
   ```
2. Configure environment variables.
3. Run the application:
   ```sh
   ./mvnw spring-boot:run
   ```

## API Endpoints
The backend exposes various endpoints for authentication, product management, and transactions. Refer to the source code for details.
