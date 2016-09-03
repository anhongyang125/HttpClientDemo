package com.andy.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class TestWebApp2 {
	public static void main(String[] args) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost("http://172.16.204.132:7002/zp-p2p-client/client.do");
		RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(10000).setConnectTimeout(10000)
				.setSocketTimeout(10000).build();
		CloseableHttpResponse response = null;
		try {
			List formparams = new ArrayList();
			formparams.add(new BasicNameValuePair("transMessage", "{\"busiCode\":\"10010112\",\"message\":{\"head\":{\"messageID\":\"2016051612010132\",\"timeStamp\":\"20160516120101\",\"sysId\":\"101\",\"sysType\":\"zpjf\",\"src\":\"62235|12A8\",\"imei\":\"3521458654475\",\"ua\":\"Android2.3.5\",\"transactionType\":\"\",\"digest\":\"VprMcPtSWAKAXGx14Vu701aazHD7UlgC1obpXmeRlnyWSTPjuemevpOnmqcQRUmZnUjx4h9aRnrClGJJyll5Rj5iqyl6x1B1qrqD4r1BoJ2C5zkRZQOBWw!!\"},\"body\":{\"realName\":\"安红利\",\"cardId\":\"210111197404154613\",\"userId\":\"andy123\"}}}"));
			post.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
			post.setConfig(config);
			response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity);
//			JSONObject jsonb = JSONObject.parseObject(content);
			System.out.println("content:" + content);
//			System.out.println("message:" + jsonb.getString("message"));
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}