package com.powernode.driver.service.impl;


import com.alibaba.nacos.common.codec.Base64;
import com.powernode.driver.config.TencentProperties;
import com.powernode.driver.service.CiService;
import com.powernode.model.vo.order.TextAuditingVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ciModel.auditing.*;
import com.qcloud.cos.region.Region;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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


    /**
     * 文本审核
     */
    @Override
    public TextAuditingVo textAuditing(String content) {
        if (!StringUtils.hasText(content)) {
            TextAuditingVo textAuditingVo = new TextAuditingVo();
            textAuditingVo.setResult("0");
            return textAuditingVo;
        }

        COSClient cosClient = getCosClient();

        //构建文本请求对象
        TextAuditingRequest request = new TextAuditingRequest();
        request.setBucketName(tenantProperties.getBucketPrivate());

        //将文本内容转成base64
        byte[] encoder = Base64.encodeBase64(content.getBytes());
        String contentBase64 = new String(encoder);

        request.getInput().setContent(contentBase64);

        //设置全部场景
        request.getConf().setDetectType("all");

        //获取响应结果
        TextAuditingResponse response = cosClient.createAuditingTextJobs(request);

        AuditingJobsDetail jobsDetail = response.getJobsDetail();

        TextAuditingVo textAuditingVo = new TextAuditingVo();

        if ("success".equalsIgnoreCase(jobsDetail.getState())) {
            String result = jobsDetail.getResult();

            StringBuffer keywords = new StringBuffer();
            List<SectionInfo> sectionInfoList = jobsDetail.getSectionList();

            for (SectionInfo info : sectionInfoList) {
                String pornInfoKeyword = info.getPornInfo().getKeywords();
                String illegalInfoKeyword = info.getIllegalInfo().getKeywords();
                String abuseInfoKeyword = info.getAbuseInfo().getKeywords();
                if (pornInfoKeyword.length() > 0) {
                    keywords.append(pornInfoKeyword).append(",");
                }
                if (illegalInfoKeyword.length() > 0) {
                    keywords.append(illegalInfoKeyword).append(",");
                }
                if (abuseInfoKeyword.length() > 0) {
                    keywords.append(abuseInfoKeyword).append(",");
                }
            }
            textAuditingVo.setResult(result);
            textAuditingVo.setKeywords(keywords.toString());
        }

        return textAuditingVo;
    }
}
