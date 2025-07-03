package com.powernode;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
public class MinIoTest {

    @Test
    public void testMinIo() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
     //指定minio的地址端口等信息
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://192.168.137.1:9000")  //端点
                .credentials("admin", "admin123456")
                .build();

        //判断桶是否存在
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket("powernode").build());

        if (!bucketExists) {
            //不存在则创建
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("powernode").build());
        }

        //上传文件
        FileInputStream fis = new FileInputStream("C:\\Users\\Administrator\\Desktop\\lyf.png");

        //构建上传对象
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket("powernode")   //文件上传到哪个桶中
                .object("lyf.png")    //桶中的文件名
                .stream(fis, fis.available(), -1)//文件输入流
                .build();

        //上传文件
        minioClient.putObject(putObjectArgs);
    }
}
