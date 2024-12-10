# Blog Application

This is a simple and powerful blog application built with Spring Boot. The application allows users to create, read, update, and delete blog posts, making it a perfect starting point for anyone looking to build a modern web application with Spring Boot.

## Prerequisites

Make sure you have Docker and Maven installed on your system.

## Installation and Setup

### Clone the Repository
```bash
git clone https://github.com/Mohammad-Ranjbr/Blog-Application.git
```

### Running Minio in Docker

To run Minio with Docker, use the following command:

```bash
docker run -p 9000:9000 -p 9090:9090 --name minio \
-e "MINIO_ROOT_USER=admin" \
-e "MINIO_ROOT_PASSWORD=admin123" \
-v /path/to/local/data:/data \
minio/minio server /data --console-address ":9090"
```

#### For example, you can run it like this:
```bash
docker run -p 9000:9000 -p 9090:9090 --name minio \
-e "MINIO_ROOT_USER=admin" \
-e "MINIO_ROOT_PASSWORD=admin123" \
-v /home/user/minio_data:/data \
minio/minio server /data --console-address ":9090"
```

## Build and Run with Maven
```bash
mvn clean package
mvn spring-boot:run
```

