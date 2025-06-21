package com.powernode.system.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.entity.system.SysLoginLog;
import com.powernode.model.query.system.SysLoginLogQuery;
import com.powernode.model.vo.base.PageVo;

public interface SysLoginLogService extends IService<SysLoginLog> {

    PageVo<SysLoginLog> findPage(Page<SysLoginLog> pageParam, SysLoginLogQuery sysLoginLogQuery);

    /**
     * 记录登录信息
     *
     * @param sysLoginLog
     * @return
     */
    void recordLoginLog(SysLoginLog sysLoginLog);

}
