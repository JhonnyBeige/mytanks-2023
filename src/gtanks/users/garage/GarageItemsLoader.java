package gtanks.users.garage;

import gtanks.battles.tanks.colormaps.Colormap;
import gtanks.battles.tanks.colormaps.ColormapsFactory;
import gtanks.system.localization.strings.LocalizedString;
import gtanks.system.localization.strings.StringsLocalizationBundle;
import gtanks.users.garage.enums.ItemType;
import gtanks.users.garage.enums.PropertyType;
import gtanks.users.garage.items.Item;
import gtanks.users.garage.items.PropertyItem;
import gtanks.users.garage.items.modification.ModificationInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GarageItemsLoader {
   public static HashMap<String, Item> items;
   private static int index = 1;

   public static void loadFromConfig(String turrets, String hulls, String colormaps, String inventory, String subscription, String kits) {
      if (items == null) {
         items = new HashMap();
      }

      for(int i = 0; i < 6; ++i) {
         StringBuilder builder = new StringBuilder();

         Throwable var7 = null;
         Object var8 = null;

         try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(i == 3 ? colormaps :
                                            i == 2 ? hulls :
                                                    i == 1 ? turrets :
                                                            i == 0 ? inventory :
                                                                    i == 5 ? kits : subscription)
                            ), StandardCharsets.UTF_8
                    )
            );

            String line;
            try {
               while((line = reader.readLine()) != null) {
                  builder.append(line);
               }
            } finally {
               if (reader != null) {
                  reader.close();
               }

            }
         } catch (Throwable var18) {
            if (var7 == null) {
               var7 = var18;
            } else if (var7 != var18) {
               var7.addSuppressed(var18);
            }

            try {
               throw var7;
            } catch (Throwable throwable) {
               throwable.printStackTrace();
            }
         }

         parseAndInitItems(
                 builder.toString(),
                 i == 3 ? ItemType.COLOR :
                         i == 2 ? ItemType.ARMOR :
                                 i == 1 ? ItemType.WEAPON :
                                         i == 0 ? ItemType.INVENTORY :
                                                 i == 5 ? ItemType.KIT :
                                                         ItemType.PLUGIN
         );
      }

   }

   private static void parseAndInitItems(String json, ItemType typeItem) {
      JSONParser parser = new JSONParser();

      try {
         Object obj = parser.parse(json);
         JSONObject jparser = (JSONObject)obj;
         JSONArray jarray = (JSONArray)jparser.get("items");

         for(int i = 0; i < jarray.size(); ++i) {
            JSONObject item = (JSONObject)jarray.get(i);
            LocalizedString name = StringsLocalizationBundle.registerString((String)item.get("name_ru"), (String)item.get("name_en"));
            LocalizedString description = StringsLocalizationBundle.registerString((String)item.get("description_ru"), (String)item.get("description_en"));
            String id = (String)item.get("id");
            if (id.equals("1000_scores")) {
               typeItem = ItemType.INVENTORY;
            }
            int priceM0 = item.get("price_m0") != null ? Integer.parseInt((String) item.get("price_m0")) : 0;
            int priceM1 = item.get("price_m1") != null ? Integer.parseInt((String) item.get("price_m1")) : priceM0;
            int priceM2 = item.get("price_m2") != null ? Integer.parseInt((String) item.get("price_m2")) : priceM0;
            int priceM3 = item.get("price_m3") != null ? Integer.parseInt((String) item.get("price_m3")) : priceM0;
            int rangM0 = Integer.parseInt((String)item.get("rang_m0"));
            int rangM1 = typeItem != ItemType.COLOR && typeItem != ItemType.INVENTORY && typeItem != ItemType.PLUGIN ? Integer.parseInt((String)item.get("rang_m1")) : rangM0;
            int rangM2 = typeItem != ItemType.COLOR && typeItem != ItemType.INVENTORY && typeItem != ItemType.PLUGIN ? Integer.parseInt((String)item.get("rang_m2")) : rangM0;
            int rangM3 = typeItem != ItemType.COLOR && typeItem != ItemType.INVENTORY && typeItem != ItemType.PLUGIN ? Integer.parseInt((String)item.get("rang_m3")) : rangM0;
            PropertyItem[] propertysItemM0 = null;
            PropertyItem[] propertysItemM1 = null;
            PropertyItem[] propertysItemM2 = null;
            PropertyItem[] propertysItemM3 = null;
            int countModification = typeItem == ItemType.COLOR ? 1 : (typeItem != ItemType.INVENTORY && typeItem != ItemType.PLUGIN ? 4 : (int)(long)item.get("count_modifications"));

            for(int m = 0; m < countModification; ++m) {
               JSONArray propertys = (JSONArray)item.get("propertys_m" + m);
               PropertyItem[] property = new PropertyItem[propertys.size()];

               for(int p = 0; p < propertys.size(); ++p) {
                  JSONObject prop = (JSONObject)propertys.get(p);
                  property[p] = new PropertyItem(getType((String)prop.get("type")), (String)prop.get("value"));
               }

               switch(m) {
               case 0:
                  propertysItemM0 = property;
                  break;
               case 1:
                  propertysItemM1 = property;
                  break;
               case 2:
                  propertysItemM2 = property;
                  break;
               case 3:
                  propertysItemM3 = property;
               }
            }

            if (typeItem == ItemType.COLOR || typeItem == ItemType.INVENTORY || typeItem == ItemType.PLUGIN) {
               propertysItemM1 = propertysItemM0;
               propertysItemM2 = propertysItemM0;
               propertysItemM3 = propertysItemM0;
            }

            int discount = item.get("discount") != null ? Integer.parseInt(item.get("discount").toString()) : 0;
            int discountedPriceM0 = (int) (priceM0 - (priceM0 * (discount / 100.0)));
            int discountedPriceM1 = (typeItem != ItemType.COLOR && typeItem != ItemType.INVENTORY && typeItem != ItemType.PLUGIN) ? (int) (priceM1 - (priceM1 * (discount / 100.0))) : discountedPriceM0;
            int discountedPriceM2 = (typeItem != ItemType.COLOR && typeItem != ItemType.INVENTORY && typeItem != ItemType.PLUGIN) ? (int) (priceM2 - (priceM2 * (discount / 100.0))) : discountedPriceM0;
            int discountedPriceM3 = (typeItem != ItemType.COLOR && typeItem != ItemType.INVENTORY && typeItem != ItemType.PLUGIN) ? (int) (priceM3 - (priceM3 * (discount / 100.0))) : discountedPriceM0;
            boolean specialItem = item.get("special_item") == null ? false : (Boolean)item.get("special_item");
            if (typeItem == ItemType.KIT) {
               ModificationInfo[] mods = new ModificationInfo[1];
               mods[0] = new ModificationInfo(id + "_m0_preview", discountedPriceM0, rangM0);

               String kitInfo = (String) item.get("json_kit_info") != null ? (String) item.get("json_kit_info") : "";
               items.put(id, new Item(id, description, typeItem == ItemType.INVENTORY || typeItem == ItemType.PLUGIN, index, propertysItemM0, typeItem, 0, name, propertysItemM1, discountedPriceM1, rangM1, discountedPriceM0, discount, rangM0, mods, specialItem, 0, kitInfo));
            } else {
               ModificationInfo[] mods = new ModificationInfo[4];
               mods[0] = new ModificationInfo(id + "_m0", discountedPriceM0, rangM0);
               mods[0].propertys = propertysItemM0;
               mods[1] = new ModificationInfo(id + "_m1", discountedPriceM1, rangM1);
               mods[1].propertys = propertysItemM1;
               mods[2] = new ModificationInfo(id + "_m2", discountedPriceM2, rangM2);
               mods[2].propertys = propertysItemM2;
               mods[3] = new ModificationInfo(id + "_m3", discountedPriceM3, rangM3);
               mods[3].propertys = propertysItemM3;

               items.put(id, new Item(id, description, typeItem == ItemType.INVENTORY || typeItem == ItemType.PLUGIN, index, propertysItemM0, typeItem, 0, name, propertysItemM1, discountedPriceM1, rangM1, discountedPriceM0, discount, rangM0, mods, specialItem, 0, ""));
               ++index;
               if (typeItem == ItemType.COLOR) {
                  ColormapsFactory.addColormap(id + "_m0", new Colormap() {
                     {
                        PropertyItem[] var5;
                        int var4 = (var5 = mods[0].propertys).length;

                        for(int var3 = 0; var3 < var4; ++var3) {
                           PropertyItem _property = var5[var3];
                           this.addResistance(ColormapsFactory.getResistanceType(_property.property), GarageItemsLoader.getInt(_property.value.replace("%", "")));
                        }

                     }
                  });
               }
            }


         }
      } catch (ParseException var29) {
         var29.printStackTrace();
      }

   }

   private static int getInt(String str) {
      try {
         return Integer.parseInt(str);
      } catch (Exception var2) {
         return 0;
      }
   }

   private static PropertyType getType(String s) {
      PropertyType[] var4;
      int var3 = (var4 = PropertyType.values()).length;

      for(int var2 = 0; var2 < var3; ++var2) {
         PropertyType type = var4[var2];
         if (type.toString().equals(s)) {
            return type;
         }
      }

      return null;
   }
}
