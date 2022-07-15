package com.lee.robot.entity;

import lombok.Data;

/**
 * @author jrlee
 * @since 2022/07
 */
@Data
public class Result<T> {

	private String code;
	private String msg;
	private T data;

}
