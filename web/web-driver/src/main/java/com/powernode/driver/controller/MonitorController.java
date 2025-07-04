package com.powernode.driver.controller;

import com.powernode.common.result.Result;
import com.powernode.driver.service.MonitorService;
import com.powernode.model.form.order.OrderMonitorForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "监控接口管理")
@RestController
@RequestMapping(value = "/monitor")
@SuppressWarnings({"unchecked", "rawtypes"})
public class MonitorController {

    @Resource
    private MonitorService monitorService;

    @Operation(summary = "上传监控信息")
    @PostMapping(value = "/upload")
    public Result<Boolean> upload(@RequestParam("file") MultipartFile file, @RequestBody OrderMonitorForm orderMonitorForm) {
        return Result.ok(monitorService.upload(file, orderMonitorForm));
    }
}

