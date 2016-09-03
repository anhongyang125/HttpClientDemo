package com.andy.test6;
/**
 * @author:andy
 * @version:1.0
 * @date:2016年8月22日
 */
public class NotifyRechargeProcess implements IWebAppProcess {
	
	private String recharge_url = WebAppConstants.Head.recharge_url;
	
	public void dealResult(String result) {
		// TODO 处理数据
		System.out.println(result);

	}

}
