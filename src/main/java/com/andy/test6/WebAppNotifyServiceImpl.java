package com.andy.test6;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 微信通知服务
 * @author:andy
 * @version:1.0
 * @date:2016年8月22日
 */
public class WebAppNotifyServiceImpl implements IWebAppNotifyService {
	
	private final ExecutorService executors = Executors.newCachedThreadPool();
	
	public String sendRecharge(Map<String, Object> params) {
//		executors.execute(command);
		return "ok";
	}
	
	

}
