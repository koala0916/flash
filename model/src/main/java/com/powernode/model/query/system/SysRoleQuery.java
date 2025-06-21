//
//
package com.powernode.model.query.system;

import java.io.Serializable;

/**
 */
public class SysRoleQuery implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String roleName;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}

