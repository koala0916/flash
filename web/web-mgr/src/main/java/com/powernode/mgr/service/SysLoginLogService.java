package com.powernode.mgr.service;


import com.powernode.model.entity.system.SysLoginLog;
import com.powernode.model.query.system.SysLoginLogQuery;
import com.powernode.model.vo.base.PageVo;

public interface SysLoginLogService {

    PageVo<SysLoginLog> findPage(Long page, Long limit, SysLoginLogQuery sysLoginLogQuery);

    /**
     * 记录登录信息
     *
     * @param sysLoginLog
     * @return
     */
    void recordLoginLog(SysLoginLog sysLoginLog);

    SysLoginLog getById(Long id);
}
