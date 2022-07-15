package com.lee.robot.entity;

import lombok.Data;

/**
 * @author jrlee
 * @since 2022/07
 */
@Data
public class Day {

	private String date;
	private Integer weekDay;
	private String yearTips;
	private Integer type;
	private String typeDes;
	private String chineseZodiac;
	private String solarTerms;
	private String avoid;
	private String lunarCalendar;
	private String suit;
	private Integer dayOfYear;
	private Integer weekOfYear;
	private String constellation;
	private Integer indexWorkDayOfMonth;

}
