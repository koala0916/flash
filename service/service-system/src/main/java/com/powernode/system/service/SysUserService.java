package com.powernode.system.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.entity.system.SysUser;
import com.powernode.model.query.system.SysUserQuery;
import com.powernode.model.vo.base.PageVo;

import java.util.Map;

public interface SysUserService extends IService<SysUser> {

    PageVo<SysUser> findPage(Page<SysUser> pageParam, SysUserQuery adminQuery);

    void updateStatus(Long id, Integer status);

    SysUser getByUsername(String username);

    /**
     * 根据用户名获取用户登录信息
     * @param userId
     * @return
     */
    Map<String, Object> getUserInfo(Long userId);
}
