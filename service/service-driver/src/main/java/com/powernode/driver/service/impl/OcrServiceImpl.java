package com.powernode.driver.service.impl;


import com.alibaba.nacos.common.codec.Base64;
import com.powernode.driver.config.TencentProperties;
import com.powernode.driver.service.CosService;
import com.powernode.driver.service.OcrService;
import com.powernode.model.vo.driver.CosUploadVo;
import com.powernode.model.vo.driver.IdCardOcrVo;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OcrServiceImpl implements OcrService {

    @Resource
    private TencentProperties tenantProperties;

    @Resource
    private CosService cosService;

    /**
     * 身份证识别
     */
    @Override
    public IdCardOcrVo idCardOcr(MultipartFile file) {

        try {
            //将图片转成base64字符串
            byte[] bytes = Base64.encodeBase64(file.getBytes());
            String idCardBase64 = new String(bytes);

            //参考腾讯云api  创建凭证对象
            Credential credential = new Credential(tenantProperties.getSecretId(), tenantProperties.getSecretKey());

            OcrClient ocrClient = new OcrClient(credential, tenantProperties.getRegion());

            //构建请求对象
            IDCardOCRRequest request = new IDCardOCRRequest();

            request.setImageBase64(idCardBase64);
            //将身份证图片传给腾讯云，它会返回一个响应
            IDCardOCRResponse response = ocrClient.IDCardOCR(request);


            IdCardOcrVo idCardOcrVo = new IdCardOcrVo();

            if (StringUtils.hasText(response.getName())) {
                //身份证正面
                idCardOcrVo.setName(response.getName());
                idCardOcrVo.setGender("男".equals(response.getSex()) ? "1" : "2");
                idCardOcrVo.setBirthday(DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(response.getBirth()).toDate());
                idCardOcrVo.setIdcardAddress(response.getAddress());
                idCardOcrVo.setIdcardNo(response.getIdNum());

                //将身份证传入cos
                CosUploadVo cosUploadVo = cosService.upload(file, "idCard");

                idCardOcrVo.setIdcardFrontUrl(cosUploadVo.getUrl());//设置身份证的上传地址
                idCardOcrVo.setIdcardFrontShowUrl(cosUploadVo.getShowUrl());//设置身份证的回显地址
            }else {
                //反面
                //获取有效期 2020.01.01-2030.01.01
                String idCardExpireString = response.getValidDate().split("-")[1];

                idCardOcrVo.setIdcardExpire(DateTimeFormat.forPattern("yyyy.MM.dd").parseDateTime(idCardExpireString).toDate());

                //上传反面
                CosUploadVo cosUploadVo = cosService.upload(file, "idCard");

                idCardOcrVo.setIdcardBackUrl(cosUploadVo.getUrl());
                idCardOcrVo.setIdcardBackShowUrl(cosUploadVo.getShowUrl());
            }

            return idCardOcrVo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
    }
}
