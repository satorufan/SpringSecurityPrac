package com.cheeus.firebase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class ImageUploadService {
	
	public File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
	    File tempFile = new File(fileName);
	    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
	        fos.write(multipartFile.getBytes());
	        fos.close();
	    }
	    return tempFile;
	}
	
	public String uploadFile(File file, String fileName, String type) throws IOException {
		// 버킷과 파일 이름 지정
	    BlobId blobId = BlobId.of("cheeusfinal.appspot.com", fileName);
	    
	    // 파일 정보 지정
	    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(type).build();
	    InputStream inputStream = ImageUploadService.class.getClassLoader().getResourceAsStream("java-firebase-sdk-firebase-adminsdk.json");
	    Credentials credentials = GoogleCredentials.fromStream(inputStream);
	    Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
	    
	    // 스토리지에 파일 저장
	    storage.create(blobInfo, Files.readAllBytes(file.toPath()));

	    String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/cheeusfinal.appspot.com/o/%s?alt=media";
	    return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
	}

}
