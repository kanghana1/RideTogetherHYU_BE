package com.ridetogether.server.domain.member.model;

import java.util.Arrays;

public enum Bank {
	KAKAO_BANK("카카오뱅크", "090"),
	KOOKMIN_BANK("국민은행", "004"),
	INDUSTRIAL_KOREA_BANK("기업은행", "003"),
	NH_BANK("농협은행", "011"),
	SHINHAN_BANK("신한은행", "088"),
	KDB_BANK("산업은행", "002"),
	WOORI_BANK("우리은행", "002"),
	KOREA_CITY_BANK("한국씨티은행", "027"),
	HANA_BANK("하나은행", "081"),
	SC_BANK("SC제일은행", "023"),
	KYONGNAM_BANK("경남은행", "039"),
	KWANGJU_BANK("광주은행", "034"),
	DAEHU_BANK("대구은행", "031"),
	DEUTSCHE_BANK("도이치은행", "055"),
	BUSAN_BANK("부산은행", "032"),
	SUHYUP_BANK("수협은행", "007"),
	SINHYUP_BANK("신협중앙회", "048"),
	KOREA_POST_BANK("우체국", "071"),
	JEONBUK_BANK("전북은행", "037"),
	JEJU_BANK("제주은행", "035"),
	K_BANK("케이뱅크", "089"),
	TOSS_BANK("토스뱅크", "092"),
	EMPTY("없음", null);

	private String title;
	private String code;

	Bank(String title, String code) {
		this.title = title;
		this.code = code;
	}

	public static Bank getBank(String name) {
		return Arrays.stream(values())
				.filter(x -> x.title.equals(name))
				.findAny()
				.orElse(EMPTY);
	}

	public String getTitle() {
		return title;
	}

	public String getCode() {
		return code;
	}

	public static Bank fromName(String name) {
		return Arrays.stream(values())
				.filter(value -> value.title.equals(name))
				.findAny()
				.orElse(EMPTY);
	}
}
