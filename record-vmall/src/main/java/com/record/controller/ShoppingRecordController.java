package com.record.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import com.record.entity.ShoppingRecord;


import com.record.service.ShoppingRecordService;


import com.record.utils.ArgsBean;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class ShoppingRecordController {

	@Resource
	private ShoppingRecordService shoppingRecordService;

	private static RestTemplate restTemplate = new RestTemplate();

	public static String userUrl;
	public static String productUrl;
	public static String shoppingcarUrl;
	public static String recordUrl;
	public static String evaluationUrl;
	private static String exporterUrl;

    public ShoppingRecordController() {
//        this.userUrl = "http://127.0.0.1:8081";
//        this.productUrl = "http://127.0.0.1:8082";
//        this.shoppingcarUrl = "http://127.0.0.1:8083";
//        this.recordUrl = "http://127.0.0.1:8084";
//        this.evaluationUrl = "http://127.0.0.1:8085";

		this.userUrl = "http://user:8081";
		this.productUrl = "http://product:8082";
		this.shoppingcarUrl = "http://shoppingcar:8083";
		this.recordUrl = "http://order:8084";
		this.evaluationUrl = "http://evaluation:8085";
		System.out.println("url初始化：\n" + userUrl + "\n" + productUrl + "\n" + shoppingcarUrl + "\n" + recordUrl + "\n"+ evaluationUrl);
	}

	@RequestMapping(value = "/addShoppingRecord", method = RequestMethod.POST)
	public String addShoppingRecord(@RequestBody ArgsBean argsBean) {
    	// 统计add订单qps，请求到exporter
		// String res = restTemplate.postForObject(url,argsBean,String.class);
		try {
			// 处理connect exporter异常
			String res = restTemplate.getForObject(this.exporterUrl,String.class);
			System.out.println("----add ordd, send to exporter. res:\n" + res);
		}catch (Exception e){
			System.out.println("ERROR ----add ordd, send to exporter failed!");
		}



		return JSONObject.toJSONString(shoppingRecordService.addShoppingRecord(argsBean));
	}

	@RequestMapping(value = "/changeShoppingRecord", method = RequestMethod.POST)
	public String changeShoppingRecord(@RequestBody ArgsBean argsBean) {
    	//int userId, int productId, String time, int orderStatus
		Map map = (Map) JSONObject.parse(argsBean.getMapString());
		//TODO 异常处理
		String userId = (String)map.get("userId");
		String productId = (String)map.get("productId");
		String time = (String)map.get("time");
		String orderStatus = (String)map.get("orderStatus");

		System.out.println("我接收了" + userId + " " + productId + " " + time + " " + orderStatus);
		ShoppingRecord shoppingRecord = shoppingRecordService.getShoppingRecord(Integer.valueOf(userId), Integer.valueOf(productId), time);
		System.out.println("我获取到了了" + shoppingRecord.getTime());
		shoppingRecord.setOrderStatus(Integer.valueOf(orderStatus));
		shoppingRecordService.updateShoppingRecord(shoppingRecord);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", "success");
		return JSONObject.toJSONString(resultMap);
	}

	@RequestMapping(value = "/getShoppingRecords", method = RequestMethod.POST)
	public String getShoppingRecords(@RequestBody ArgsBean argsBean) {
		Map map = (Map) JSONObject.parse(argsBean.getMapString());
		//TODO 异常处理
		String userId = (String)map.get("userId");

		List<ShoppingRecord> shoppingRecordList = shoppingRecordService.getShoppingRecords(Integer.valueOf(userId));
		String shoppingRecords = JSONArray.toJSONString(shoppingRecordList);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", shoppingRecords);
		System.out.println("res="+JSONObject.toJSONString(resultMap));
		return JSONObject.toJSONString(resultMap);
	}

	@RequestMapping(value = "/getShoppingRecordsByOrderStatus", method = RequestMethod.POST)
	public String getShoppingRecordsByOrderStatus(@RequestBody ArgsBean argsBean) {
		Map map = (Map) JSONObject.parse(argsBean.getMapString());
		//TODO 异常处理
		String orderStatus = (String)map.get("orderStatus");

		List<ShoppingRecord> shoppingRecordList = shoppingRecordService.getShoppingRecordsByOrderStatus(Integer.valueOf(orderStatus));
		String shoppingRecords = JSONArray.toJSONString(shoppingRecordList);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", shoppingRecords);
		return JSONObject.toJSONString(resultMap);
	}

	@RequestMapping(value = "/getAllShoppingRecords", method = RequestMethod.POST)
	public String getAllShoppingRecords() {

		List<ShoppingRecord> shoppingRecordList = shoppingRecordService.getAllShoppingRecords();
		String shoppingRecords = JSONArray.toJSONString(shoppingRecordList);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", shoppingRecords);
		return JSONObject.toJSONString(resultMap);
	}

	@RequestMapping(value = "/getUserProductRecord", method = RequestMethod.POST)
	public String getUserProductRecord(@RequestBody ArgsBean argsBean) {
		Map map = (Map) JSONObject.parse(argsBean.getMapString());
		//TODO 异常处理
		String userId = (String)map.get("userId");
		String productId = (String)map.get("productId");

		String result = "false";
		if (shoppingRecordService.getUserProductRecord(Integer.valueOf(userId), Integer.valueOf(productId))) {
			result = "true";
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", result);
		return JSONObject.toJSONString(resultMap);
	}

	// 供user应用调用
	@RequestMapping(value = "/deleteShoppingRecordByUser", method = RequestMethod.POST)
	public String deleteShoppingRecordByUser(@RequestBody ArgsBean argsBean) {
		Map map = (Map) JSONObject.parse(argsBean.getMapString());
		//TODO 异常处理
		String userId = (String)map.get("userId");

		String result = "false";
		if (shoppingRecordService.deleteShoppingRecordByUser(Integer.valueOf(userId))) {
			result = "true";
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", result);
		return JSONObject.toJSONString(resultMap);
	}

	// 供product应用调用
	@RequestMapping(value = "/deleteShoppingRecordByProductId", method = RequestMethod.POST)
	public String deleteShoppingRecordByProductId(@RequestBody ArgsBean argsBean) {
		Map map = (Map) JSONObject.parse(argsBean.getMapString());
		//TODO 异常处理
		String productId = (String)map.get("productId");

		String result = "false";
		if (shoppingRecordService.deleteShoppingRecordByProductId(Integer.valueOf(productId))) {
			result = "true";
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", result);
		return JSONObject.toJSONString(resultMap);
	}
}
