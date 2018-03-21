/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client.execute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;



public final class HttpClientUtil {

	
	private PoolingHttpClientConnectionManager httpClientConnectionManager = null;
	
	int readTimeout = 60000;

	int connectTimeout = 60000;

	int maxTotal = 200;

	int maxPerRoute = 3;

	// 请求重试机制
	HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
		
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			if (executionCount >= 3) {
				// 超过三次则不再重试请求
				return false;
			}
			if (exception instanceof InterruptedIOException) {
				// Timeout
				return false;
			}
			if (exception instanceof UnknownHostException) {
				// Unknown host
				return false;
			}
			if (exception instanceof ConnectTimeoutException) {
				// Connection refused
				return false;
			}
			if (exception instanceof SSLException) {
				// SSL handshake exception
				return false;
			}
			HttpClientContext clientContext = HttpClientContext.adapt(context);
			HttpRequest request = clientContext.getRequest();
			boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
			if (idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}
			return false;
		}
	};

	public HttpClientUtil() {
		
	}

	
	private void init() {
		httpClientConnectionManager = new PoolingHttpClientConnectionManager();
		httpClientConnectionManager.setMaxTotal(maxTotal);
		httpClientConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
	}
	
	private CloseableHttpClient getHttpClient() {
		init();
		// 创建全局的requestConfig
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
		// 声明重定向策略对象
		LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();

		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(httpClientConnectionManager)
				.setDefaultRequestConfig(requestConfig)
				.setRedirectStrategy(redirectStrategy).setRetryHandler(myRetryHandler)
				.build();

		return httpClient;
	}

	public String doPost(String url, String xml) {
		return doPost(url, xml, "UTF-8");
	}
	
	public String doPostGB2312(String url, String xml) {
		return doPost(url, xml, "GB2312");
	}
	
	public String doPost(String url, Map<String, String> map) {
		return doPost(url, map, "UTF-8");
	}
	//推送消息至FFS结算系统
	public String doPostFFS(String url, Map<String, String> map) {
		return doPost(url, map, "UTF-8");
	}
	//推送消息至FFS结算系统
	public String doPostFFS(String url, String jsonValue) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("jsonValue", jsonValue);
		return doPost(url, map, "UTF-8");
	}

	public String doPost(String url, Map<String, String> map, String charset) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = getHttpClient();
			//HttpClients.createDefault();
			httpPost = new HttpPost(url);
			// 设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> elem = (Entry<String, String>) iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
			}
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
				httpPost.setEntity(entity);
			}
			CloseableHttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
				response.close();
			}
			
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}

	public String doPost(String url, String xml, String charset) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			//httpClient = HttpClients.createDefault();
			httpClient = getHttpClient();
			httpPost = new HttpPost(url);

			StringEntity se = new StringEntity(xml, charset);

			httpPost.setEntity(se);
			CloseableHttpResponse response = null;
			try {
				response = httpClient.execute(httpPost);
				if (response != null) {
					HttpEntity resEntity = response.getEntity();
					if (resEntity != null) {
						result = EntityUtils.toString(resEntity, charset);
					}
				}
			}
			finally {
				if (response != null) {
					response.close();
				}
			}
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}
	
	
	public static String sendGet(String url, Map<String, String> paramMap) {
        String result = "";
        BufferedReader in = null;
        String paramString = "";
        try {
        	Iterator<Entry<String, String>> it = paramMap.entrySet().iterator();
    		
    		while (it.hasNext()) {
    			Entry<String, String> entry = it.next();
    			System.out.println(entry.getKey() + "--->" + entry.getValue());
                paramString = paramString + entry.getKey() + "=" + entry.getValue() + "&";
    		}
        	
    		if (!paramString.isEmpty()) {
    			paramString = "?" + paramString.substring(0, paramString.length()-1);
    			System.out.println(paramString);
    		}
        	
            String urlNameString = url + paramString;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
	
	

}
