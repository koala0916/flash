package com.powernode.mgr.service;


import com.powernode.model.entity.system.SysOperLog;
import com.powernode.model.query.system.SysOperLogQuery;
import com.powernode.model.vo.base.PageVo;

public interface SysOperLogService {

    PageVo<SysOperLog> findPage(Long page, Long limit, SysOperLogQuery sysOperLogQuery);

    /**
     * 保存系统日志记录
     */
    void saveSysLog(SysOperLog sysOperLog);

    SysOperLog getById(Long id);
}
