package com.ridetogether.server.domain.model;

public enum Role {

	//guest와 user라는 권한 목록 생성
	GUEST("ROLE_GUEST", "손님"),
	STUDENT("ROLE_STUDENT", "학생"),
	ADMIN("ROLE_ADMIN", "관리자");

	//각 권한이 가질 필드 선언 + 생성자 주입
	private final String key;
	private final String title;

	Role(String key, String title) {
		this.key = key;
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getKey() {
		return key;
	}
}
