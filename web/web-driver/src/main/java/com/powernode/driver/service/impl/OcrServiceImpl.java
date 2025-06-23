package com.powernode.driver.service.impl;


import com.powernode.driver.client.OcrFeignClient;
import com.powernode.driver.service.OcrService;
import com.powernode.model.vo.driver.IdCardOcrVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OcrServiceImpl implements OcrService {


    @Resource
    private OcrFeignClient ocrFeignClient;

    @Override
    public IdCardOcrVo idCardOcr(MultipartFile file) {
        return ocrFeignClient.idCardOcr(file).getData();
    }
}
