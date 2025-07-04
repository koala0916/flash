package com.powernode.driver.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.driver.config.TencentProperties;
import com.powernode.driver.service.CiService;
import com.powernode.driver.service.CosService;
import com.powernode.model.vo.driver.CosUploadVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CosServiceImpl implements CosService {

    @Resource
    private TencentProperties tenantProperties;

    @Resource
    private CiService ciService;

    /**
     * 将文件上传到cos
     * @param file
     * @return
     */
    @Override
    public CosUploadVo upload(MultipartFile file, String path) {
        COSClient cosClient = getCosClient();

        //构建元数据  描述了我们上传的文件的数据
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());//文件大小
        objectMetadata.setContentEncoding("UTF-8");
        objectMetadata.setContentType(file.getContentType());//文件类型

        //设置上传文件的名字  lyf.png
        //获取后缀名
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        //这里文件的路径可以按照年月日来创建
        String uploadPath = "/driver/" + path + "/" + UUID.randomUUID().toString().replaceAll("-", "") + fileType;

        //上传
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(tenantProperties.getBucketPrivate(), uploadPath, file.getInputStream(), objectMetadata);

            putObjectRequest.setStorageClass(StorageClass.Standard);

            cosClient.putObject(putObjectRequest);//上传
            cosClient.shutdown();

            //审核图片
            Boolean imageAuditing = ciService.imageAuditing(uploadPath);
            if (!imageAuditing) {
                //违规 删除该图片
                cosClient.deleteObject(tenantProperties.getBucketPrivate(), uploadPath);
                throw new PowerException(ResultCodeEnum.IMAGE_AUDITION_FAIL);
            }


            CosUploadVo cosUploadVo = new CosUploadVo();
            cosUploadVo.setUrl(uploadPath);

            cosUploadVo.setShowUrl(getImageUrl(uploadPath));//设置图片回显的地址
            return cosUploadVo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取图片的回显地址
     * @param path
     * @return
     */
    @Override
    public String getImageUrl(String path) {
        COSClient cosClient = getCosClient();

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(tenantProperties.getBucketPrivate(), path, HttpMethodName.GET);

        //设置失效时间
        Date expiration = new DateTime().plusMinutes(20).toDate();
        request.setExpiration(expiration);

        //生成回显地址
        URL url = cosClient.generatePresignedUrl(request);

        cosClient.shutdown();

        return url.toString();
    }


    /**
     * 参考了cos开发文档获取了cosClient对象
     * @return
     */
    private COSClient getCosClient() {
        COSCredentials cred = new BasicCOSCredentials(tenantProperties.getSecretId(), tenantProperties.getSecretKey());

        Region region = new Region(tenantProperties.getRegion());

        ClientConfig clientConfig = new ClientConfig(region);

        //设置https协议
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);

        return cosClient;
    }



}
