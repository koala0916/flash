package com.powernode.dispatch.service;

import com.powernode.model.vo.dispatch.NewOrderTaskVo;
import com.powernode.model.vo.order.NewOrderDataVo;

import java.util.List;

public interface NewOrderService {

    Long addAndStartTask(NewOrderTaskVo newOrderTaskVo);

    Boolean executeTask(Long jobId);

    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    Boolean clearNewOrderQueueData(Long driverId);
}
