package com.lee.robot.entity;

import lombok.Data;

/**
 * @author jrlee
 * @since 2022/07
 */
@Data
public class Weather {

	private String address;
	private String cityCode;
	private String temp;
	private String weather;
	private String windDirection;
	private String windPower;
	private String humidity;
	private String reportTime;

}
