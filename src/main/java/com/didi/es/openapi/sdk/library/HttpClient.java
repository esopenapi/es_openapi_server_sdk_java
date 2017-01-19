package com.didi.es.openapi.sdk.library;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

/**
 * http请求客户端类
 */
public class HttpClient {

    /**
     * 请求方法（get/post）
     */
    public static final String HTTP_METHOD_GET  = "get";
    public static final String HTTP_METHOD_POST = "post";

    /**
     * http请求
     * @param method                        请求方法（get/post）
     * @param url                           请求地址
     * @param paramMap                      参数表
     * @param timeout_socket                从服务器读取数据的timeout(ms)
     * @param timeout_connection            和服务器建立连接的timeout(ms)
     * @param timeout_connection_request    从连接池获取连接的timeout(ms)
     * @return                              请求返回值
     */
    public static String request(String method, String url, Map<String, String> paramMap,
                                 int timeout_socket, int timeout_connection, int timeout_connection_request) {

        HttpRequestBase httpReqeust = null;

        if (HTTP_METHOD_POST.equals(method)) {
            httpReqeust = new HttpPost(url);
            if (null != paramMap) {
                httpReqeust = setRequestBody((HttpPost)httpReqeust, paramMap);
            }
        } else {
            if (null != paramMap) {
                url = httpBuildQuery(url, paramMap);
            }
            httpReqeust = new HttpGet(url);
        }

        // 传输超时时间
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout_socket)
                .setConnectionRequestTimeout(timeout_connection_request)
                .setConnectTimeout(timeout_connection)
                .build();

        httpReqeust.setConfig(requestConfig);

        try {
            CloseableHttpResponse response = client.execute(httpReqeust);
            try {
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    return EntityUtils.toString(response.getEntity());
                } else {
                    String exceptionInfo = String.format("http response error, URL:{%s} METHOD:{%s} CODE:{%s} CONTENT:{%s}",
                            url, method, statusLine.getStatusCode(), statusLine.getReasonPhrase());
                    throw new RuntimeException(exceptionInfo);
                }
            } finally {
                response.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("execute http " + method + " request failed! url:" + url, e);

        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    /**
     * 将参数拼接到url
     *
     * @param url       接口地址
     * @param paramMap  请求参数
     * @return          带参数的url
     */
    private static String httpBuildQuery(String url, Map<String, String> paramMap) {
        StringBuffer paramStr = new StringBuffer("");
        for (Map.Entry<String, String> item : paramMap.entrySet()) {
            if(paramStr.toString().equals("")){
                paramStr.append("?");
            }else{
                paramStr.append("&");
            }
            paramStr.append(item.getKey().trim()).append("=").append(item.getValue().trim());
        }
        return url + paramStr.toString();
    }

    /**
     * 设置POST提交的参数
     *
     * @param httpPost   POST对象
     * @param paramMap   参数映射
     */
    private static HttpPost setRequestBody(HttpPost httpPost, Map<String, String> paramMap) {
        Set<String> keySet = paramMap.keySet();
        Iterator<String> it = keySet.iterator();
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        while (it.hasNext()) {
            String key = it.next();
            String value = paramMap.get(key);
            NameValuePair nameValuePair = new BasicNameValuePair(key, value);
            paramList.add(nameValuePair);
        }

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, Consts.UTF_8);

        httpPost.setEntity(entity);

        return httpPost;
    }
}
