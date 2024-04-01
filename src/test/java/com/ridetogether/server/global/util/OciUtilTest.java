package com.ridetogether.server.global.util;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.ConfigFileReader.ConfigFile;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.ListObjects;
import com.oracle.bmc.objectstorage.model.ObjectSummary;
import com.oracle.bmc.objectstorage.requests.GetBucketRequest;
import com.oracle.bmc.objectstorage.requests.GetBucketRequest.Fields;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetBucketResponse;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OciUtilTest {

	@Autowired
	ObjectStorage objectStorage;

	@Autowired
	UploadManager uploadManager;

	@Test
	public void Test() throws IOException {
		final String bucket = "RideTogetherHYU_Bucket";

		System.out.println("Getting the namespace.");
		GetNamespaceResponse namespaceResponse = objectStorage.getNamespace(GetNamespaceRequest.builder().build());

		String namespaceName = namespaceResponse.getValue();
		System.out.println("namespaceName = " + namespaceName);

		System.out.println("Creating Get bucket request");
		List<Fields> fieldsList = new ArrayList<>(2);
		fieldsList.add(GetBucketRequest.Fields.ApproximateCount);
		fieldsList.add(GetBucketRequest.Fields.ApproximateSize);
		GetBucketRequest request =
				GetBucketRequest.builder()
						.namespaceName(namespaceName)
						.bucketName(bucket)
						.fields(fieldsList)
						.build();

		System.out.println("Fetching bucket details");
		GetBucketResponse response = objectStorage.getBucket(request);

		System.out.println("Bucket Name : " + response.getBucket().getName());
		System.out.println("Bucket Compartment : " + response.getBucket().getCompartmentId());
		System.out.println(
				"The Approximate total number of objects within this bucket : "
						+ response.getBucket().getApproximateCount());
		System.out.println(
				"The Approximate total size of objects within this bucket : "
						+ response.getBucket().getApproximateSize());

	}

	@Test
	public void UploadObjectTest() throws Exception {
		String bucketName = "RideTogetherHYU_Bucket";
		String namespaceName = "axjoaeuyezzj";
		String objectName = "img/test.png";
		Map<String, String> metadata = null;
		String contentType = "image/png";

		String contentEncoding = null;
		String contentLanguage = null;

		File body = new File("C:\\Users\\dh101\\Desktop\\img\\git.png");

		PutObjectRequest request =
				PutObjectRequest.builder()
						.bucketName(bucketName)
						.namespaceName(namespaceName)
						.objectName(objectName)
						.contentType(contentType)
						.contentLanguage(contentLanguage)
						.contentEncoding(contentEncoding)
						.opcMeta(metadata)
						.build();


		UploadRequest uploadDetails =
				UploadRequest.builder(body).allowOverwrite(true).build(request);

		UploadResponse response = uploadManager.upload(uploadDetails);
		System.out.println(response);

		objectStorage.close();
	}

	@Test
	public void ObjectReadTest() throws Exception{
		ConfigFile config = ConfigFileReader.parse("~/.oci/config", "DEFAULT");

		AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(config);

		ObjectStorage client = new ObjectStorageClient(provider);
		client.setRegion(Region.AP_CHUNCHEON_1);
		String bucketName = "RideTogetherHYU_Bucket";
		String namespaceName = "axjoaeuyezzj";

		ListObjectsRequest request =
				ListObjectsRequest.builder()
						.namespaceName(namespaceName)
						.bucketName(bucketName)
						.fields("size, md5, timeCreated, timeModified")
//						.prefix("test") // 파일명 지정
						.build();
		ListObjectsResponse response = client.listObjects(request);

		ListObjects list = response.getListObjects();
		List<ObjectSummary> objectList = list.getObjects();


		for(ObjectSummary x : objectList) {
			System.out.println("====================");
			System.out.println("@@@@@@@@@@@@@@@@@ getName : " + x.getName());
			System.out.println("@@@@@@@@@@@@@@@@@ getArchivalState : " + x.getArchivalState());
			System.out.println("@@@@@@@@@@@@@@@@@ getSize : " + x.getSize());
			System.out.println("@@@@@@@@@@@@@@@@@ getTimeCreated : " + x.getTimeCreated());
			System.out.println("@@@@@@@@@@@@@@@@@ getTimeModified : " + x.getTimeModified());
		}

		System.out.println(response.getListObjects());
		client.close();
	}



}
