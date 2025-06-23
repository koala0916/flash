package com.powernode.driver.service.impl;


import com.powernode.driver.client.CosFeignClient;
import com.powernode.driver.service.CosService;
import com.powernode.model.vo.driver.CosUploadVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CosServiceImpl implements CosService {

    @Resource
    private CosFeignClient cosFeignClient;

    @Override
    public CosUploadVo upload(MultipartFile file, String path) {
        return cosFeignClient.upload(file, path).getData();
    }
}
