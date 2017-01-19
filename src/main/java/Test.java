
import java.util.*;

import com.didi.es.openapi.sdk.Request;

public class Test {


    public static void main(String[] args) {


//        Request request = new Request(
//                "738ee9a953bf6de478b9f71a601e297f_test",
//                "735f2BED73d88593bd12",
//                "860dd96b70a3d37269509aab31d66d1f",
//                "client_credentials",
//                "11000003343"
//        );
        Request request = new Request(
                "myClentId",
                "mySignKey",
                "myClientSecret",
                "client_credentials",
                "10000000003"
        );

        Map<String, String> content = request.authorizeRiver();
        System.out.println(content.toString());

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("access_token", content.get("access_token"));
        paramMap.put("company_id", "1231231231");
        paramMap.put("order_id", "1231231231");

        content = request.get("/river/Order/get", paramMap);


        System.out.println(content.toString());
    }


}
