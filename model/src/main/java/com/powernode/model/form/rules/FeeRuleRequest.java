package com.powernode.model.form.rules;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FeeRuleRequest {

    @Schema(description = "配送里程")
    private BigDecimal distance;

    @Schema(description = "配送时间")
    private String startTime;

    @Schema(description = "等候分钟")
    private Integer waitMinute;

}
