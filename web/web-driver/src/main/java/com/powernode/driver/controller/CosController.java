package com.powernode.driver.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.driver.service.CosService;
import com.powernode.model.vo.driver.CosUploadVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "腾讯云cos上传接口管理")
@RestController
@RequestMapping(value="/cos")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CosController {

    @Resource
    private CosService cosService;

    @Operation(summary = "上传文件")
//    @PowerLogin
    @PostMapping(value = "/upload")
    public Result<CosUploadVo> upload(@RequestPart("file") MultipartFile file,@RequestParam("path") String path) {
    	return Result.ok(cosService.upload(file, path));
    }
}

