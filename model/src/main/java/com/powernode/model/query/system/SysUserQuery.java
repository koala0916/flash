//
//
package com.powernode.model.query.system;


import lombok.Data;

import java.io.Serializable;

/**
 */
@Data
public class SysUserQuery implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String keyword;

	private String createTimeBegin;
	private String createTimeEnd;

	private Long roleId;
	private Long postId;
	private Long deptId;

}

