package dev.ioexception.crawling.page;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class UploadImage {

    private final AmazonS3 amazonS3Client;
    private final String BUCKET_NAME = "crawling-img";

    public String uploadFromUrlToS3(String imageUrl, String dirName, String filename) throws IOException {
        byte[] imageBytes = readImageBytes(imageUrl);

        String fileName = dirName + "/" + filename + ".jpg";
        ObjectMetadata objectMetadata = new ObjectMetadata();

        if (imageBytes != null) {
            objectMetadata.setContentLength(imageBytes.length);
        } else {

            return "no image";
        }

        amazonS3Client.putObject(
                new PutObjectRequest(BUCKET_NAME, fileName, new ByteArrayInputStream(imageBytes), objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(BUCKET_NAME, fileName).toString();
    }

    private byte[] readImageBytes(String imageUrl) throws IOException {
        try {
            URL url = new URL(imageUrl);

            try (InputStream inputStream = url.openStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return outputStream.toByteArray();
            }

        } catch (Exception e) {
            return null;
        }
    }
}