package gtanks.battles.tanks.loaders;

import gtanks.StringUtils;
import gtanks.battles.tanks.hulls.Hull;
import gtanks.exceptions.GTanksServerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HullsFactory {
   private static HashMap<String, Hull> hulls = new HashMap();

   public static void init(String path2configs) {
      hulls.clear();

      try {
         File file = new File(path2configs);
         File[] var5;
         int var4 = (var5 = file.listFiles()).length;

         for(int var3 = 0; var3 < var4; ++var3) {
            File config = var5[var3];
            parse(config);
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   private static void parse(File config) throws FileNotFoundException, IOException, ParseException {
      JSONObject jobj = (JSONObject)(new JSONParser()).parse((Reader)(new FileReader(config)));
      String type = (String)jobj.get("type");
      Iterator var4 = ((JSONArray)jobj.get("modifications")).iterator();

      while(var4.hasNext()) {
         Object obj = var4.next();
         JSONObject jt = (JSONObject)obj;
         Hull hull = new Hull((float)(double)jt.get("mass"), (float)(double)jt.get("power"), (float)(double)jt.get("speed"), (float)(double)jt.get("turn_speed"), (float)(Long)jt.get("hp"), (float)(double)jt.get("maxSpeed"), (float)(double)jt.get("acceleration"), (float)(double)jt.get("reverseAcceleration"), (float)(double)jt.get("deceleration"), (float)(double)jt.get("turnMaxSpeed"), (float)(double)jt.get("turnAcceleration"), (float)(double)jt.get("turnReverseAcceleration"), (float)(double)jt.get("turnDeceleration"), (float)(double)jt.get("sideAcceleration"), (float)(double)jt.get("damping"));
         hulls.put(StringUtils.concatStrings(type, "_", (String)jt.get("modification")), hull);
      }

   }

   public static Hull getHull(String id) {
      Hull hull = (Hull)hulls.get(id);
      if (hull == null) {
         new GTanksServerException("Hull with id " + id + " is null!");
         return null;
      } else {
         return hull;
      }
   }
}
