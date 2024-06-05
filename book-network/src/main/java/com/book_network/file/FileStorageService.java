package com.book_network.file;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
	
	@Value("${application.file.upload.photos-output-path}")
	private String fileUploadPath;
	
	public String saveFile(
			@NotNull MultipartFile sourceFile, 
			@NotNull Integer userId) {
		
		final String fileUploadSubPath = "users" + separator + userId;
		
		return uploadFile(sourceFile, fileUploadSubPath);
		
	}

	private String uploadFile(
			@NotNull MultipartFile sourceFile, 
			@NotNull String fileUploadSubPath) {
		final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
		File targetFolder = new File(finalUploadPath);
		
		if (!targetFolder.exists()) {
			boolean folderCreated = targetFolder.mkdirs();
			if (!folderCreated) {
				log.warn("Failed to creat the target folder");
				return null;
			}
		}
		
		final String fileExtention = getFileExtention(sourceFile.getOriginalFilename());
		
		// ./upload/users/1/3323223535512.jpg
		
		String targetFilePath = finalUploadPath + separator + currentTimeMillis() + "." + fileExtention;
		Path targetPath = Paths.get(targetFilePath);
		try {
			Files.write(targetPath, sourceFile.getBytes());
			log.info("File svaed to "+ targetFilePath);
		} catch (IOException e) {
			log.error("File wass not saved ", e);
		}
		return null;
	}

	private String getFileExtention(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return "";
		}
		//something.jpg
		int lastDotIndex = fileName.lastIndexOf(".");
		if (lastDotIndex == -1) {
			return "";
		}

		return fileName.substring(lastDotIndex + 1).toLowerCase();
	}

}
   