package com.lee.robot.controller;

import com.lee.robot.service.IRobotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author jrlee
 * @since 2022/07
 */
@RestController
@RequestMapping("/robot")
@Slf4j
public class RobotController {

	@Resource
	private IRobotService robotService;

	@PostMapping("/test")
	public void test() {
		robotService.test();
	}

	@PostMapping("/v1/send")
	public String sendMsg(@RequestBody Map<String, Object> param){
		return robotService.sendMsg(param);
	}

	@PostMapping("/work")
	public void work() {
		try {
			robotService.work();
		} catch (InterruptedException e) {
			log.error("work异常：", e);
		}
	}

	@PostMapping("/after_work")
	public void afterWork() {
		robotService.afterWork();
	}



}
