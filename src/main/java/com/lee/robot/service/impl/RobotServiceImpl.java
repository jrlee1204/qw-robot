package com.lee.robot.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lee.robot.entity.*;
import com.lee.robot.service.IRobotService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author jrlee
 * @since 2022/07
 */
@Service
@Slf4j
public class RobotServiceImpl implements IRobotService {

	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

	private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

	Gson gson = new Gson();

	@Override
	public String sendMsg(Map<String, Object> param) {

		// 实例化OkHttpClient对象
		OkHttpClient client = new OkHttpClient();
		// 构建参数
		String json = gson.toJson(param);
		//实例化RequestBody对象
		RequestBody requestBody = RequestBody.create(json, JSON);
		// 实例化Request对象
		Request request = new Request.Builder()
				.url("<企业微信webhook地址>")
				.post(requestBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.code() == 200) {
				return "success";
			}
		} catch (IOException e) {
			log.error("调用企业微信发送消息异常：", e);
		}
		return "fail";
	}

	@Override
	public void work() throws InterruptedException {
		LocalDate now = LocalDate.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String date = dateTimeFormatter.format(now);
		Day day = getCalendar(date);
		StringBuilder sb = new StringBuilder();
		// 因为免费API QPS限制为 1 ，所以睡 1 秒
		TimeUnit.SECONDS.sleep(1);
		Weather weather = getWeather("深圳");
		sb.append("## 【摸鱼办】提醒您：\n");
		sb.append("上午好，摸鱼人，今天是").append(now).append("（农历").append(day.getLunarCalendar()).append("）").append("，天气").append(weather.getWeather()).append("，气温").append(weather.getTemp()).append("\n");
		sb.append("宜：").append(day.getSuit()).append("\n");
		sb.append("忌：").append(day.getAvoid()).append("\n");
		// 如果为工作日
		if ("工作日".equals(day.getTypeDes())) {
			sb.append("有事没事冲个咖啡，多喝热水，多去厕所，去廊道走走别总在工位上坐着，钱是老板的，但健康是自己的。\n");
			sb.append("**温馨提示：**\n");
			// 因为免费API QPS限制为 1 ，所以睡 1 秒
			TimeUnit.SECONDS.sleep(1);
			Map<String, Long> map = buildFestival();
			TimeUnit.SECONDS.sleep(1);
			Jokes jokes = getJokes();
			List<Map.Entry<String, Long>> list = new ArrayList<>(map.entrySet());
			list.sort(new Comparator<Map.Entry<String, Long>>() {
				@Override
				public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
					// 从小到大排序
					return (int) (o1.getValue() - o2.getValue());
				}
			});
			for (Map.Entry<String, Long> l : list) {
				sb.append(">距离【").append(l.getKey()).append("】还有：<font color=\"info\">").append(l.getValue()).append("</font>天\n\n");
			}
			sb.append("**摸鱼指南：**\n");
			sb.append("[链接1](https://mo.fish/)\n");
			sb.append("[链接2](https://duomoyu.com/)\n");
			sb.append("[链接3](https://www.anyknew.com/#/)\n");
			sb.append("[链接4](https://flipanim.com/)\n");
			sb.append("上班是交易，摸鱼才是赚钱！\n");
			sb.append("**每日段子：**\n");
			sb.append(jokes.getContent()).append("\n");
		} else {
			sb.append("今天休息，好好放松吧~");
		}
		log.info("消息体：{}", sb.toString());
		Map<String, Object> param = new HashMap<>();
		param.put("msgtype", "markdown");
		HashMap<String, Object> markdown = new HashMap<>();
		markdown.put("content", sb.toString());
		param.put("markdown", markdown);
		sendMsg(param);
	}

	@Override
	public void afterWork() {
		LocalDate now = LocalDate.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String date = dateTimeFormatter.format(now);
		Day day = getCalendar(date);
		if ("工作日".equals(day.getTypeDes())) {
			StringBuilder content = new StringBuilder();
			content.append("【摸鱼办】提醒您：\n");
			content.append("\n");
			content.append("下班时间到\n");
			content.append("\n");
			content.append("记得打卡！\n");
			content.append("\n");
			content.append("记得打卡！\n");
			content.append("\n");
			content.append("记得打卡！\n");
			content.append("\n");
			Map<String, Object> param = new HashMap<>();
			param.put("msgtype", "text");
			HashMap<String, Object> text = new HashMap<>();
			text.put("content", content.toString());
			text.put("mentioned_list", "@all");
			param.put("text", text);
			sendMsg(param);
		}
	}

	@Override
	public void test() {
		Jokes jokes = getJokes();
		System.out.println(jokes.getContent());
	}

	/**
	 * 构建节日剩余时间
	 * @return Map<String, String>
	 */
	public Map<String, Long> buildFestival() throws InterruptedException {
		List<String> festivalList = Arrays.asList("清明节", "劳动节", "端午节", "中秋节", "国庆节", "元旦", "春节");
		LocalDate now = LocalDate.now();
		int monthValue = now.getMonthValue();
		Map<String, Long> map = new HashMap<>();
		map.put("周六", (long)6 - now.getDayOfWeek().getValue());
		StringBuilder sb = new StringBuilder();
		if (monthValue == 12) {
			sb.append(now.getYear() + 1);
			sb.append("01");
		} else {
			sb.append(now.getYear());
			if (monthValue + 1 < 10) {
				sb.append("0");
			}
			sb.append(monthValue + 1);
		}
		sb.append("10");
		LocalDate salaryDay = LocalDate.parse(sb.toString(), DateTimeFormatter.BASIC_ISO_DATE);
		map.put("工资日", salaryDay.toEpochDay() - now.toEpochDay());
		List<Festival> yearFestival = getFestival(String.valueOf(now.getYear()));
		// 因为免费API QPS限制为 1 ，所以睡 1 秒
		TimeUnit.SECONDS.sleep(1);
		List<Festival> nextYearFestival = getFestival(String.valueOf(now.getYear() + 1));
		if (nextYearFestival != null) {
			yearFestival.addAll(nextYearFestival);
		}
		for (Festival f : yearFestival) {
			if (Integer.parseInt(f.getYear()) == now.getYear() && Integer.parseInt(f.getMonth()) < monthValue) {
				continue;
			}
			List<Day> days = f.getDays();
			for (Day d : days) {
				if (festivalList.contains(d.getTypeDes()) && !map.containsKey(d.getTypeDes())) {
					String dateStr = d.getDate();
					LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
					if (date.toEpochDay() - now.toEpochDay() > 0) {
						map.put(d.getTypeDes(), date.toEpochDay() - now.toEpochDay());
					}
				}
			}
		}
		return map;
	}

	/**
	 * 获取指定日期详情
	 * @param date 日期，格式：yyyy-MM-dd
	 * @return 返回详情
	 */
	public Day getCalendar(String date) {
		String url = "https://www.mxnzp.com/api/holiday/single/" + date + "?<后面接appid和app_secret>";
		Request request = new Request.Builder()
				.url(url)
				.build();
		Result<Day> result = new Result<>();
		try {
			Response response = HTTP_CLIENT.newCall(request).execute();
			if (response.code() != 200 || response.body() == null) {
				log.error("调用外部节假日链接未获取到数据");
				return null;
			}
			String json = response.body().string();
			result = gson.fromJson(json, new TypeToken<Result<Day>>(){}.getType());
		} catch (IOException e) {
			log.error("调用外部节假日链接异常：", e);
		}
		return result.getData();
	}

	/**
	 * 获取指定年份节日信息
	 * @param year 年份
	 * @return 返回节日信息
	 */
	public List<Festival> getFestival(String year){
		String url = "https://www.mxnzp.com/api/holiday/list/year/" + year + "/festival?<后面接appid和app_secret>";
		Request request = new Request.Builder()
				.url(url)
				.build();
		Result<List<Festival>> result = new Result<>();
		try {
			Response response = HTTP_CLIENT.newCall(request).execute();
			if (response.code() != 200 || response.body() == null) {
				log.error("调用外部节假日链接未获取到数据");
				return Collections.emptyList();
			}
			String json = response.body().string();
			result = gson.fromJson(json, new TypeToken<Result<List<Festival>>>(){}.getType());
		} catch (IOException e) {
			log.error("调用外部节假日链接异常：", e);
		}
		return result.getData();
	}


	public Weather getWeather(String city){
		String url = " https://www.mxnzp.com/api/weather/current/" + city + "?<后面接appid和app_secret>";
		Request request = new Request.Builder()
				.url(url)
				.build();
		Result<Weather> result = new Result<>();
		try {
			Response response = HTTP_CLIENT.newCall(request).execute();
			if (response.code() != 200 || response.body() == null) {
				log.error("调用外部天气链接未获取到数据");
				return null;
			}
			String json = response.body().string();
			result = gson.fromJson(json, new TypeToken<Result<Weather>>(){}.getType());
		} catch (IOException e) {
			log.error("调用外部天气链接异常：", e);
		}
		return result.getData();
	}


	public Jokes getJokes(){
		String url = " https://www.mxnzp.com/api/jokes/list/random?<后面接appid和app_secret>";
		Request request = new Request.Builder()
				.url(url)
				.build();
		Result<List<Jokes>> result = new Result<>();

		try {
			Response response = HTTP_CLIENT.newCall(request).execute();
			if (response.code() != 200 || response.body() == null) {
				log.error("调用外部每日段子链接未获取到数据");
				return null;
			}
			String json = response.body().string();
			result = gson.fromJson(json, new TypeToken<Result<List<Jokes>>>(){}.getType());
		} catch (IOException e) {
			log.error("调用外部每日段子链接异常：", e);
		}
		List<Jokes> jokeList = result.getData();
		Jokes jokes = jokeList.get(0);
		for (Jokes j : jokeList) {
			if (j.getContent().length() < jokes.getContent().length()) {
				jokes = j;
			}
		}
		return jokes;
	}


}
