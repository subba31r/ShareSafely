import boto3
import datetime
import urllib.parse

# Initialize the S3 client
s3 = boto3.client('s3')

BUCKET_NAME = "sharesafely-bucket"

def extract_expiration_time(file_key):
    try:
        file_name_parts = file_key.split("_")
        expiration_timestamp = int(file_name_parts[0])
        return expiration_timestamp

    except Exception as e:
        print(f"Error extracting expiration time: {str(e)}")
        return None

def lambda_handler(event, context):
    
    try:
        response = s3.list_objects_v2(Bucket=BUCKET_NAME)
        
        if "Contents" in response:
            for obj in response["Contents"]:
                file_key = obj["Key"]

                expiration_timestamp = extract_expiration_time(file_key)

                if expiration_timestamp:
                    # Compare expiration timestamp with current time
                    current_timestamp = int(datetime.datetime.utcnow().timestamp())

                    if current_timestamp > expiration_timestamp:
                        # File has expired, delete it from S3
                        s3.delete_object(Bucket=BUCKET_NAME, Key=file_key)
                        print(f"Deleted expired file: {file_key}")
        
        return {"statusCode": 200, "body": "Expired files deleted successfully"}

    except Exception as e:
        print(f"Error: {str(e)}")
        return {"statusCode": 500, "body": str(e)}
