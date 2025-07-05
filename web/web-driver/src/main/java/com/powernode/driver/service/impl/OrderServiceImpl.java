package com.powernode.driver.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.dispatch.client.NewOrderFeignClient;
import com.powernode.driver.service.OrderService;
import com.powernode.map.client.LocationFeignClient;
import com.powernode.map.client.MapFeignClient;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.form.order.OrderFeeForm;
import com.powernode.model.form.order.StartDriveForm;
import com.powernode.model.form.order.UpdateOrderBillForm;
import com.powernode.model.form.order.UpdateOrderCartForm;
import com.powernode.model.form.rules.FeeRuleRequestForm;
import com.powernode.model.form.rules.ProfitsharingRuleRequestForm;
import com.powernode.model.form.rules.RewardRuleRequestForm;
import com.powernode.model.vo.map.DrivingLineVo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.NewOrderDataVo;
import com.powernode.model.vo.order.OrderInfoVo;
import com.powernode.model.vo.rules.FeeRuleResponseVo;
import com.powernode.model.vo.rules.ProfitsharingRuleResponseVo;
import com.powernode.model.vo.rules.RewardRuleResponseVo;
import com.powernode.order.client.OrderInfoFeignClient;
import com.powernode.rules.client.FeeRuleFeignClient;
import com.powernode.rules.client.ProfitsharingRuleFeignClient;
import com.powernode.rules.client.RewardRuleFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Resource
    private NewOrderFeignClient newOrderFeignClient;
    @Autowired
    private MapFeignClient mapFeignClient;
    @Autowired
    private LocationFeignClient locationFeignClient;
    @Autowired
    private FeeRuleFeignClient feeRuleFeignClient;

    @Resource
    private RewardRuleFeignClient rewardRuleFeignClient;

    @Resource
    private ProfitsharingRuleFeignClient profitsharingRuleFeignClient ;

    @Override
    public Integer queryOrderStatus(Long orderId) {
        return orderInfoFeignClient.getOrderStatus(orderId).getData();
    }

    /**
     * 查询当前配送员符合条件的订单
     */
    @Override
    public List<NewOrderDataVo> findNewOrderQueueData(Long driverId) {
        return newOrderFeignClient.findNewOrderQueueData(driverId).getData();
    }


    /**
     * 配送员抢单
     */
    @Override
    public Boolean robNewOrder(Long driverId, Long orderId) {
        return orderInfoFeignClient.robNewOrder(driverId, orderId).getData();
    }


    /**
     * 配送员查看当前进行中的订单
     */
    @Override
    public CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId) {
        return orderInfoFeignClient.searchDriverCurrentOrder(driverId).getData();
    }

    /**
     * 根据订单id查询配送员订单信息
     */
    @Override
    public OrderInfoVo getOrderInfo(Long orderId, Long driverId) {
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();

        if (orderInfo.getDriverId().longValue() != driverId.longValue()) {
            throw new PowerException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        //封装订单信息
        OrderInfoVo orderInfoVo = new OrderInfoVo();
        orderInfoVo.setOrderId(orderInfo.getId());
        BeanUtils.copyProperties(orderInfo, orderInfoVo);

        return orderInfoVo;
    }


    /**
     * 查看最佳路线
     */
    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm) {
        return mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();
    }

    /**
     * 配送员到达指定位置
     */
    @Override
    public Boolean driverArrivedStartLocation(Long orderId, Long driverId) {
        return orderInfoFeignClient.driverArrivedStartLocation(orderId, driverId).getData();
    }


    @Override
    public Boolean updateDriverCarInfo(UpdateOrderCartForm updateOrderCartForm) {
        return orderInfoFeignClient.updateOrderCart(updateOrderCartForm).getData();
    }

    @Override
    public Boolean startDrive(StartDriveForm startDriveForm) {
        return orderInfoFeignClient.startDrive(startDriveForm).getData();
    }

    @Override
    public Boolean driverArriveStartLocation(Long orderId, Long driverId) {
        return orderInfoFeignClient.driverArriveStartLocation(orderId, driverId).getData();
    }

    /**
     * 结束配送
     * 1.获取订单信息
     * 2.计算实际里程
     * 3.计算实际金额
     * 4.等候时间
     * 5.订单总金额
     * 6.奖励
     * 7.分账
     */
    @Override
    public Boolean endDrive(OrderFeeForm orderFeeForm) {
        //1.获取订单信息
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderFeeForm.getOrderId()).getData();
        if (orderInfo.getDriverId().longValue() != orderFeeForm.getDriverId().longValue()) {
            throw new PowerException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        //2.计算实际里程
        BigDecimal realDistance = locationFeignClient.calculateOrderRealDistance(orderInfo.getId()).getData();

        //3.计算实际金额
        FeeRuleRequestForm feeRuleRequestForm = new FeeRuleRequestForm();
        feeRuleRequestForm.setDistance(realDistance);
        feeRuleRequestForm.setStartTime(orderInfo.getStartServiceTime());

        //4.等候时间
        int waitMinute = (int)(orderInfo.getStartServiceTime().getTime() - orderInfo.getArriveTime().getTime()) / (1000 * 60);
        feeRuleRequestForm.setWaitMinute(waitMinute);

        FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm).getData();

        //5.订单总金额 需加上 路桥费、停车费、其他费用、客户好处费
        BigDecimal totalAmount = feeRuleResponseVo.getTotalAmount().add(orderFeeForm.getTollFee()).add(orderFeeForm.getParkingFee()).add(orderFeeForm.getOtherFee()).add(orderInfo.getFavourFee());
        feeRuleResponseVo.setTotalAmount(totalAmount);


        //6.奖励
        String startTime = new DateTime(orderInfo.getStartServiceTime()).toString("yyyy-MM-dd HH:mm:ss");
        String endTime = new DateTime(orderInfo.getEndServiceTime()).toString("yyyy-MM-dd HH:mm:ss");

        //计算时间段之内配送的订单数量
        Long orderNum = orderInfoFeignClient.getOrderNumByTime(startTime, endTime).getData();

        //构建规则引擎入参
        RewardRuleRequestForm rewardRuleRequestForm = new RewardRuleRequestForm();
        rewardRuleRequestForm.setOrderNum(orderNum);
        rewardRuleRequestForm.setStartTime(orderInfo.getStartServiceTime());
        //奖励金额
        RewardRuleResponseVo rewardRuleResponseVo = rewardRuleFeignClient.calculateOrderRewardFee(rewardRuleRequestForm).getData();

        //计算分账
        ProfitsharingRuleRequestForm profitsharingRuleRequestForm = new ProfitsharingRuleRequestForm();
        profitsharingRuleRequestForm.setOrderAmount(feeRuleResponseVo.getTotalAmount());
        profitsharingRuleRequestForm.setOrderNum(orderNum);

        ProfitsharingRuleResponseVo profitsharingRuleResponseVo = profitsharingRuleFeignClient.calculateOrderProfitsharingFee(profitsharingRuleRequestForm).getData();

        //更新相关订单信息
        UpdateOrderBillForm updateOrderBillForm = new UpdateOrderBillForm();
        updateOrderBillForm.setOrderId(orderInfo.getId());
        updateOrderBillForm.setDriverId(orderInfo.getDriverId());
        updateOrderBillForm.setTotalAmount(orderFeeForm.getTollFee());
        updateOrderBillForm.setParkingFee(orderFeeForm.getParkingFee());
        updateOrderBillForm.setFavourFee(orderInfo.getFavourFee());
        updateOrderBillForm.setRealDistance(realDistance);

        BeanUtils.copyProperties(feeRuleResponseVo, updateOrderBillForm);
        BeanUtils.copyProperties(profitsharingRuleResponseVo, updateOrderBillForm);

        //结束配送更新账单信息
        orderInfoFeignClient.endDrive(updateOrderBillForm);

        return true;

    }
}
