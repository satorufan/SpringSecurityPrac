package com.cheeus.firebase;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class ImageDeleteService {

	public void deleteImage (String category, String email, int cnt) throws IOException {
		
		InputStream inputStream = ImageDeleteService.class.getClassLoader().getResourceAsStream("java-firebase-sdk-firebase-adminsdk.json");
		Credentials credentials = GoogleCredentials.fromStream(inputStream);
		
		Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
		
		String bucketName = "cheeusfinal.appspot.com";
		
		
		for (int i = 0 ; i < cnt ; i++) {
			
			String blobName = category + email + "/" + i;
			BlobId blobId = BlobId.of(bucketName, blobName);
			if (storage.get(blobId)!=null) {
				boolean deleted = storage.delete(blobId);
				System.out.println(deleted);
				System.out.println(blobName);
			}
		}
	}
}
