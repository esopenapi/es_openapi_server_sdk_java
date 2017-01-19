
# 滴滴企业版openapi服务端java接入sdk

## 一、sdk使用说明  
### 引入come-didi-es-openapi-sdk-1.0.jar   
### 引入所依赖的第三方jar包（详见pom.xml）    

## 二、Request类帮助

### 使用new或依赖注入Request对象   
    
    Request request = new Request(
    	"myClentId",    
     	"mySignKey",
    	"myClientSecret",
     	"client_credentials",
     	"10000000003"
    );

### 调用用车类接口（v1/Auth/authorize）  

	Map<String, String> content = request.authorizeV1();

### 调用管理类接口（river/Auth/authorize）授权方法  

	Map<String, String> content = request.authorizeRiver();	

### 调用get/post方法(无需传入client_id、timestamp、sign)，以订单查询接口为例  

     Map<String, String> paramMap = new HashMap<String, String>();
     paramMap.put("access_token", content.get("access_token"));
     paramMap.put("company_id", "1231231231");
     paramMap.put("order_id", "1231231231"); 
     Map<String, String> content = request.get("/river/Order/get", paramMap);

