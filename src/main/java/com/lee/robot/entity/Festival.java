package com.lee.robot.entity;

import lombok.Data;

import java.util.List;

/**
 * @author jrlee
 * @since 2022/07
 */
@Data
public class Festival {

	private String month;
	private String year;
	private List<Day> days;

}
