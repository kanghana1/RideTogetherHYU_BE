package com.ridetogether.server.domain.image.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ridetogether.server.domain.image.model.ImageType;
import com.ridetogether.server.domain.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "Image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_idx")
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_idx")
	@JsonIgnore
	private Member member;

	private String accessUri;

	private String imgUrl;

	private String parId;

	@Enumerated(EnumType.STRING)
	private ImageType imageType;

	public void updateAccessUri(String accessUri) {
		this.accessUri = accessUri;
	}

	public void updateParId(String parId) {
		this.parId = parId;
	}

	public void updateImageType(ImageType imageType) {
		this.imageType = imageType;
	}

}
