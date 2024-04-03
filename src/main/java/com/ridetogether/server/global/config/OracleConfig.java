//package com.ridetogether.server.global.config;
//
//import com.oracle.bmc.ConfigFileReader;
//import com.oracle.bmc.ConfigFileReader.ConfigFile;
//import com.oracle.bmc.Region;
//import com.oracle.bmc.auth.AuthenticationDetailsProvider;
//import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
//import com.oracle.bmc.objectstorage.ObjectStorage;
//import com.oracle.bmc.objectstorage.ObjectStorageClient;
//import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
//import com.oracle.bmc.objectstorage.transfer.UploadManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OracleConfig {
//
//	@Bean
//	public ObjectStorage objectStorage() throws Exception{
//		ConfigFile config = ConfigFileReader.parse("~/.oci/config", "DEFAULT");
//
//		AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(config);
//
//		return ObjectStorageClient.builder()
//				.region(Region.AP_CHUNCHEON_1)
//				.build(provider);
//	}
//
//	@Bean
//	public UploadConfiguration uploadConfiguration() {
//		//upload object
//		return UploadConfiguration.builder()
//						.allowMultipartUploads(true)
//						.allowParallelUploads(true)
//						.build();
//	}
//
//	@Bean
//	public UploadManager uploadManager() throws Exception{
//		return new UploadManager(objectStorage(), uploadConfiguration());
//	}
//}
