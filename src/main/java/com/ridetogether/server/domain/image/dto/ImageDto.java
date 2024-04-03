package com.ridetogether.server.domain.image.dto;

import lombok.Builder;
import lombok.Data;

public class ImageDto {

	@Data
	@Builder
	public static class ImageUriResponseDto {
		private String accessUri;
	}

}
