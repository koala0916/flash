package com.powernode.driver.service.impl;


import com.powernode.driver.properties.MinioProperties;
import com.powernode.driver.service.FileService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class FileServiceImpl implements FileService {

    @Resource
    private MinioProperties minioProperties;

    /**
     * 上传录音到minio中
     *
     * 返回的是上传成功的minio中文件的地址   目前minio是社区版，这里可能有问题
     */
    @Override
    public String upload(MultipartFile file) {
        //构建minio的client
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpointUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecreKey())
                .build();

        //判断桶是否存在
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());

            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
            }

            //设置对象存储的名字
            //获取文件后缀名
            String extFileName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            //20250704/uuid.png
            String fileName = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" +
                    UUID.randomUUID().toString().replaceAll("-", "")
                    + "." + extFileName;

            //构建上传实例
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(fileName) //文件名
                    .stream(file.getInputStream(), file.getSize(), -1) //上传的流
                    .build();

            //上传
            minioClient.putObject(putObjectArgs);

            return minioProperties.getEndpointUrl() + "/" + minioProperties.getBucketName() + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
