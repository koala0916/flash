package com.powernode.model.form.driver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DriverFaceModelForm {

    @Schema(description = "配送员id")
    private Long driverId;

    @Schema(description = "图片 base64 数据")
    private String imageBase64 ;
}
