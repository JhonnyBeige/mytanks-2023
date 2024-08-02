package gtanks.rmi.payments.shop;

import org.json.simple.JSONObject;

public class SHOP {
    public static JSONObject shop_data;

    public static String getShop() {
        return shop_data.toJSONString();
    }
}
