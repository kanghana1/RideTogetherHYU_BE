package com.ridetogether.server.domain.image.application;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.ConfigFileReader.ConfigFile;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails.AccessType;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.DeletePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.GetPreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.oracle.bmc.objectstorage.responses.GetPreauthenticatedRequestResponse;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;
import com.ridetogether.server.domain.image.dao.ImageRepository;
import com.ridetogether.server.domain.image.domain.Image;
import com.ridetogether.server.domain.image.model.ImageType;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.apiPayload.code.status.ErrorStatus;
import com.ridetogether.server.global.apiPayload.exception.handler.ErrorHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OracleImageService implements ImageService {

	private final MemberRepository memberRepository;

	private final ImageRepository imageRepository;



	private static final String BUCKET_NAME = "RideTogetherHYU_Bucket";
	private static final String BUCKET_NAME_SPACE = "axjoaeuyezzj";
	private static final String PROFILE_IMG_DIR = "profile/";
	public static final String DEFAULT_URI_PREFIX = "https://" + BUCKET_NAME_SPACE + ".objectstorage."
			+ Region.AP_CHUNCHEON_1.getRegionId() + ".oci.customer-oci.com";
	private static final String KAKAO_IMG_DIR = "kakao/";

	private static final long PRE_AUTH_EXPIRE_MINUTE = 20;

	public ObjectStorage getClient() throws Exception {
		ConfigFile config = ConfigFileReader.parse("~/.oci/config", "DEFAULT");

		AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(config);

		return ObjectStorageClient.builder()
				.region(Region.AP_CHUNCHEON_1)
				.build(provider);
	}

	public UploadManager getManager(ObjectStorage client) throws Exception {
		UploadConfiguration configuration = UploadConfiguration.builder()
				.allowMultipartUploads(true)
				.allowParallelUploads(true)
				.build();
		return new UploadManager(client, configuration);
	}

	public UploadConfiguration getUploadConfiguration() {
		//upload object
		return UploadConfiguration.builder()
				.allowMultipartUploads(true)
				.allowParallelUploads(true)
				.build();
	}

	@Override
	public Long uploadProfileImg(MultipartFile file, Long memberIdx) throws Exception {
		File uploadFile = convert(file)  // 파일 변환할 수 없으면 에러
				.orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
		String fileDir = memberIdx + "/" + PROFILE_IMG_DIR;
		Member member = memberRepository.findByIdx(memberIdx).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		return upload(uploadFile, fileDir, member);
	}
	@Override
	public Long uploadKakaoQrImg(MultipartFile file, Long memberIdx) throws Exception{
		File uploadFile = convert(file)  // 파일 변환할 수 없으면 에러
				.orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
		String fileDir = memberIdx + "/" + KAKAO_IMG_DIR;
		Member member = memberRepository.findByIdx(memberIdx).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		return upload(uploadFile, fileDir, member);
	}

	@Override
	public String getPublicImgUrl(Long imageIdx, Long memberIdx) throws Exception {
		ObjectStorage client = getClient();
		Image img = imageRepository.findImageByIdx(imageIdx).orElseThrow(
				() -> new ErrorHandler(ErrorStatus.IMAGE_NOT_FOUND)
		);
		AuthenticatedRequest authenticatedRequest = getPreAuth(img.getImgUrl());
		
		// 응답 로그
		GetPreauthenticatedRequestRequest request =
				GetPreauthenticatedRequestRequest.builder()
						.namespaceName(BUCKET_NAME_SPACE)
						.bucketName(BUCKET_NAME)
						.parId(authenticatedRequest.getAuthenticateId())	//parId 필수
						.build();
		GetPreauthenticatedRequestResponse response = client.getPreauthenticatedRequest(request);
		log.info("response = " + response);

		Member member = memberRepository.findByIdx(memberIdx).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
		addAccessUriToMember(member, authenticatedRequest, img.getImgUrl());

		log.info("PublicImgUrl 발급에 성공하였습니다 : {}", DEFAULT_URI_PREFIX + authenticatedRequest.getAccessUri());
		client.close();
		return DEFAULT_URI_PREFIX +authenticatedRequest.getAccessUri();
	}

	@Override
	public MultipartFile downloadImg(Long imageIdx, Long memberIdx) throws Exception{
		return null;
	}

	// 버킷에서 이미지와 인증정보 삭제
	@Override
	public void deleteImg(Image image) throws Exception {
		ObjectStorage client = getClient();
		DeleteObjectRequest request =
				DeleteObjectRequest.builder()
						.bucketName(BUCKET_NAME)
						.namespaceName(BUCKET_NAME_SPACE)
						.objectName(image.getImgUrl())
						.build();

		deletePreAuth(image.getParId());
		imageRepository.delete(image);

		client.deleteObject(request);
		client.close();
	}

	// 오라클 버킷으로 파일 업로드
	public Long upload(File uploadFile, String dirName, Member member) throws Exception {
		ObjectStorage client = getClient();
		UploadManager uploadManager = getManager(client);

		String fileName = dirName + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
		String contentType = "img/" + fileName.substring(fileName.length() - 3); // PNG, JPG 만 가능함
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

		client.close();
		removeNewFile(uploadFile);
		return saveImageToMember(member, fileName);
	}

	private Long saveImageToMember(Member member, String fileName) {
		Image image = Image.builder()
				.member(member)
				.imgUrl(fileName)
				.imageType(ImageType.PROFILE)
				.build();
		if (fileName.contains(KAKAO_IMG_DIR)) {
			image.updateImageType(ImageType.KAKAO);
		}
		imageRepository.save(image);
		member.getImages().add(image);
		return image.getIdx();
	}

	private void addAccessUriToMember(Member member, AuthenticatedRequest request, String imgUrl) {
		List<Image> images = member.getImages();
		for (Image img : images) {
			if (img.getImgUrl().equals(imgUrl)) {
				img.updateAccessUri(DEFAULT_URI_PREFIX + request.getAccessUri());
				img.updateParId(request.authenticateId);
			}
		}
	}


	// 로컬에 파일 업로드 해서 convert
	private Optional<File> convert(MultipartFile file) throws IOException {
		File convertFile = new File(System.getProperty("user.home") + "/rideTogetherDummy/" + UUID.randomUUID() +file.getOriginalFilename());
		if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
			try (FileOutputStream fos = new FileOutputStream(
					convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}

	// 로컬에 저장된 이미지 지우기
	public void removeNewFile(File targetFile) {
		log.info("@@@@@@@@ 지울 대상 파일 이름"+targetFile.getName());
		log.info("@@@@@@@@ 지울 대상 파일 경로"+targetFile.getPath());
		if (targetFile.exists()) {
			if (targetFile.delete()) {
				log.info("@@@@@@@@ File delete success");
				return;
			}
			log.info("@@@@@@@@ File delete fail.");
		}
		log.info("@@@@@@@@ File not exist.");
	}

	public AuthenticatedRequest getPreAuth(String imgUrl) throws Exception{
		ObjectStorage client = getClient();

		Calendar cal = Calendar.getInstance();
		cal.set(2024, Calendar.DECEMBER, 30);

		Date expireTime = cal.getTime();

		log.info("권한을 얻어오기 위해 시도중입니다. 파일 이름 : {}", imgUrl);
		CreatePreauthenticatedRequestDetails details =
				CreatePreauthenticatedRequestDetails.builder()
						.accessType(AccessType.ObjectReadWrite)
						.objectName(imgUrl)
						.timeExpires(expireTime)
						.name(imgUrl)
						.build();

		CreatePreauthenticatedRequestRequest request =
				CreatePreauthenticatedRequestRequest.builder()
						.namespaceName(BUCKET_NAME_SPACE)
						.bucketName(BUCKET_NAME)
						.createPreauthenticatedRequestDetails(details)
						.build();

		CreatePreauthenticatedRequestResponse response = client.createPreauthenticatedRequest(request);
		client.close();
		return AuthenticatedRequest.builder()
				.authenticateId(response.getPreauthenticatedRequest().getId())
				.accessUri(response.getPreauthenticatedRequest().getAccessUri())
				.build();
	}

	private void deletePreAuth(String parId) throws Exception {
		ObjectStorage client = getClient();
		DeletePreauthenticatedRequestRequest request =
				DeletePreauthenticatedRequestRequest.builder()
						.namespaceName(BUCKET_NAME_SPACE)
						.bucketName(BUCKET_NAME)
						.parId(parId)
						.build();

		client.deletePreauthenticatedRequest(request);
		client.close();
	}

	@Data
	@Builder
	static class AuthenticatedRequest {
		String accessUri;
		String authenticateId;
		String imgUrl;
	}


}
