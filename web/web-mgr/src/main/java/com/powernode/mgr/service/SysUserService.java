package com.powernode.mgr.service;


import com.powernode.model.entity.system.SysUser;
import com.powernode.model.query.system.SysUserQuery;
import com.powernode.model.vo.base.PageVo;

public interface SysUserService {

    SysUser getById(Long id);

    void save(SysUser sysUser);

    void update(SysUser sysUser);

    void remove(Long id);

    PageVo<SysUser> findPage(Long page, Long limit, SysUserQuery sysUserQuery);

    void updateStatus(Long id, Integer status);


}
