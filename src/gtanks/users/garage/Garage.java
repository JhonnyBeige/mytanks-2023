package gtanks.users.garage;

import gtanks.commands.Type;
import gtanks.lobby.LobbyManager;
import gtanks.users.garage.enums.ItemType;
import gtanks.users.garage.items.Item;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Entity
@org.hibernate.annotations.Entity
@Table(
        name = "garages"
)
public class Garage implements Serializable {
   private static final long serialVersionUID = 2342422342L;
   @Id
   @GeneratedValue(
           strategy = GenerationType.IDENTITY
   )
   @Column(
           name = "uid",
           nullable = false,
           unique = true
   )
   private long id;
   @Column(
           name = "turrets",
           nullable = false
   )
   private String _json_turrets;
   @Column(
           name = "hulls",
           nullable = false
   )
   private String _json_hulls;
   @Column(
           name = "colormaps",
           nullable = false
   )
   private String _json_colormaps;
   @Column(
           name = "inventory",
           nullable = false
   )
   private String _json_inventory;
   @Column(
           name = "effects",
           nullable = false
   )
   private String _json_effects;
   @Column(
           name = "userid",
           nullable = false,
           unique = true
   )
   private String userId;
   @Transient
   public ArrayList<Item> items = new ArrayList();
   @Transient
   public Item mountTurret;
   @Transient
   public Item mountHull;
   @Transient
   public Item mountColormap;

   public Garage() {
      this.items.add(((Item)GarageItemsLoader.items.get("up_score_start")).clone());
      this.items.add(((Item)GarageItemsLoader.items.get("smoky")).clone());
      this.items.add(((Item)GarageItemsLoader.items.get("wasp")).clone());
      this.items.add(((Item)GarageItemsLoader.items.get("green")).clone());
      this.items.add(((Item)GarageItemsLoader.items.get("holiday")).clone());
      this.mountItem("wasp_m0");
      this.mountItem("smoky_m0");
      this.mountItem("green_m0");
   }

   public boolean containsItem(String id) {
      Iterator var3 = this.items.iterator();

      while(var3.hasNext()) {
         Item item = (Item)var3.next();
         if (item.id.equals(id)) {
            return true;
         }
      }

      return false;
   }

   public Item getItemById(String id) {
      Iterator var3 = this.items.iterator();

      while(var3.hasNext()) {
         Item item = (Item)var3.next();
         if (item.id.equals(id)) {
            return item;
         }
      }

      return null;
   }

   public boolean mountItem(String id) {
      Item item = this.getItemById(id.substring(0, id.length() - 3));
      if (item != null && Integer.parseInt(id.substring(id.length() - 1, id.length())) == item.modificationIndex) {
         if (item.itemType == ItemType.WEAPON) {
            this.mountTurret = item;
            return true;
         }

         if (item.itemType == ItemType.ARMOR) {
            this.mountHull = item;
            return true;
         }

         if (item.itemType == ItemType.COLOR) {
            this.mountColormap = item;
            return true;
         }
      }

      return false;
   }

   public static String formatAsExponential(long number) {
      String formattedNumber = String.format("%.12e", (double) number);
      return formattedNumber.replace(',', '.');
   }
   public static void updateLobbyTime(LobbyManager lobby, Item item) {
      String formattedTime = formatAsExponential(item.time);
      lobby.send(Type.LOBBY, "set_remaining_time", item.id + "_m0", formattedTime);
   }

   public boolean updateItem(String id) {
      Item item = this.getItemById(id.substring(0, id.length() - 3));
      int modificationID = Integer.parseInt(id.substring(id.length() - 1));
      if (modificationID < 3 && item.modificationIndex == modificationID) {
         ++item.modificationIndex;
         item.nextPrice = item.modifications[item.modificationIndex + 1 != 4 ? item.modificationIndex + 1 : item.modificationIndex].price;
         item.nextProperty = item.modifications[item.modificationIndex + 1 != 4 ? item.modificationIndex + 1 : item.modificationIndex].propertys;
         item.nextRankId = item.modifications[item.modificationIndex + 1 != 4 ? item.modificationIndex + 1 : item.modificationIndex].rank;
         this.replaceItems(this.getItemById(id.substring(0, id.length() - 3)), item);
         return true;
      } else {
         return false;
      }
   }

