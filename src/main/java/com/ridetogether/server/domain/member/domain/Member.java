package com.ridetogether.server.domain.member.domain;

import com.ridetogether.server.domain.model.ActiveState;
import com.ridetogether.server.domain.model.Bank;
import com.ridetogether.server.domain.model.Gender;
import com.ridetogether.server.domain.model.PayType;
import com.ridetogether.server.domain.model.StudentStatus;
import com.ridetogether.server.domain.model.Role;
import com.ridetogether.server.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "Member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_idx")
	private Long idx;

	private String memberId;

	private String name;

	private String password;

	@Column(nullable = false, unique = true)
	private String nickName;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String profileImage;

	private String kakaoPayUrl;

	private String kakaoQrImageUrl;

	private String account;

	@Enumerated(EnumType.STRING)
	private Bank accountBank;

	@Enumerated(EnumType.STRING)
	private List<PayType> payTypes;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Enumerated(EnumType.STRING)
	private StudentStatus studentStatus;

	@Enumerated(EnumType.STRING)
	private ActiveState activeState;

	@Column(length = 1000)
	private String refreshToken;

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void destroyRefreshToken() {
		this.refreshToken = null;
	}

	public boolean isStudent() {
		return this.role == Role.STUDENT;
	}

	public boolean isAdmin() {
		return this.role == Role.ADMIN;
	}
}
