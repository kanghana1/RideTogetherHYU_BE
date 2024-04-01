package com.ridetogether.server.global.file;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OracleFileService implements FileService {

	@Autowired
	ObjectStorage objectStorage;

	@Autowired
	UploadManager uploadManager;

	private static final String BUCKET_NAME = "RideTogetherHYU_Bucket";
	private static final String BUCKET_NAME_SPACE = "axjoaeuyezzj";
	private static final String PROFILE_IMG_DIR = "profile/";
	private static final String KAKAO_IMG_DIR = "kakao/";

	@Override
	public String uploadProfileImg(MultipartFile file, Long memberId) throws Exception{
		File uploadFile = convert(file)  // 파일 변환할 수 없으면 에러
				.orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
		String fileDir = memberId + "/" + PROFILE_IMG_DIR;
		return upload(uploadFile, fileDir);
	}
	@Override
	public String uploadKakaoQrImg(MultipartFile file, Long memberId) throws Exception{
		File uploadFile = convert(file)  // 파일 변환할 수 없으면 에러
				.orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
		String fileDir = memberId + "/" + KAKAO_IMG_DIR;
		return upload(uploadFile, fileDir);
	}

	@Override
	public String getPublicImgUrl(String imgUrl, Long memberId) {
		return null;
	}

	@Override
	public MultipartFile downloadImg(String imgUrl, Long memberId) {
		return null;
	}

	// 버킷에서 이미지 삭제
	@Override
	public void deleteImg(String imgUrl) throws Exception {
		DeleteObjectRequest request =
				DeleteObjectRequest.builder()
						.bucketName(BUCKET_NAME)
						.namespaceName(BUCKET_NAME_SPACE)
						.objectName(imgUrl)
						.build();
		
		objectStorage.deleteObject(request);
		objectStorage.close();
	}

	// 오라클 버킷으로 파일 업로드
	private String upload(File uploadFile, String dirName) throws Exception{
		String fileName = dirName + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
		String contentType = fileName.endsWith(".png") ? "image/png" : "image/jpg";
		String contentEncoding = null;
		String contentLanguage = null;
		Map<String, String> metadata = null;
		PutObjectRequest request =
				PutObjectRequest.builder()
						.bucketName(BUCKET_NAME)
						.namespaceName(BUCKET_NAME_SPACE)
						.objectName(fileName)
						.contentType(contentType)
						.contentLanguage(contentLanguage)
						.contentEncoding(contentEncoding)
						.opcMeta(metadata)
						.build();
		UploadRequest uploadDetails =
				UploadRequest.builder(uploadFile).allowOverwrite(true).build(request);

		UploadResponse response = uploadManager.upload(uploadDetails);
		log.info("Upload Success. File : {}", fileName);
		removeNewFile(uploadFile);
		objectStorage.close();
		return fileName;
	}

	// 로컬에 파일 업로드 해서 convert
	private Optional<File> convert(MultipartFile file) throws IOException {
		File convertFile = new File(System.getProperty("user.home") + "/" + file.getOriginalFilename());
		if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
			try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}

	// 로컬에 저장된 이미지 지우기
	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("File delete success");
			return;
		}
		log.info("File delete fail");
	}
}
