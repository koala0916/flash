package com.powernode.model.form.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateWxPaymentForm {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户优惠券id")
    private Long customerCouponId;

    @Schema(description = "用户id")
    private Long customerId;
}
