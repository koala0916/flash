package com.powernode.dispatch.client;

import com.alibaba.fastjson.JSONObject;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.dispatch.config.XxlJobClientConfig;
import com.powernode.model.entity.dispatch.XxlJobInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 手动将定时任务添加 删除等操作到xxl-job
 */
@Slf4j
@Component
public class XxlJobClient {

    @Resource
    private XxlJobClientConfig xxlJobClientConfig;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 添加定时任务
     */
    public Long addJob(String executorHandler, String param, String cron, String desc) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();

        xxlJobInfo.setJobGroup(xxlJobClientConfig.getJobGroupId());
        xxlJobInfo.setJobDesc(desc);//描述
        xxlJobInfo.setAuthor("pat");
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(cron);
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(executorHandler);
        xxlJobInfo.setExecutorParam(param);
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setMisfireStrategy("FIRE_ONCE_NOW");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);

        //发送请求注册任务
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);//设置json格式
        //构建http实体
        HttpEntity<XxlJobInfo> entity = new HttpEntity<>(xxlJobInfo, httpHeaders);

        //获取请求发送的地址
        String addUrl = xxlJobClientConfig.getAddUrl();

        //给上面地址发送请求
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(addUrl, entity, JSONObject.class);

        //判断响应结果

        if(response.getStatusCode().value() == 200 ){
            //成功 返回任务编号
            return response.getBody().getLong("content");
        }

        throw new PowerException(ResultCodeEnum.XXL_JOB_ERROR);
    }

    //启动定时任务
    public Boolean startJob(Long jobId) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(jobId.intValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        //获取请求发送的地址
        String url = xxlJobClientConfig.getStartJobUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            log.info("启动xxl执行任务成功:{},返回信息:{}", jobId, response.getBody().toJSONString());
            return true;
        }
        log.info("启动xxl执行任务失败:{},返回信息:{}", jobId, response.getBody().toJSONString());
        throw new PowerException(ResultCodeEnum.XXL_JOB_ERROR);
    }

    public Boolean stopJob(Long jobId) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(jobId.intValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        String url = xxlJobClientConfig.getStopJobUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            log.info("停止xxl执行任务成功:{},返回信息:{}", jobId, response.getBody().toJSONString());
            return true;
        }
        log.info("停止xxl执行任务失败:{},返回信息:{}", jobId, response.getBody().toJSONString());
        throw new PowerException(ResultCodeEnum.XXL_JOB_ERROR);
    }

    public Boolean removeJob(Long jobId) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(jobId.intValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        String url = xxlJobClientConfig.getRemoveUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            log.info("删除xxl执行任务成功:{},返回信息:{}", jobId, response.getBody().toJSONString());
            return true;
        }
        log.info("删除xxl执行任务失败:{},返回信息:{}", jobId, response.getBody().toJSONString());
        throw new PowerException(ResultCodeEnum.XXL_JOB_ERROR);
    }

    public Long addAndStart(String executorHandler, String param, String corn, String desc) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setJobGroup(xxlJobClientConfig.getJobGroupId());
        xxlJobInfo.setJobDesc(desc);
        xxlJobInfo.setAuthor("pat");
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(corn);
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(executorHandler);
        xxlJobInfo.setExecutorParam(param);
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setMisfireStrategy("FIRE_ONCE_NOW");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<XxlJobInfo> request = new HttpEntity<>(xxlJobInfo, headers);

        String url = xxlJobClientConfig.getAddAndStartUrl();
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, request, JSONObject.class);
        if(response.getStatusCode().value() == 200 && response.getBody().getIntValue("code") == 200) {
            log.info("增加并开始执行xxl任务成功,返回信息:{}", response.getBody().toJSONString());
            //content为任务id
            return response.getBody().getLong("content");
        }
        log.info("增加并开始执行xxl任务失败:{}", response.getBody().toJSONString());
        throw new PowerException(ResultCodeEnum.XXL_JOB_ERROR);
    }
}
