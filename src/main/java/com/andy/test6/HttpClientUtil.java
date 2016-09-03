package com.andy.test6;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient工具类
 * 
 * @author:andy
 * @version:1.0
 * @date:2016年8月19日
 */
public class HttpClientUtil {
	private static final Log logger = LogFactory.getLog(HttpClientUtil.class);
	private static PoolingHttpClientConnectionManager phccm;
	private static CloseableHttpClient httpClient = null;
	private final static Object syncLock = new Object();
	// 最大连接数默认200
	private static int maxTotalPool = 200;
	// 每个路由默认20
	private static int defMaxPerRoute = 20;
	// 目标主机最大路由默认50
	private static int maxObjRoute = 50;

	private static int socketTimeout = 10 * 1000;
	private static int connectTimeout = 10 * 1000;
	private static int connectionRequestTimeout = 10 * 1000;
	
	public static String hostname_base;
	private static int port = 80;
	
	public static String test = "";
	
	static{
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void init() throws Exception {
		//获取配置文件初始化
		InputStream in = HttpClientUtil.class.getResourceAsStream("httpclient.properties");
		Properties props = new Properties();
		props.load(in);
		
		hostname_base = props.getProperty("hostname");
		if(hostname_base == null){
			throw new Exception("hostname 取不到");
		}
		hostname_base = props.getProperty("hostnamebase");
		port = Integer.parseInt(props.getProperty("port"));
		maxTotalPool = Integer.parseInt(props.getProperty("maxTotalPool","200"));
		defMaxPerRoute = Integer.parseInt(props.getProperty("defMaxPerRoute","50"));
		maxObjRoute = Integer.parseInt(props.getProperty("maxObjRoute","100"));
		socketTimeout = Integer.parseInt(props.getProperty("socketTimeout"));
		connectTimeout = Integer.parseInt(props.getProperty("connectTimeout"));
		connectionRequestTimeout = Integer.parseInt(props.getProperty("connectionRequestTimeout"));
		
//		System.out.println(hostname);
		
	}

	public static CloseableHttpClient getHttpClient(String url) {
		String hostname = url.split("/")[2];
		
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
		if (httpClient == null) {
			synchronized (syncLock) {
				if (httpClient == null) {
					httpClient = createHttpClient(maxTotalPool,defMaxPerRoute, maxObjRoute, hostname, port);
				}
			}
		}
		return httpClient;
	}
	/**
	 * 创建httpclient
	 * @param maxTotalPool
	 * @param defMaxPerRoute
	 * @param maxObjRoute
	 * @param hostname
	 * @param port
	 * @return
	 */
	public static CloseableHttpClient createHttpClient(int maxTotalPool, int defMaxPerRoute, 
			int maxObjRoute, String hostname, int port) {
		PoolingHttpClientConnectionManager phccm = new PoolingHttpClientConnectionManager();
		phccm.setMaxTotal(maxTotalPool);// 设置最大连接
		phccm.setDefaultMaxPerRoute(defMaxPerRoute);

		HttpHost httpHost = new HttpHost(hostname, port);
		phccm.setMaxPerRoute(new HttpRoute(httpHost), maxObjRoute);

		HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {

			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				// 如果已经重试了5次，就放弃
				if (executionCount > 5) {
					return false;
				}
				if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
					return true;
				}
				if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
					return false;
				}
				if (exception instanceof InterruptedIOException) {// 超时
					return false;
				}
				if (exception instanceof UnknownHostException) {// 目标服务器不可达
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
					return false;
				}
				if (exception instanceof SSLException) {// SSL握手异常
					return false;
				}

				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};

		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(phccm).setRetryHandler(retryHandler)
				.build();

		return httpClient;
	}
	/**
	 * post 请求
	 * @param url
	 * @param param
	 * @return
	 */
	public static String post(String url,Map<String,Object> params){
		HttpPost httppost = new HttpPost(url);
		config(httppost);
		setPostParams(httppost, params);
		CloseableHttpResponse response = null;
		
		try {
			response = getHttpClient(url).execute(httppost,HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, "UTF-8");
			EntityUtils.consume(entity);
			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(response != null){
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	private static void setPostParams(HttpPost httppost, Map<String, Object> params) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<String> keys = params.keySet();
		for (String key : keys) {
			nvps.add(new BasicNameValuePair(key,params.get(key).toString()));
		}
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
	}

	private static void config(HttpRequestBase httpRequestBase) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        httpRequestBase.setConfig(requestConfig);
		
	}
	
	static class PostRunnable implements Runnable{
		private String reqestPath;
		private IWebAppProcess todo;
		Map<String,Object> params;
		PostRunnable(String reqestPath,IWebAppProcess todo,Map<String,Object> params){
			this.reqestPath = reqestPath;
			this.todo = todo;
			this.params = params;
		}
		public void run() {
			String result = HttpClientUtil.post(hostname_base+reqestPath, params);
			todo.dealResult(result);
		}
	}

	public static void main(String[] args) {
//		String a = HttpClientUtil.test;
		String url = "http://weixintg.tunnel.qydev.com/api/v1/top_up";
//		String hostname = url.split("/")[2];
//		System.out.println(url.split("/")[2]);
		
		

	}

}
