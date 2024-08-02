package gtanks.system.dailybonus;

import gtanks.lobby.LobbyManager;
import gtanks.main.database.DatabaseManager;
import gtanks.main.database.impl.DatabaseManagerImpl;
import gtanks.services.annotations.ServicesInject;
import gtanks.system.dailybonus.crystalls.CrystallsBonusModel;
import gtanks.system.dailybonus.ui.DailyBonusUIModel;
import gtanks.users.User;
import gtanks.users.garage.Garage;
import gtanks.users.garage.GarageItemsLoader;
import gtanks.users.garage.items.Item;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DailyBonusService {
   private static final DailyBonusService instance = new DailyBonusService();
   @ServicesInject(
      target = DatabaseManager.class
   )
   private static final DatabaseManager databaseManager = DatabaseManagerImpl.instance();
   public static final String[] SUPPLIES_IDS = new String[]{"armor", "double_damage", "n2o"};
   private static Map<LobbyManager, DailyBonusService.Data> waitingUsers = new HashMap();
   private static DailyBonusUIModel uiModel = new DailyBonusUIModel();
   private static CrystallsBonusModel crystallsBonus = new CrystallsBonusModel();
   private static Random random = new Random();

   public static DailyBonusService instance() {
      return instance;
   }

   private DailyBonusService() {
   }

   public void userInited(LobbyManager lobby) {
      User user = lobby.getLocalUser();
      if (user.getRang() + 1 > 2 && this.canGetBonus(user)) {
         int fund = (int)(((double)(user.getRang() + 1) - 1.75D) * 2.4D) * 5;
         if (fund > 0) {
            DailyBonusService.Data bonusData = new DailyBonusService.Data();
            List<BonusListItem> bonusList = bonusData.bonusList;
            int rankFirstAid = ((Item)GarageItemsLoader.items.get("health")).rankId;
            int itemCrystalPrice = ((Item)GarageItemsLoader.items.get("health")).price;
            int countFirstAid = fund / itemCrystalPrice / 2;
            itemCrystalPrice = ((Item)GarageItemsLoader.items.get("mine")).price;
            int countMine = fund / itemCrystalPrice / 2;
            int rankMine = ((Item)GarageItemsLoader.items.get("mine")).rankId;
            if ((double)random.nextFloat() < 0.1D) {
               bonusData.type = 1;
            } else {
               bonusData.type = 3;
               int count;
               int price;
               Item bonus;
               int nextInt;
               if ((double)random.nextFloat() < 0.3D && countFirstAid > 0 && user.getRang() >= rankFirstAid) {
                  bonus = (Item)GarageItemsLoader.items.get("health");
                  price = bonus.price;
                  count = fund / price / 2 + 1;
               } else if ((double)random.nextFloat() < 0.3D && countMine > 0 && user.getRang() >= rankMine) {
                  bonus = (Item)GarageItemsLoader.items.get("mine");
                  price = bonus.price;
                  count = fund / price / 2 + 1;
               } else {
                  nextInt = random.nextInt(3);
                  bonus = (Item)GarageItemsLoader.items.get(SUPPLIES_IDS[nextInt]);
                  price = bonus.price;
                  count = fund / price / 2;
               }

               bonusList.add(new BonusListItem(bonus, count));
               fund -= price * count;
               nextInt = random.nextInt(3);
               bonus = (Item)GarageItemsLoader.items.get(SUPPLIES_IDS[nextInt]);
               price = bonus.price;
               if (((BonusListItem)bonusList.get(0)).getBonus().equals(bonus)) {
                  ((BonusListItem)bonusList.get(0)).addCount(fund / price);
               } else {
                  bonusList.add(new BonusListItem(bonus, fund / price));
               }
            }

            waitingUsers.put(lobby, bonusData);
            Garage garage = user.getGarage();

            BonusListItem item;
            Item bonusItem;
            for(Iterator var17 = bonusList.iterator(); var17.hasNext(); bonusItem.count += item.getCount()) {
               item = (BonusListItem)var17.next();
               bonusItem = garage.getItemById(item.getBonus().id);
               if (bonusItem == null) {
                  bonusItem = ((Item)GarageItemsLoader.items.get(item.getBonus().id)).clone();
                  garage.items.add(bonusItem);
               }
            }

            garage.parseJSONData();
            databaseManager.update(garage);
         }
      }

   }

   public void userLoaded(LobbyManager lobby) {
      DailyBonusService.Data data = (DailyBonusService.Data)waitingUsers.get(lobby);
      if (data != null) {
         if (data.type == 1) {
            crystallsBonus.applyBonus(lobby);
            uiModel.showCrystalls(lobby, crystallsBonus.getBonus(lobby.getLocalUser().getRang()));
         } else if (data.type == 3) {
            uiModel.showBonuses(lobby, data.bonusList);
         }

         waitingUsers.remove(lobby);
         this.saveLastDate(lobby.getLocalUser());
      }
   }

   public boolean canGetBonus(User user) {
      if (user == null) {
         return false;
      } else {
         boolean result = false;
         Date lastDate = user.getLastIssueBonus();
         Date now = new Date(System.currentTimeMillis() - 14400000L);
         Calendar nowCal = Calendar.getInstance();
         nowCal.setTime(now);
         Calendar lastCal = Calendar.getInstance();
         if (lastDate != null) {
            lastCal.setTime(lastDate);
         }

         if (lastDate == null || nowCal.get(5) > lastCal.get(5) || nowCal.get(2) > lastCal.get(2)) {
            result = true;
         }

         return result;
      }
   }

   private void saveLastDate(User user) {
      Date now = new Date(System.currentTimeMillis() - 14400000L);
      user.setLastIssueBonus(now);
      databaseManager.update(user);
   }

   private class Data {
      public int type = 0;
      public List<BonusListItem> bonusList = new ArrayList();

      public Data() {
      }
   }
}
