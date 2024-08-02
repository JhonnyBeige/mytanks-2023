package gtanks.users.garage.items;

import gtanks.StringUtils;
import gtanks.dumpers.Dumper;
import gtanks.system.localization.Localization;
import gtanks.system.localization.strings.LocalizedString;
import gtanks.users.garage.enums.ItemType;
import gtanks.users.garage.items.modification.ModificationInfo;

public class Item implements Dumper {
   public String id;
   public LocalizedString description;
   public boolean isInventory;
   public int index;
   public PropertyItem[] propetys;
   public ItemType itemType;
   public int modificationIndex;
   public LocalizedString name;
   public PropertyItem[] nextProperty;
   public int nextPrice;
   public int nextRankId;
   public int price;
   public int discount;
   public int rankId;
   public ModificationInfo[] modifications;
   public boolean specialItem;
   public int count;
   public String json_kit_info = "";
   public long time = 0L;

   public Item(String id, LocalizedString description, boolean isInventory, int index, PropertyItem[] propetys, ItemType weapon, int modificationIndex, LocalizedString name, PropertyItem[] nextProperty, int nextPrice, int nextRankId, int price, int discount, int rankId, ModificationInfo[] modifications, boolean specialItem, int count, String json_kit_info) {
      this.id = id;
      this.description = description;
      this.isInventory = isInventory;
      this.index = index;
      this.propetys = propetys;
      this.itemType = weapon;
      this.modificationIndex = modificationIndex;
      this.name = name;
      this.nextProperty = nextProperty;
      this.nextPrice = nextPrice;
      this.nextRankId = nextRankId;
      this.price = price;
      this.discount = discount;
      this.rankId = rankId;
      this.modifications = modifications;
      this.specialItem = specialItem;
      this.count = count;
      this.json_kit_info = json_kit_info;
      if (id.equals("no_supplies")) {
         this.time = System.currentTimeMillis() + ((3600000L * 24) * 31);
      }
      else if (id.equals("up_score_start")) {
         this.time = System.currentTimeMillis() + ((3600000L * 24) * 7);
      }
      else if (id.equals("up_score_small")) {
         this.time = System.currentTimeMillis() + ((3600000L * 24) * 31);
      }
      else if (id.equals("up_score")) {
         this.time = System.currentTimeMillis() + ((3600000L * 24) * 31);
      }
      else if (id.equals("double_crystalls")) {
         this.time = System.currentTimeMillis() + ((3600000L * 24) * 1);
      }
   }

   public String getId() {
      return StringUtils.concatStrings(this.id, "_m", String.valueOf(this.modificationIndex));
   }

   public Item clone() {
      return new Item(this.id, this.description, this.isInventory, this.index, this.propetys, this.itemType, this.modificationIndex, this.name, this.nextProperty, this.nextPrice, this.nextRankId, this.price, this.discount, this.rankId, this.modifications, this.specialItem, this.count, this.json_kit_info);
   }

   public String dump() {
      return StringUtils.concatStrings("-------DUMP GARAGE ITEM------\n", "\tid: ", this.id, "\n", "\tinventoryItem: ", String.valueOf(this.isInventory), "\n", "\tindex: ", String.valueOf(this.index), "\n", "\tname: ", this.name.localizatedString(Localization.RU), "\n", "\tprice: ", String.valueOf(this.price), "\n", "\trandId: ", String.valueOf(this.rankId), "\n", "\tspecialItem: ", String.valueOf(this.specialItem), "\n", "-------------------------------", "\n");
   }
}
