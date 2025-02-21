# ShareSafely - File Share Web App

This project provides functionality to upload files to an Amazon S3 bucket, generate presigned URLs for file access with an expiration time, and automatically delete expired files using AWS Lambda.

---

## Features

- **File Upload**: Allows uploading files to Amazon S3 bucket.
- **Presigned URL Generation**: Generate a presigned URL for file access with an expiration time set by user.
- **Automatic File Deletion**: Lambda function checks for expired files based on timestamps and deletes them from the S3 bucket.

---

## Architecture

1. **S3 Bucket**: Used to store the uploaded files.
2. **Lambda Function**: 
   - Runs periodically to check for expired files based on the timestamp in the file name and delete them.
   - The Lambda function triggers based on the presigned URL expiration and compares the current time with the timestamp stored in the file name.
3. **Presigned URL**: Generated to allow secure and temporary access to the files in the S3 bucket.
4. **AWS Secrets Manager**: Stores S3 credentials securely, ensuring that sensitive information such as access keys is not hardcoded in the code.
5. **IAM Policies** - IAM permissions to interact with AWS resources (S3 and Secrets Manager)

---

## API Endpoints

Upload a File - The file will be stored in the S3 bucket, and a presigned URL will be generated for temporary access to the file.
Endpoint: POST /s3/upload
```bash
curl --location 'http://localhost:8080/s3/upload' \
--form 'file=@"/C:/Users/subbareddy/Downloads/ticketpayment.pdf"' \
--form 'expirationSeconds="60"'
```

Response: 

<img width="640" alt="image" src="https://github.com/user-attachments/assets/2c89fb61-29a7-4734-a97c-d96bb17e84c2" />

---

## Installation & Setup
1. AWS Setup:
    - Create an S3 bucket in the AWS account.
    - Configure an IAM role for Lambda that allows it to read from and delete files in the S3 bucket and access AWS Secrets Manager.
    - Set up AWS Secrets Manager to securely store the S3 credentials.
    - Create a Lambda function that will be used for checking and deleting expired files from S3.

2. Clone the Repository
```bash
git clone https://github.com/subba31r/ShareSafely.git
cd ShareSafely
```

3. Build the Application
```bash
mvn clean package
```

4. Run the Application
```bash
mvn spring-boot:run
```
---

## Technologies Used

- Java 17
- Spring Boot
- Maven
- AWS

---

## Future Enhancements
- **Deploy the application on AWS EC2:**
  The application will be deployed on an AWS EC2 instance in the future. The deployment process will involve setting up the EC2 instance, installing necessary dependencies, and configuring the application to expose the endpoint via a web server like Nginx.
  - Steps:
    1. Launch an EC2 instance
    2. SSH into the instance and install required software(Java)
    3. Upload the code and run the application
    4. Set up security groups and Nginx as a reverse proxy
    5. Expose the endpoint to the public
