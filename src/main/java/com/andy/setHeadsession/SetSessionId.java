package com.andy.setHeadsession;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

/**
 * @author:andy
 * @version:1.0
 * @date:2016年8月22日
 */

public class SetSessionId {
	public static void main(String[] args) {
		
		String url = "http://10.10.10.90/urcweb/api/http.do?method=get_im_info";
		
		Map<String,String> header = new HashMap<String,String>();
		header.put("Cookie", "JSESSIONID=C1A34A2EC4C9423BB460E6F7005CA81E");
		System.out.println(httpGet(url,null,header));
	}
	
	/**
	 * 发送 get 请求
	 * @param url
	 * @param encode
	 * @param headers
	 * @return
	 */
	public static String httpGet(String url,String encode,Map<String,String> headers){
		if(encode == null){
			encode = "utf-8";
		}
		String content = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(url);
	
	    //设置 header
	    Header headerss[] = buildHeader(headers);
	    if(headerss != null && headerss.length > 0){
	    	httpGet.setHeaders(headerss);
	    }
	    HttpResponse http_response;
		try {
			http_response = httpclient.execute(httpGet);
			HttpEntity entity = http_response.getEntity();
			content = EntityUtils.toString(entity, encode);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
	        httpGet.releaseConnection();
	    }
	    return content;
	}
	
	/**
	 * 组装请求头
	 * @param params
	 * @return
	 */
	public static Header[] buildHeader(Map<String,String> params){
		Header[] headers = null;
		if(params != null && params.size() > 0){
			headers = new BasicHeader[params.size()];
			int i  = 0;
			for (Map.Entry<String, String> entry:params.entrySet()) {
				headers[i] = new BasicHeader(entry.getKey(),entry.getValue());
				i++;
			}
		}
		return headers;
	}
}
