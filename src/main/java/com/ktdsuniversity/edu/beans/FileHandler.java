package com.ktdsuniversity.edu.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public class FileHandler {
	/**
	 * 업로드 된 파일이 저장될 위치
	 */
	private String baseDir;
	/**
	 * 파일명을 난독화 할지에 대한 여부
	 */
	private boolean enableObfuscation;
	
	/**
	 * 확장자를 숨길지에 대한 여부
	 */
	private boolean enableObfuscationHideExt;

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public void setEnalbeObfuscation(boolean enalbeObfuscation) {
		this.enableObfuscation = enalbeObfuscation;
	}

	public void setEnableObfuscationHideExt(boolean enableObfuscationHideExt) {
		this.enableObfuscationHideExt = enableObfuscationHideExt;
	}
	
	/**
	 * 서버에 등록한 파일을 반환한다.
	 * @param fileName 찾아올 파일 명
	 * @return 파일 객체
	 */
	public File getStoredFile(String fileName) {
		return new File(baseDir, fileName);
	}
	
	/**
	 * 파일 다운로드를 처리
	 * @param downloadFile 다운로드 싴ㄹ 서버의 파일을 
	 * @param downloadFileName
	 * @return
	 */
	public ResponseEntity<Resource> getResponseEntity(File downloadFile, String downloadFileName) {
		//
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+ downloadFileName);
		//다운로드 할 파일의 리소스(byte)를 생성
		InputStreamResource resource;
		try {
			resource = new InputStreamResource(new FileInputStream(downloadFile));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("파일이 존재하지 않습니다.");
		}
		//HTTP 응답 객체를 직접 생성
		return ResponseEntity.ok()
					.headers(header)
					.contentLength(downloadFile.length())
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.body(resource);
	}
	
	/**
	 * 사용자가 업로드한 파일을 서버에 저장시킨다.
	 * @param multipartFile 사용자가 업로드한 파일
	 * @return 업로드 결과
	 */
	public StoreFile storeFile(MultipartFile multipartFile) {
		//사용자가 파일을 업로드 하지 않았다면 null을 반환.
		if(multipartFile == null || multipartFile.isEmpty()) {
			return null;
		}
		String originalFileName = multipartFile.getOriginalFilename();
		//사용자가 업로드한 파일의 이름을 난독화 설정에 따라 받아옴.
		String fileName = getObfuscationFileName(originalFileName);
		//파일이 저장될 위치를 지정
		File storePath = new File(baseDir, fileName);
		//만약, 파일이 저장될 위치가 존재하지 않는다면
		if(!storePath.getParentFile().exists()) {
			//폴더를 생성한다.
			storePath.getParentFile().mkdirs();
		}
		//사용자가 업로드한 파일을 파일이 저장될 위치로 이동시킴.
		try {
			multipartFile.transferTo(storePath);
		} catch (IllegalStateException | IOException e) {
			//업로드한 파일을 이동하는 중에 예외가 발생하면
			//업로드를 실패한 것이므로 null을 반환.
			return null;
		}
		//업로드 결과를 반한한다.
		return new StoreFile(originalFileName, storePath);
	}
	
	/**
	 * 파일명을 난독화 처리하는 기능
	 * @param fileName 사용자가 업로드한 파일의 이름.
	 * @return 설정값에 따라 난독화된 이름 또는 업로드한 파일의 이름.
	 */
	private String getObfuscationFileName(String fileName) {
		//난독화 설정을 했을 대
		if(enableObfuscation) {
			//파일명에서 확장자를 분리
			String ext = fileName.substring(fileName.lastIndexOf("."));
			//현재시간을 기준으로 난독화된 코드를 받아옴
			String obfuscationName = UUID.randomUUID().toString();
			//확장자를 숨김처리 설정을 했다면
			if (enableObfuscationHideExt) {
			//확장자를 제외한 난독화된 코드만 반환하고
				return obfuscationName;
			}
			//확장자를 숨김처리 하지 않았다면
			else {
				//난독화된 코드 뒤에 확장자를 붙여서 반환함.
				return obfuscationName + ext;
			}
		}
		return fileName;
	}
	
	//Class안에 Class => Nested Class
	public class StoreFile {
		/**
		 * 사용자가 업로드한 파일의 이름(확장자 포함)
		 */
		private String fileName;
		/**
		 * 서버에 저장된 파일의 실제 이름.
		 * 난독화 설정했다면, 파일의 이름은 난독화되어 저장된다.
		 */
		private String realFileName;
		/**
		 * 서버에 저장된 파일의 경로
		 */
		private String realFilePath;
		/**
		 * 서버에 저장된 파일의 크기(byte단위)
		 */
		private long fileSize;
		
		StoreFile(String fileName, File storeFile) {
			this.fileName = fileName;
			this.realFileName = storeFile.getName();
			this.realFilePath = storeFile.getAbsolutePath();
			this.fileSize = storeFile.length();
		}

		public String getFileName() {
			return fileName;
		}

		public String getRealFileName() {
			return realFileName;
		}

		public String getRealFilePath() {
			return realFilePath;
		}

		public long getFileSize() {
			return fileSize;
		}
	}
	
}
