package com.powernode.driver.service;

import com.powernode.model.vo.driver.CosUploadVo;
import org.springframework.web.multipart.MultipartFile;

public interface CosService {


    CosUploadVo upload(MultipartFile file, String path);
}
