package com.ridetogether.server.domain.image.model;

import static java.util.Locale.ENGLISH;


public enum ImageType {
	KAKAO, PROFILE;

	public static ImageType fromName(String type) {
		return ImageType.valueOf(type.toUpperCase(ENGLISH));
	}
}