   public boolean buyItem(String id, int count) {
      Item temp = (Item)GarageItemsLoader.items.get(id.substring(0, id.length() - 3));
      if (temp.specialItem) {
         return false;
      } else {
         Item item = temp.clone();
         if (!this.items.contains(this.getItemById(id))) {
            if (item.isInventory) {
               item.count += count;
            }

            this.items.add(item);
            return true;
         } else if (item.isInventory) {
            Item fromUser = this.getItemById(id);
            fromUser.count += count;
            return true;
         } else {
            return false;
         }
      }
   }

   public Item buyItem(String id, int count, int nul) {
      id = id.substring(0, id.length() - 3);
      Item temp = (Item)GarageItemsLoader.items.get(id);
      if (temp.specialItem) {
         return null;
      } else {
         Item item = temp.clone();
         if (!this.items.contains(this.getItemById(id))) {
            if (item.itemType == ItemType.INVENTORY) {
               if (!id.equals("1000_scores")) {
                  item.count += count;
               }
            }

            if (!id.equals("1000_scores")) {
               this.items.add(item);
            }
            return item;
         } else if (item.itemType == ItemType.INVENTORY) {
            Item fromUser = this.getItemById(id);
            fromUser.count += count;
            return fromUser;
         } else {
            return null;
         }
      }
   }

   private void replaceItems(Item old, Item newItem) {
      if (this.items.contains(old)) {
         this.items.set(this.items.indexOf(old), newItem);
      }

   }

   public ArrayList<Item> getInventoryItems() {
      ArrayList<Item> _items = new ArrayList();
      Iterator var3 = this.items.iterator();

      while(var3.hasNext()) {
         Item item = (Item)var3.next();
         if (item.itemType == ItemType.INVENTORY) {
            _items.add(item);
         }
      }

      return _items;
   }

   public void parseJSONData() {
      JSONObject hulls = new JSONObject();
      JSONArray _hulls = new JSONArray();
      JSONObject colormaps = new JSONObject();
      JSONArray _colormaps = new JSONArray();
      JSONObject turrets = new JSONObject();
      JSONArray _turrets = new JSONArray();
      JSONObject inventory_items = new JSONObject();
      JSONArray _inventory = new JSONArray();
      JSONObject effects_items = new JSONObject();
      JSONArray _effects = new JSONArray();
      Iterator var10 = this.items.iterator();

      while(var10.hasNext()) {
         Item item = (Item)var10.next();
         JSONObject inventory;
         if (item.itemType == ItemType.ARMOR) {
            inventory = new JSONObject();
            inventory.put("id", item.id);
            inventory.put("modification", item.modificationIndex);
            inventory.put("mounted", item == this.mountHull);
            _hulls.add(inventory);
         }

         if (item.itemType == ItemType.COLOR) {
            inventory = new JSONObject();
            inventory.put("id", item.id);
            inventory.put("modification", item.modificationIndex);
            inventory.put("mounted", item == this.mountColormap);
            _colormaps.add(inventory);
         }

         if (item.itemType == ItemType.WEAPON) {
            inventory = new JSONObject();
            inventory.put("id", item.id);
            inventory.put("modification", item.modificationIndex);
            inventory.put("mounted", item == this.mountTurret);
            _turrets.add(inventory);
         }

         if (item.itemType == ItemType.INVENTORY) {
            inventory = new JSONObject();
            inventory.put("id", item.id);
            inventory.put("count", item.count);
            _inventory.add(inventory);
         }

         if (item.itemType == ItemType.PLUGIN) {
            inventory = new JSONObject();
            inventory.put("id", item.id);
            inventory.put("time", item.time);
            _effects.add(inventory);
         }
      }

      hulls.put("hulls", _hulls);
      colormaps.put("colormaps", _colormaps);
      turrets.put("turrets", _turrets);
      inventory_items.put("inventory", _inventory);
      effects_items.put("effects", _effects);
      this._json_colormaps = colormaps.toJSONString();
      this._json_hulls = hulls.toJSONString();
      this._json_turrets = turrets.toJSONString();
      this._json_inventory = inventory_items.toJSONString();
      this._json_effects = effects_items.toJSONString();
   }

