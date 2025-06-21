package com.powernode.model.form.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "OrderInfo")
public class StartDriveForm {


    @Schema(description = "订单ID")
	private Long orderId;

    @Schema(description = "配送员ID")
    private Long driverId;

}