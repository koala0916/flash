package com.powernode.customer.service.impl;


import com.powernode.customer.service.OrderService;
import com.powernode.map.client.MapFeignClient;
import com.powernode.model.form.customer.ExpectOrderForm;
import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.form.rules.FeeRuleRequestForm;
import com.powernode.model.vo.customer.ExpectOrderVo;
import com.powernode.model.vo.map.DrivingLineVo;
import com.powernode.model.vo.rules.FeeRuleResponseVo;
import com.powernode.rules.client.FeeRuleFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Resource
    private MapFeignClient mapFeignClient;

    @Resource
    private FeeRuleFeignClient feeRuleFeignClient;

    /**
     * 预估订单费用和路线
     * @param exerciseOrderForm
     * @return
     */
    @Override
    public ExpectOrderVo expectOrder(ExpectOrderForm exerciseOrderForm) {
        //计算配送路线
        CalculateDrivingLineForm calculateDrivingLineForm = new CalculateDrivingLineForm();
        BeanUtils.copyProperties(exerciseOrderForm, calculateDrivingLineForm);
        DrivingLineVo drivingLineVo = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();

        //预估订单费用
        FeeRuleRequestForm feeRuleRequestForm = new FeeRuleRequestForm();
        feeRuleRequestForm.setDistance(drivingLineVo.getDistance());
        feeRuleRequestForm.setStartTime(new Date());
        feeRuleRequestForm.setWaitMinute(0);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm).getData();

        //创建返回结果对象
        ExpectOrderVo expectOrderVo = new ExpectOrderVo();
        expectOrderVo.setDrivingLineVo(drivingLineVo);
        expectOrderVo.setFeeRuleResponseVo(feeRuleResponseVo);

        return expectOrderVo;
    }
}