   public void unparseJSONData() throws ParseException {
      this.items.clear();
      JSONParser parser = new JSONParser();
      JSONObject turrets = (JSONObject)parser.parse(this._json_turrets);
      JSONObject colormaps = (JSONObject)parser.parse(this._json_colormaps);
      JSONObject hulls = (JSONObject)parser.parse(this._json_hulls);
      JSONObject inventory;
      if (this._json_inventory != null && !this._json_inventory.isEmpty()) {
         inventory = (JSONObject)parser.parse(this._json_inventory);
      } else {
         inventory = null;
      }
      JSONObject effects;
      if ((this._json_effects != null) && (!this._json_effects.isEmpty()))
         effects = (JSONObject)parser.parse(this._json_effects);
      else {
         effects = null;
      }

      Iterator var7 = ((JSONArray)turrets.get("turrets")).iterator();

      Object inventory_item;
      JSONObject _item;
      Item item;
      while(var7.hasNext()) {
         inventory_item = var7.next();
         _item = (JSONObject)inventory_item;
         item = ((Item)GarageItemsLoader.items.get(_item.get("id"))).clone();
         item.modificationIndex = (int)(long)_item.get("modification");
         item.nextRankId = item.modifications[item.modificationIndex == 3 ? 3 : item.modificationIndex + 1].rank;
         item.nextPrice = item.modifications[item.modificationIndex == 3 ? 3 : item.modificationIndex + 1].price;
         this.items.add(item);
         if ((Boolean)_item.get("mounted")) {
            this.mountTurret = item;
         }
      }

      var7 = ((JSONArray)colormaps.get("colormaps")).iterator();

      while(var7.hasNext()) {
         inventory_item = var7.next();
         _item = (JSONObject)inventory_item;
         item = ((Item)GarageItemsLoader.items.get(_item.get("id"))).clone();
         item.modificationIndex = (int)(long)_item.get("modification");
         this.items.add(item);
         if ((Boolean)_item.get("mounted")) {
            this.mountColormap = item;
         }
      }

      var7 = ((JSONArray)hulls.get("hulls")).iterator();

      while(var7.hasNext()) {
         inventory_item = var7.next();
         _item = (JSONObject)inventory_item;
         item = ((Item)GarageItemsLoader.items.get(_item.get("id"))).clone();
         item.modificationIndex = (int)(long)_item.get("modification");
         item.nextRankId = item.modifications[item.modificationIndex == 3 ? 3 : item.modificationIndex + 1].rank;
         item.nextPrice = item.modifications[item.modificationIndex == 3 ? 3 : item.modificationIndex + 1].price;
         this.items.add(item);
         if ((Boolean)_item.get("mounted")) {
            this.mountHull = item;
         }
      }

      if (inventory != null) {
         var7 = ((JSONArray)inventory.get("inventory")).iterator();

         while(var7.hasNext()) {
            inventory_item = var7.next();
            _item = (JSONObject)inventory_item;
            item = ((Item)GarageItemsLoader.items.get(_item.get("id"))).clone();
            item.modificationIndex = 0;
            item.count = (int)(long)_item.get("count");
            if (item.itemType == ItemType.INVENTORY) {
               this.items.add(item);
            }
         }
      }

      if (effects != null) {
         var7 = ((JSONArray)effects.get("effects")).iterator();

         while (var7.hasNext()) {
            inventory_item = var7.next();
            _item = (JSONObject)inventory_item;
            item = ((Item)GarageItemsLoader.items.get(_item.get("id"))).clone();
            item.modificationIndex = 0;
            item.time = (long)_item.get("time");
            new SimpleTimer(this, item);
            if (item.itemType == ItemType.PLUGIN) {
               this.items.add(item);
            }
         }
      }

   }

   public String get_json_turrets() {
      return this._json_turrets;
   }

   public void set_json_turrets(String _json_turrets) {
      this._json_turrets = _json_turrets;
   }

   public String get_json_hulls() {
      return this._json_hulls;
   }

   public void set_json_hulls(String _json_hulls) {
      this._json_hulls = _json_hulls;
   }

   public String get_json_colormaps() {
      return this._json_colormaps;
   }

   public void set_json_colormaps(String _json_colormaps) {
      this._json_colormaps = _json_colormaps;
   }

   public String getUserId() {
      return this.userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String get_json_inventory() {
      return this._json_inventory;
   }

   public void set_json_inventory(String _json_inventory) {
      this._json_inventory = _json_inventory;
   }

   public String get_json_effects() {
      return this._json_effects;
   }

   public void set_json_effects(String _json_effects) {
      this._json_effects = _json_effects;
   }
}
