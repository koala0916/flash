package com.powernode.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.model.entity.system.SysOperLog;
import com.powernode.model.query.system.SysOperLogQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

    IPage<SysOperLog> selectPage(Page<SysOperLog> page, @Param("query") SysOperLogQuery sysOperLogQuery);

}
