package com.ridetogether.server.domain.image.application;

import com.ridetogether.server.domain.image.domain.Image;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

	public Long uploadProfileImg(MultipartFile file, Long memberIdx) throws Exception;
	public Long uploadKakaoQrImg(MultipartFile file, Long memberIdx) throws Exception;

	public String getPublicImgUrl(Long imageIdx, Long memberIdx) throws Exception ;

	public MultipartFile downloadImg(Long imageIdx, Long memberIdx) throws Exception;

	public void deleteImg(Long imageIdx) throws Exception;

}
