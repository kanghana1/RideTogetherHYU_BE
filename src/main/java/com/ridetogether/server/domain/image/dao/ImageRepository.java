package com.ridetogether.server.domain.image.dao;

import com.ridetogether.server.domain.image.domain.Image;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

	Optional<Image> findImageByIdx(Long idx);
	Optional<Image> findImageByImgUrl(String imgUrl);
}
