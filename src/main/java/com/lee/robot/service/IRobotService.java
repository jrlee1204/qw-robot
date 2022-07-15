package com.lee.robot.service;

import java.util.Map;

/**
 * @author jrlee
 * @since 2022/07
 */
public interface IRobotService {

	/**
	 * 发送信息
	 * @param param 发送的数据
	 * @return string
	 */
	String sendMsg(Map<String, Object> param);

	/**
	 * 上班时发送消息
	 * @throws InterruptedException 异常
	 */
	void work() throws InterruptedException;

	/**
	 * 下班是发送消息
	 */
	void afterWork();

	/**
	 * 测试
	 */
	void test();
}
