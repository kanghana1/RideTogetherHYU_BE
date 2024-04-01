package com.ridetogether.server.global.file;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OracleFileServiceTest {

	@Autowired
	OracleFileService oracleFileService;

	@Test
	public void 이미지_URI_받아오기() throws Exception{
		String publicImgUrl = oracleFileService.getPublicImgUrl("img/test.png", 3L);
		System.out.println("publicImgUrl = " + publicImgUrl);
	}

}