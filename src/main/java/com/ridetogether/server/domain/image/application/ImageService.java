package com.ridetogether.server.domain.image.application;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public interface ImageService {

	public Long uploadProfileImg(MultipartFile file, Long memberIdx) throws Exception;
	public Long uploadKakaoQrImg(MultipartFile file, Long memberIdx) throws Exception;

	public String getPublicImgUrl(String imgUrl, Long memberIdx) throws Exception ;

	public MultipartFile downloadImg(String imgUrl, Long memberIdx) throws Exception;

	public void deleteImg(String imgUrl) throws Exception;

}
