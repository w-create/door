package com.taiji.tscp.sso.server.enums;

import com.taiji.tscp.mvc.model.EnumItemable;

/**
 * 是否枚举
 * 
 * @author Joe
 */
public enum TrueFalseEnum implements EnumItemable<TrueFalseEnum> {

	TRUE("是", 1), 
	FALSE("否", 0);

	private String label;
	private Integer value;

	private TrueFalseEnum(String label, Integer value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return this.label;
	}

	public Integer getValue() {
		return this.value;
	}
}
