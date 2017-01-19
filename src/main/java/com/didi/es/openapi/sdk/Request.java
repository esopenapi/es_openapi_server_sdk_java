package com.didi.es.openapi.sdk;

import com.didi.es.openapi.sdk.library.HttpClient;
import com.didi.es.openapi.sdk.library.MD5Util;


import  net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 请求代理类
 */
public class Request {


    /**
     * client相关配置
     */
    private String clientId     = "";
    private String signKey      = "";
    private String clientSecret = "";
    private String grantType    = "";
    private String phone        = "";


    /**
     * 默认请求超时时间(ms)
     */
    public static int defaultTimeoutSocket             = 2000;
    public static int defaultTimeoutConnection         = 1000;
    public static int defaultTimeoutConnectionRequest  = 1000;

    /**
     * 接口域名
     */
    public static String host = "http://api.es.xiaojukeji.com";

    /**
     * 构造函数
     * @param clientId
     * @param signKey
     * @param clientSecret
     * @param grantType
     * @param phone
     */
    public Request(String clientId, String signKey, String clientSecret, String grantType, String phone) {
        this.clientId       = clientId;
        this.signKey        = signKey;
        this.clientSecret   = clientSecret;
        this.grantType      = grantType;
        this.phone          = phone;
    }

    /**
     * 叫车类接口授权认证
     * @return 授权认证信息
     */
    public Map<String, String> authorizeV1() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("client_secret", clientSecret);
        paramMap.put("grant_type", grantType);
        paramMap.put("phone", phone);
        return post("/v1/Auth/authorize", paramMap);
    }

    /**
     * 管理类接口授权认证
     * @return 授权认证信息
     */
    public Map<String, String> authorizeRiver() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("client_secret", clientSecret);
        paramMap.put("grant_type", grantType);
        paramMap.put("phone", phone);
        return post("/river/Auth/authorize", paramMap);
    }

    /**
     * get请求
     * @param uri       接口地址
     * @param paramMap  请求参数
     * @return          map格式返回值
     */
    public Map<String, String> get (String uri, Map<String, String> paramMap)
    {
        return request(HttpClient.HTTP_METHOD_GET, uri, paramMap,
                defaultTimeoutSocket, defaultTimeoutConnection, defaultTimeoutConnectionRequest);
    }


    /**
     * get请求
     * @param uri                           接口地址
     * @param paramMap                      请求参数
     * @param timeoutSocket                 从服务器读取数据的timeout(ms)
     * @param timeoutConnection             和服务器建立连接的timeout(ms)
     * @param timeoutConnectionRequest      从连接池获取连接的timeout(ms)
     * @return                              map格式返回值
     */
    public Map<String, String> get (String uri, Map<String, String> paramMap,
                                           int timeoutSocket, int timeoutConnection, int timeoutConnectionRequest)
    {
        return request(HttpClient.HTTP_METHOD_GET, uri, paramMap,
                timeoutSocket, timeoutConnection, timeoutConnectionRequest);
    }

    /**
     * post请求
     * @param uri       接口地址
     * @param paramMap  请求参数
     * @return          map格式返回值
     */
    public Map<String, String> post (String uri, Map<String, String> paramMap)
    {
        return request(HttpClient.HTTP_METHOD_POST, uri, paramMap,
                defaultTimeoutSocket, defaultTimeoutConnection, defaultTimeoutConnectionRequest);
    }

    /**
     * post请求
     * @param uri                           接口地址
     * @param paramMap                      请求参数
     * @param timeoutSocket                 从服务器读取数据的timeout(ms)
     * @param timeoutConnection             和服务器建立连接的timeout(ms)
     * @param timeoutConnectionRequest      从连接池获取连接的timeout(ms)
     * @return                              map格式返回值
     */
    public Map<String, String> post (String uri, Map<String, String> paramMap,
                                           int timeoutSocket, int timeoutConnection, int timeoutConnectionRequest)
    {
        return request(HttpClient.HTTP_METHOD_POST, uri, paramMap,
                timeoutSocket, timeoutConnection, timeoutConnectionRequest);
    }

    /**
     * 请求（get/post）
     * @param method                        请求方法（get/post）
     * @param uri                           接口uri
     * @param paramMap                      参数
     * @param timeoutSocket                 从服务器读取数据的timeout(ms)
     * @param timeoutConnection             和服务器建立连接的timeout(ms)
     * @param timeoutConnectionRequest      从连接池获取连接的timeout(ms)
     * @return                              请求结果
     */
    public Map<String, String> request (String method, String uri, Map<String, String> paramMap,
                                                int timeoutSocket, int timeoutConnection, int timeoutConnectionRequest)
    {
        String url = host + uri;

        addCommonParamMap(paramMap);
        sign(paramMap);

        String content = HttpClient.request(method, url, paramMap, timeoutSocket, timeoutConnection, timeoutConnectionRequest);

        return JSON2Map(content);
    }

    /**
     * 签名计算
     * @param sinParameters
     */
    private void sign (Map<String, String> sinParameters)
    {
        sinParameters.put("sign_key", signKey);

        List<String> keys = new ArrayList<String>(sinParameters.keySet());
        Collections.sort(keys);

        List<String> keyValueList = new ArrayList<String>();
        for (String key : keys) {
            String value = sinParameters.get(key);
            if (value != null) {
                keyValueList.add(key + "=" + value);
            }
        }
        String queryString = StringUtils.join(keyValueList, "&");
        String sign = MD5Util.getMD5Str(queryString);

        sinParameters.put("sign", sign);
        sinParameters.remove("sign_key");

    }

    /**
     * json转map
     * @return
     */
    private static Map<String, String> JSON2Map(String jsonMapStr) {
        Map<String, String> map = new HashMap<String, String>();
        JSONObject jsonMap = JSONObject.fromObject(jsonMapStr);
        Iterator<String> it = jsonMap.keys();
        while(it.hasNext()) {
            String key = it.next().toString();
            String u = jsonMap.get(key).toString();
            map.put(key, u);
        }
        return map;
    }

    /**
     * 添加通用参数
     * @param paramMap
     */
    private void addCommonParamMap(Map<String, String> paramMap)
    {
        if (null == paramMap) {
            paramMap = new HashMap<String, String>();
        }
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", "" + System.currentTimeMillis() / 1000);
    }

}
