package com.ridetogether.server.global.file;

import java.io.IOException;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public interface FileService {

	public String uploadProfileImg(MultipartFile file, Long memberId) throws Exception;
	public String uploadKakaoQrImg(MultipartFile file, Long memberId) throws Exception;

	public String getPublicImgUrl(String imgUrl, Long memberId) throws Exception ;

	public MultipartFile downloadImg(String imgUrl, Long memberId) throws Exception;

	public void deleteImg(String imgUrl) throws Exception;

}
