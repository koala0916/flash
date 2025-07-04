package com.powernode.driver.service.impl;


import com.powernode.driver.config.TencentProperties;
import com.powernode.driver.service.CiService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingRequest;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingResponse;
import com.qcloud.cos.region.Region;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CiServiceImpl implements CiService {

    @Resource
    private TencentProperties tenantProperties;

    /**
     * 获取cosclient对象
     *
     * @return
     */
    private COSClient getCosClient() {
        BasicCOSCredentials cred = new BasicCOSCredentials(tenantProperties.getSecretId(), tenantProperties.getSecretKey());

        ClientConfig clientConfig = new ClientConfig(new Region(tenantProperties.getRegion()));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }


    /**
     * 审核上传的图片
     */
    @Override
    public Boolean imageAuditing(String path) {
        COSClient cosClient = getCosClient();

        //创建请求
        ImageAuditingRequest request = new ImageAuditingRequest();

        //设置bucket
        request.setBucketName(tenantProperties.getBucketPrivate());
        //设置图片的位置
        request.setObjectKey(path);

        //获取响应对象
        ImageAuditingResponse response = cosClient.imageAuditing(request);

        cosClient.shutdown();

        //用于返回该审核场景的审核结果，返回值：0：正常。1：确认为当前场景的违规内容。2：疑似为当前场景的违规内容。
        if (!response.getPornInfo().getHitFlag().equals("0")
                || !response.getAdsInfo().getHitFlag().equals("0")
                || !response.getTerroristInfo().getHitFlag().equals("0")
                || !response.getPoliticsInfo().getHitFlag().equals("0")
        ) {
            return false;
        }

        return true;
    }
}
