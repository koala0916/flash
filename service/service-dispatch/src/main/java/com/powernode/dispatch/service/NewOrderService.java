package com.powernode.dispatch.service;

import com.powernode.model.vo.dispatch.NewOrderTaskVo;

public interface NewOrderService {

    Long addAndStartTask(NewOrderTaskVo newOrderTaskVo);

    Boolean executeTask(Long jobId);
}
