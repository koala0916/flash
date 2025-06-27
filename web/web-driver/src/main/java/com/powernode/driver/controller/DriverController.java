package com.powernode.driver.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.driver.service.DriverService;
import com.powernode.model.form.driver.DriverFaceModelForm;
import com.powernode.model.form.driver.UpdateDriverAuthInfoForm;
import com.powernode.model.vo.driver.DriverAuthInfoVo;
import com.powernode.model.vo.driver.DriverLoginVo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "配送员API接口管理")
@RestController
@RequestMapping(value = "/driver")
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverController {

    @Resource
    private DriverService driverService;

    @Operation(summary = "配送员登录")
    @GetMapping("/login/{code}")
    public Result<String> login(@PathVariable String code) {
        return Result.ok(driverService.login(code));
    }

    @PowerLogin
    @Operation(summary = "获取配送员登录信息")
    @GetMapping("/getDriverLoginInfo")
    public Result<DriverLoginVo> getDriverLoginInfo() {
        //从ThreadLocal中获取配送员的id
        Long userId = AuthContextHolder.getUserId();

        return Result.ok(driverService.getDriverLoginVo(userId));
    }


    @Operation(summary = "获取配送员认证信息")
    @PowerLogin
    @GetMapping("/getDriverAuthInfo")
    public Result<DriverAuthInfoVo> getDriverAuthInfo() {
        Long driverId = AuthContextHolder.getUserId();

        return Result.ok(driverService.getDriverAuthInfo(driverId));
    }


    @Operation(summary = "更新配送员认证信息")
    @PostMapping("/updateDriverAuthInfo")
    @PowerLogin
    public Result<Boolean> updateDriverAuthInfo(@RequestBody UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        Long driverId = AuthContextHolder.getUserId();
        updateDriverAuthInfoForm.setDriverId(driverId);
        return Result.ok(driverService.updateDriverAuthInfo(updateDriverAuthInfoForm));
    }


    @Operation(summary = "创建配送员人脸模型")
    @PostMapping("/creatDriverFaceModel")
    @PowerLogin
    public Result<Boolean> createDriverFaceModel(@RequestBody DriverFaceModelForm driverFaceModelForm) {
        Long driverId = AuthContextHolder.getUserId();
        driverFaceModelForm.setDriverId(driverId);
        return Result.ok(driverService.createDriverFaceModel(driverFaceModelForm));
    }


    /**
     * 目前先写死
     */
    @Operation(summary = "查询当前进行中的订单")
    @PowerLogin
    @GetMapping("/searchDriverCurrentOrder")
    public Result<CurrentOrderInfoVo> searchDriverCurrentOrder() {
        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();
        currentOrderInfoVo.setIsHasCurrentOrder(false);

        return Result.ok(currentOrderInfoVo);
    }


    @Operation(summary = "查看配送员当天是否进行人脸识别")
    @PowerLogin
    @GetMapping("/isFaceRecognition")
    public Result<Boolean> isFaceRecognition() {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(driverService.isFaceRecognition(driverId));
    }
}

