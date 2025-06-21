package com.powernode.model.form.rules;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FeeRuleRequestForm {

    @Schema(description = "配送里程")
    private BigDecimal distance;

    @Schema(description = "配送时间")
    private Date startTime;

    @Schema(description = "等候分钟")
    private Integer waitMinute;

}
