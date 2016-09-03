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

public class TestWebApp {
	public static void main(String[] args) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost("http://test.zpcredit.com/api/v1/top_up");
		RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(10000).setConnectTimeout(10000)
				.setSocketTimeout(10000).build();
		CloseableHttpResponse response = null;
		try {
			List formparams = new ArrayList();
			formparams.add(new BasicNameValuePair("user_id", "andy123"));
			formparams.add(new BasicNameValuePair("first", "恭喜您，充值成功！"));
			formparams.add(new BasicNameValuePair("amount", "1"));
			formparams.add(new BasicNameValuePair("time", "20160808"));
			formparams.add(new BasicNameValuePair("account_balance", "11"));
			formparams.add(new BasicNameValuePair("remark", "11"));
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