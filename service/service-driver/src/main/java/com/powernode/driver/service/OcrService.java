package com.powernode.driver.service;

import com.powernode.model.vo.driver.IdCardOcrVo;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {


    IdCardOcrVo idCardOcr(MultipartFile file);
}
