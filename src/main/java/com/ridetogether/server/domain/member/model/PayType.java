package com.ridetogether.server.domain.member.model;

import static java.util.Locale.ENGLISH;

public enum PayType {
	KAKAO_PAY, TOSS_PAY, ACCOUNT_PAY;

	public static PayType fromName(String type) {
		return PayType.valueOf(type.toUpperCase(ENGLISH) + "_PAY");
	}
}
