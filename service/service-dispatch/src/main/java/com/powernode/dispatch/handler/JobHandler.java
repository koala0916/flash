package com.powernode.dispatch.handler;

import com.powernode.dispatch.mapper.XxlJobLogMapper;
import com.powernode.dispatch.service.NewOrderService;
import com.powernode.model.entity.dispatch.XxlJobLog;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 搜索附近符合条件的配送员，然后派单
 */
@Component
public class JobHandler {

    @Resource
    private XxlJobLogMapper xxlJobLogMapper;

    @Resource
    private NewOrderService newOrderService;

    @XxlJob("newOrderTaskHandler")
    public void newOrderTaskHandler() {
        //记录日志  交叉业务
        XxlJobLog xxlJobLog = new XxlJobLog();
        xxlJobLog.setJobId(XxlJobHelper.getJobId());

        long startTime = System.currentTimeMillis();

        try {
            //执行逻辑 查询符合条件的配送员并派单  核心业务只有这1行
            newOrderService.executeTask(XxlJobHelper.getJobId());

            xxlJobLog.setStatus(1);//成功
        } catch (Exception e) {
            xxlJobLog.setStatus(0);//失败
            xxlJobLog.setError(e.getMessage());
            e.printStackTrace();
        }finally {
            long endTime = System.currentTimeMillis();
            int time = (int) (endTime - startTime);
            xxlJobLog.setTimes( time);

            xxlJobLogMapper.insert(xxlJobLog);

        }

    }
}
