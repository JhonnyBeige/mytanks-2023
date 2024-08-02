package gtanks.rmi.payments.kit.loader;

import gtanks.logger.Logger;
import gtanks.rmi.payments.kit.Kit;
import gtanks.rmi.payments.kit.KitItem;
import gtanks.rmi.payments.kit.KitItemType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class KitsLoader {
   private static final Map<String, Kit> kits = new HashMap();

   public static void load(String config) throws FileNotFoundException, IOException, ParseException {
      load(new File(config));
   }

   public static void load(File file) throws FileNotFoundException, IOException, ParseException {
      JSONParser parser = new JSONParser();
      JSONObject json = (JSONObject)parser.parse((Reader)(new FileReader(file)));
      Iterator var4 = ((JSONArray)json.get("kits")).iterator();

      while(var4.hasNext()) {
         Object _kit = var4.next();
         JSONObject kit = (JSONObject)_kit;
         String kitId = (String)kit.get("kit_id");
         Logger.log("Load " + kit.get("name") + "(" + kitId + ") kit...");
         List<KitItem> kitItems = new ArrayList();
         Iterator var9 = ((JSONArray)kit.get("items")).iterator();

         while(var9.hasNext()) {
            Object _item = var9.next();
            JSONObject item = (JSONObject)_item;
            KitItemType type = KitItemType.valueOf((String)item.get("type"));
            String itemId = (String)item.get("item_id");
            int count = (int)(long)item.get("count");
            kitItems.add(new KitItem(type, itemId, count));
         }

         kits.put(kitId, new Kit(kitItems, (int)(long)kit.get("price")));
      }

   }
}
