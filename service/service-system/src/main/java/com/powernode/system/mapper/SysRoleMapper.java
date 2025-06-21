package com.powernode.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.model.entity.system.SysRole;
import com.powernode.model.query.system.SysRoleQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    IPage<SysRole> selectPage(Page<SysRole> page, @Param("query") SysRoleQuery roleQuery);
}
