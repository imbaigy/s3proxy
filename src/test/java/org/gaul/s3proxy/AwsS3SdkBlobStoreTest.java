/*
 * Copyright 2014-2026 Andrew Gaul <andrew@gaul.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gaul.s3proxy;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.Properties;

import org.gaul.s3proxy.awssdk.AwsS3SdkBlobStore;
import org.gaul.s3proxy.blobstore.BlobStore;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.services.s3.model.GetUrlRequest;

public final class AwsS3SdkBlobStoreTest {

    @Test
    public void testProviderMetadata() {
        var properties = new Properties();
        properties.setProperty("jclouds.identity", "test-identity");
        properties.setProperty("jclouds.credential", "test-credential");
        properties.setProperty("jclouds.endpoint", "http://localhost:9000");

        BlobStore blobStore = BlobStores.create("aws-s3", properties);
        assertThat(blobStore).isNotNull();
    }

    @Test
    public void testCustomRegionConfiguration() {
        var properties = new Properties();
        properties.setProperty("jclouds.identity", "test-identity");
        properties.setProperty("jclouds.credential", "test-credential");
        properties.setProperty("jclouds.endpoint", "http://localhost:9000");
        properties.setProperty("jclouds.region", "eu-west-1");

        BlobStore blobStore = BlobStores.create("aws-s3", properties);
        assertThat(blobStore).isNotNull();
    }

    @Test
    public void testTencentCosEndpointDefaultsToVirtualHostedStyle() {
        var properties = new Properties();
        properties.setProperty("jclouds.identity", "test-identity");
        properties.setProperty("jclouds.credential", "test-credential");
        properties.setProperty("jclouds.endpoint",
                "https://cos.ap-guangzhou.myqcloud.com");

        var blobStore = (AwsS3SdkBlobStore) BlobStores.create("aws-s3",
                properties);
        URL url = blobStore.getS3Client().utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket("test-bucket-1250000000")
                        .key("test-key.txt")
                        .build());
        assertThat(url.toExternalForm()).isEqualTo(
                "https://test-bucket-1250000000.cos.ap-guangzhou.myqcloud.com/test-key.txt");
    }

    @Test
    public void testTencentCosFinancialCloudDefaultsToVirtualHostedStyle() {
        var properties = new Properties();
        properties.setProperty("jclouds.identity", "test-identity");
        properties.setProperty("jclouds.credential", "test-credential");
        properties.setProperty("jclouds.endpoint",
                "https://cos.ap-shanghai-fsi.tencentcos.cn");

        var blobStore = (AwsS3SdkBlobStore) BlobStores.create("aws-s3",
                properties);
        URL url = blobStore.getS3Client().utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket("finance-bucket")
                        .key("data.csv")
                        .build());
        assertThat(url.toExternalForm()).isEqualTo(
                "https://finance-bucket.cos.ap-shanghai-fsi.tencentcos.cn/data.csv");
    }

    @Test
    public void testAliyunOssEndpointDefaultsToVirtualHostedStyle() {
        var properties = new Properties();
        properties.setProperty("jclouds.identity", "test-identity");
        properties.setProperty("jclouds.credential", "test-credential");
        properties.setProperty("jclouds.endpoint",
                "https://oss-cn-hangzhou.aliyuncs.com");

        var blobStore = (AwsS3SdkBlobStore) BlobStores.create("aws-s3",
                properties);
        URL url = blobStore.getS3Client().utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket("oss-bucket")
                        .key("image.png")
                        .build());
        assertThat(url.toExternalForm()).isEqualTo(
                "https://oss-bucket.oss-cn-hangzhou.aliyuncs.com/image.png");
    }

    @Test
    public void testAwsEndpointDefaultsToVirtualHostedStyle() {
        var properties = new Properties();
        properties.setProperty("jclouds.identity", "test-identity");
        properties.setProperty("jclouds.credential", "test-credential");
        properties.setProperty("jclouds.endpoint",
                "https://s3.us-east-1.amazonaws.com");

        var blobStore = (AwsS3SdkBlobStore) BlobStores.create("aws-s3",
                properties);
        URL url = blobStore.getS3Client().utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket("aws-bucket")
                        .key("file.txt")
                        .build());
        assertThat(url.toExternalForm()).isEqualTo(
                "https://aws-bucket.s3.us-east-1.amazonaws.com/file.txt");
    }

    @Test
    public void testGenericEndpointDefaultsToPathStyle() {
        var properties = new Properties();
        properties.setProperty("jclouds.identity", "test-identity");
        properties.setProperty("jclouds.credential", "test-credential");
        properties.setProperty("jclouds.endpoint", "http://localhost:9000");

        var blobStore = (AwsS3SdkBlobStore) BlobStores.create("aws-s3",
                properties);
        URL url = blobStore.getS3Client().utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket("my-bucket")
                        .key("my-key")
                        .build());
        assertThat(url.toExternalForm()).isEqualTo(
                "http://localhost:9000/my-bucket/my-key");
    }

    @Test
    public void testExplicitForcePathStyleTrueWithTencentCos() {
        var properties = new Properties();
        properties.setProperty("jclouds.identity", "test-identity");
        properties.setProperty("jclouds.credential", "test-credential");
        properties.setProperty("jclouds.endpoint",
                "https://cos.ap-guangzhou.myqcloud.com");
        properties.setProperty("s3proxy.aws-s3.force-path-style", "true");

        var blobStore = (AwsS3SdkBlobStore) BlobStores.create("aws-s3",
                properties);
        URL url = blobStore.getS3Client().utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket("test-bucket-1250000000")
                        .key("test-key.txt")
                        .build());
        assertThat(url.toExternalForm()).isEqualTo(
                "https://cos.ap-guangzhou.myqcloud.com/test-bucket-1250000000/test-key.txt");
    }

    @Test
    public void testExplicitForcePathStyleFalseWithGenericEndpoint() {
        var properties = new Properties();
        properties.setProperty("jclouds.identity", "test-identity");
        properties.setProperty("jclouds.credential", "test-credential");
        properties.setProperty("jclouds.endpoint", "http://localhost:9000");
        properties.setProperty("s3proxy.aws-s3.force-path-style", "false");

        var blobStore = (AwsS3SdkBlobStore) BlobStores.create("aws-s3",
                properties);
        URL url = blobStore.getS3Client().utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket("my-bucket")
                        .key("my-key")
                        .build());
        assertThat(url.toExternalForm()).isEqualTo(
                "http://my-bucket.localhost:9000/my-key");
    }

    @Test
    public void testPathStyleAliasProperty() {
        var properties = new Properties();
        properties.setProperty("jclouds.identity", "test-identity");
        properties.setProperty("jclouds.credential", "test-credential");
        properties.setProperty("jclouds.endpoint", "http://localhost:9000");
        properties.setProperty("s3proxy.aws-s3.path-style", "false");

        var blobStore = (AwsS3SdkBlobStore) BlobStores.create("aws-s3",
                properties);
        URL url = blobStore.getS3Client().utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket("my-bucket")
                        .key("my-key")
                        .build());
        assertThat(url.toExternalForm()).isEqualTo(
                "http://my-bucket.localhost:9000/my-key");
    }
}
