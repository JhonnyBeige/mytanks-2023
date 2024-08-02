package gtanks.rmi.payments.sfx;

import org.json.simple.JSONObject;

public class SFX {
    public static JSONObject sfx_data;

    public static String getSFX() {
        return sfx_data.toJSONString();
    }
}
