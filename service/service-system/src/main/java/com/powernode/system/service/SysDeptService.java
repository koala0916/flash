package com.powernode.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.entity.system.SysDept;

import java.util.List;

public interface SysDeptService extends IService<SysDept> {

    List<SysDept> findNodes();

    List<SysDept> findUserNodes();

    void updateStatus(Long id, Integer status);
}
