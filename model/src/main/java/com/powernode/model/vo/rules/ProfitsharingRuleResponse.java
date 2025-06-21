package com.powernode.model.vo.rules;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProfitsharingRuleResponse {

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "微信支付平台费率")
    private BigDecimal paymentRate;

    @Schema(description = "微信支付平台费用")
    private BigDecimal paymentFee;

    @Schema(description = "配送员代缴个税税率")
    private BigDecimal driverTaxRate;

    @Schema(description = "配送员税率支出费用")
    private BigDecimal driverTaxFee;

    @Schema(description = "平台分账收入")
    private BigDecimal platformIncome;

    @Schema(description = "配送员分账收入")
    private BigDecimal driverIncome;
}
