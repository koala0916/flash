package com.powernode.driver.service;

import com.powernode.model.vo.order.TextAuditingVo;

public interface CiService {


    Boolean imageAuditing(String path);

    TextAuditingVo textAuditing(String content);
}
