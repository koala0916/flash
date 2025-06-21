package com.powernode.mgr.service.impl;


import com.powernode.mgr.service.SysOperLogService;
import com.powernode.model.entity.system.SysOperLog;
import com.powernode.model.query.system.SysOperLogQuery;
import com.powernode.model.vo.base.PageVo;
import com.powernode.system.client.SysOperLogFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class SysOperLogServiceImpl implements SysOperLogService {

	@Autowired
	private SysOperLogFeignClient sysOperLogFeignClient;

	@Override
	public PageVo<SysOperLog> findPage(Long page, Long limit, SysOperLogQuery sysOperLogQuery) {
		return sysOperLogFeignClient.findPage(page, limit, sysOperLogQuery).getData();
	}

	@Override
	public void saveSysLog(SysOperLog sysOperLog) {
		sysOperLogFeignClient.saveSysLog(sysOperLog);
	}

	@Override
	public SysOperLog getById(Long id) {
		return sysOperLogFeignClient.getById(id).getData();
	}
}
