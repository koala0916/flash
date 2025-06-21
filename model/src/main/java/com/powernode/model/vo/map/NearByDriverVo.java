package com.powernode.model.vo.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NearByDriverVo {

    @Schema(description = "配送员id")
    private Long driverId;

    @Schema(description = "距离")
    private BigDecimal distance;
}
