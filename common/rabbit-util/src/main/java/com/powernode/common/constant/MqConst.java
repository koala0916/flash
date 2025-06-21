package com.powernode.common.constant;

public class MqConst {


    public static final String EXCHANGE_ORDER = "power.order";
    public static final String ROUTING_PAY_SUCCESS = "power.pay.success";
    public static final String ROUTING_PROFITSHARING_SUCCESS = "power.profitsharing.success";
    public static final String QUEUE_PAY_SUCCESS = "power.pay.success";
    public static final String QUEUE_PROFITSHARING_SUCCESS = "power.profitsharing.success";


    //取消订单延迟消息
    public static final String EXCHANGE_CANCEL_ORDER = "power.cancel.order";
    public static final String ROUTING_CANCEL_ORDER = "power.cancel.order";
    public static final String QUEUE_CANCEL_ORDER = "power.cancel.order";

    //分账延迟消息
    public static final String EXCHANGE_PROFITSHARING = "power.profitsharing";
    public static final String ROUTING_PROFITSHARING = "power.profitsharing";
    public static final String QUEUE_PROFITSHARING  = "power.profitsharing";

}
