package com.lee.robot.task;

import com.lee.robot.service.IRobotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时任务
 * @author jrlee
 * @since 2022/07
 */
@Component
@Slf4j
public class ScheduledTask {

	@Resource
	private IRobotService robotService;

	@Scheduled(cron = "00 30 9 * * ?")
	public void work(){
		try {
			robotService.work();
		} catch (InterruptedException e) {
			log.error("定时任务发送失败：", e);
		}
	}


	@Scheduled(cron = "00 00 18 * * ?")
	public void afterWork(){
		robotService.afterWork();
	}

}
