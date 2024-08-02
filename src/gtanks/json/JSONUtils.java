package gtanks.json;

import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.bonuses.Bonus;
import gtanks.battles.chat.BattleChatMessage;
import gtanks.battles.ctf.CTFModel;
import gtanks.battles.ctf.flags.FlagServer;
import gtanks.battles.maps.Map;
import gtanks.battles.maps.MapsLoader;
import gtanks.battles.mines.ServerMine;
import gtanks.battles.tanks.Tank;
import gtanks.battles.tanks.math.Vector3;
import gtanks.battles.tanks.weapons.EntityType;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.WeaponWeakeningData;
import gtanks.battles.tanks.weapons.flamethrower.FlamethrowerEntity;
import gtanks.battles.tanks.weapons.frezee.FrezeeEntity;
import gtanks.battles.tanks.weapons.isida.IsidaEntity;
import gtanks.battles.tanks.weapons.ricochet.RicochetEntity;
import gtanks.battles.tanks.weapons.shaft.ShaftEntity;
import gtanks.battles.tanks.weapons.snowman.SnowmanEntity;
import gtanks.battles.tanks.weapons.thunder.ThunderEntity;
import gtanks.battles.tanks.weapons.twins.TwinsEntity;
import gtanks.battles.tanks.weapons.vulcan.VulcanEntity;
import gtanks.collections.FastHashMap;
import gtanks.lobby.battles.BattleInfo;
import gtanks.lobby.battles.BattlesList;
import gtanks.lobby.chat.ChatMessage;
import gtanks.lobby.top.HallOfFame;
import gtanks.main.database.DatabaseManager;
import gtanks.main.database.impl.DatabaseManagerImpl;
import gtanks.services.AutoEntryServices;
import gtanks.services.annotations.ServicesInject;
import gtanks.users.TypeUser;
import gtanks.users.User;
import gtanks.users.garage.Garage;
import gtanks.users.garage.GarageItemsLoader;
import gtanks.users.garage.enums.ItemType;
import gtanks.users.garage.items.Item;
import gtanks.users.garage.items.PropertyItem;
import gtanks.users.garage.items.modification.ModificationInfo;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONUtils {
   @ServicesInject(
      target = AutoEntryServices.class
   )
   private static AutoEntryServices autoEntryServices = AutoEntryServices.instance();
   @ServicesInject(
      target = AutoEntryServices.class
   )
   private static DatabaseManager databaseManager = DatabaseManagerImpl.instance();
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$gtanks$battles$tanks$weapons$EntityType;

   public static String parseConfiguratorEntity(Object entity, Class clazz) {
      JSONObject jobj = new JSONObject();

      try {
         Field[] var6;
         int var5 = (var6 = clazz.getDeclaredFields()).length;

         for(int var4 = 0; var4 < var5; ++var4) {
            Field field = var6[var4];
            field.setAccessible(true);
            jobj.put(field.getName(), field.get(entity));
         }
      } catch (IllegalAccessException | IllegalArgumentException var7) {
         var7.printStackTrace();
      }

      return jobj.toJSONString();
   }

   public static String parseInitMinesComand(FastHashMap<BattlefieldPlayerController, ArrayList<ServerMine>> mines) {
      JSONObject jobj = new JSONObject();
      JSONArray array = new JSONArray();
      Iterator var4 = mines.values().iterator();

      while(var4.hasNext()) {
         ArrayList<ServerMine> userMines = (ArrayList)var4.next();
         Iterator var6 = userMines.iterator();

         while(var6.hasNext()) {
            ServerMine mine = (ServerMine)var6.next();
            JSONObject _mine = new JSONObject();
            _mine.put("ownerId", mine.getOwner().tank.id);
            _mine.put("mineId", mine.getId());
            _mine.put("x", mine.getPosition().x);
            _mine.put("y", mine.getPosition().y);
            _mine.put("z", mine.getPosition().z);
            array.add(_mine);
         }
      }

      jobj.put("mines", array);
      return jobj.toJSONString();
   }

   public static String parsePutMineComand(ServerMine mine) {
      JSONObject jobj = new JSONObject();
      jobj.put("mineId", mine.getId());
      jobj.put("userId", mine.getOwner().tank.id);
      jobj.put("x", mine.getPosition().x);
      jobj.put("y", mine.getPosition().y);
      jobj.put("z", mine.getPosition().z);
      return jobj.toJSONString();
   }

   public static String parseInitInventoryComand(Garage garage) {
      JSONObject jobj = new JSONObject();
      JSONArray array = new JSONArray();
      Iterator var4 = garage.getInventoryItems().iterator();

      while(var4.hasNext()) {
         Item item = (Item)var4.next();
         JSONObject io = new JSONObject();
         io.put("id", item.id);
         io.put("count", item.count);
         io.put("slotId", item.index);
         io.put("itemEffectTime", item.id.equals("mine") ? 20 : (item.id.equals("health") ? 20 : 55));
         io.put("itemRestSec", 10);
         array.add(io);
      }

      jobj.put("items", array);
      return jobj.toJSONString();
   }

   public static String parseRemovePlayerComand(BattlefieldPlayerController player) {
      JSONObject jobj = new JSONObject();
      jobj.put("battleId", player.battle.battleInfo.battleId);
      jobj.put("id", player.getUser().getNickname());
      return jobj.toJSONString();
   }

   public static String parseRemovePlayerComand(String userId, String battleid) {
      JSONObject jobj = new JSONObject();
      jobj.put("battleId", battleid);
      jobj.put("id", userId);
      return jobj.toJSONString();
   }

   public static String parseAddPlayerComand(BattlefieldPlayerController player, BattleInfo battleInfo) {
      JSONObject obj = new JSONObject();
      obj.put("battleId", battleInfo.battleId);
      obj.put("id", player.getUser().getNickname());
      obj.put("kills", player.statistic.getScore());
      obj.put("name", player.getUser().getNickname());
      obj.put("rank", player.getUser().getRang() + 1);
      obj.put("type", player.playerTeamType);
      return obj.toJSONString();
   }

   public static String parseDropFlagCommand(FlagServer flag) {
      JSONObject obj = new JSONObject();
      obj.put("x", flag.position.x);
      obj.put("y", flag.position.y);
      obj.put("z", flag.position.z);
      obj.put("flagTeam", flag.flagTeamType);
      return obj.toJSONString();
   }

   public static String parseCTFModelData(BattlefieldModel model) {
      JSONObject obj = new JSONObject();
      CTFModel ctfModel = model.ctfModel;
      JSONObject basePosBlue = new JSONObject();
      basePosBlue.put("x", model.battleInfo.map.flagBluePosition.x);
      basePosBlue.put("y", model.battleInfo.map.flagBluePosition.y);
      basePosBlue.put("z", model.battleInfo.map.flagBluePosition.z);
      JSONObject basePosRed = new JSONObject();
      basePosRed.put("x", model.battleInfo.map.flagRedPosition.x);
      basePosRed.put("y", model.battleInfo.map.flagRedPosition.y);
      basePosRed.put("z", model.battleInfo.map.flagRedPosition.z);
      JSONObject posBlue = new JSONObject();
      posBlue.put("x", ctfModel.getBlueFlag().position.x);
      posBlue.put("y", ctfModel.getBlueFlag().position.y);
      posBlue.put("z", ctfModel.getBlueFlag().position.z);
      JSONObject posRed = new JSONObject();
      posRed.put("x", ctfModel.getRedFlag().position.x);
      posRed.put("y", ctfModel.getRedFlag().position.y);
      posRed.put("z", ctfModel.getRedFlag().position.z);
      obj.put("basePosBlueFlag", basePosBlue);
      obj.put("basePosRedFlag", basePosRed);
      obj.put("posBlueFlag", posBlue);
      obj.put("posRedFlag", posRed);
      obj.put("blueFlagCarrierId", ctfModel.getBlueFlag().owner == null ? null : ctfModel.getBlueFlag().owner.tank.id);
      obj.put("redFlagCarrierId", ctfModel.getRedFlag().owner == null ? null : ctfModel.getRedFlag().owner.tank.id);
      return obj.toJSONString();
   }

   public static String parseUpdateCoundPeoplesCommand(BattleInfo battle) {
      JSONObject obj = new JSONObject();
      obj.put("battleId", battle.battleId);
      obj.put("redPeople", battle.redPeople);
      obj.put("bluePeople", battle.bluePeople);
      return obj.toJSONString();
   }

   public static String parseFishishBattle(FastHashMap<String, BattlefieldPlayerController> players, int timeToRestart) {
      JSONObject obj = new JSONObject();
      JSONArray users = new JSONArray();
      obj.put("time_to_restart", timeToRestart);
      if (players == null) {
         return obj.toString();
      } else {
         Iterator var5 = players.values().iterator();

         while(var5.hasNext()) {
            BattlefieldPlayerController bpc = (BattlefieldPlayerController)var5.next();
            JSONObject stat = new JSONObject();
            stat.put("kills", bpc.statistic.getKills());
            stat.put("deaths", bpc.statistic.getDeaths());
            stat.put("id", bpc.getUser().getNickname());
            stat.put("rank", bpc.getUser().getRang() + 1);
            stat.put("prize", bpc.statistic.getPrize());
            stat.put("team_type", bpc.playerTeamType);
            stat.put("score", bpc.statistic.getScore());
            users.add(stat);
         }

         obj.put("users", users);
         return obj.toString();
      }
   }

   public static String parsePlayerStatistic(BattlefieldPlayerController player) {
      JSONObject obj = new JSONObject();
      obj.put("kills", player.statistic.getKills());
      obj.put("deaths", player.statistic.getDeaths());
      obj.put("id", player.getUser().getNickname());
      obj.put("rank", player.getUser().getRang() + 1);
      obj.put("team_type", player.playerTeamType);
      obj.put("score", player.statistic.getScore());
      return obj.toString();
   }

   public static String parseSpawnCommand(BattlefieldPlayerController bpc, Vector3 pos) {
      JSONObject obj = new JSONObject();
      if (bpc != null && bpc.tank != null) {
         obj.put("tank_id", bpc.tank.id);
         obj.put("health", bpc.tank.health);
         obj.put("speed", bpc.tank.speed);
         obj.put("turn_speed", bpc.tank.turnSpeed);
         obj.put("turret_rotation_speed", bpc.tank.turretRotationSpeed);
         obj.put("incration_id", bpc.battle.incration);
         obj.put("team_type", bpc.playerTeamType);
         obj.put("x", pos.x);
         obj.put("y", pos.y);
         obj.put("z", pos.z);
         obj.put("rot", pos.rot);
         return obj.toString();
      } else {
         return null;
      }
   }

   public static String parseBattleData(BattlefieldModel model) {
      JSONObject obj = new JSONObject();
      JSONArray users = new JSONArray();
      obj.put("name", model.battleInfo.name);
      obj.put("fund", model.tanksKillModel.getBattleFund());
      obj.put("scoreLimit", model.battleInfo.battleType.equals("CTF") ? model.battleInfo.numFlags : model.battleInfo.numKills);
      obj.put("timeLimit", model.battleInfo.time);
      obj.put("currTime", model.getTimeLeft());
      obj.put("score_red", model.battleInfo.scoreRed);
      obj.put("score_blue", model.battleInfo.scoreBlue);
      obj.put("team", model.battleInfo.team);
      Iterator var4 = model.players.values().iterator();

      while(var4.hasNext()) {
         BattlefieldPlayerController bpc = (BattlefieldPlayerController)var4.next();
         JSONObject usr = new JSONObject();
         usr.put("nickname", bpc.parentLobby.getLocalUser().getNickname());
         usr.put("rank", bpc.parentLobby.getLocalUser().getRang() + 1);
         usr.put("teamType", bpc.playerTeamType);
         users.add(usr);
      }

      obj.put("users", users);
      return obj.toJSONString();
   }

   public static String parseUserToJSON(User user) {
      JSONObject obj = new JSONObject();
      obj.put("name", user.getNickname());
      obj.put("crystall", user.getCrystall());
      obj.put("email", user.getEmail());
      obj.put("tester", user.getType() != TypeUser.DEFAULT);
      obj.put("next_score", user.getNextScore());
      obj.put("place", user.getPlace());
      obj.put("rang", user.getRang() + 1);
      obj.put("rating", user.getRating());
      obj.put("score", user.getScore());
      obj.put("have_double_crystalls", true);
      return obj.toJSONString();
   }

   public static JSONObject parseUserToJSONObject(User user) {
      JSONObject obj = new JSONObject();
      obj.put("name", user.getNickname());
      obj.put("crystall", user.getCrystall());
      obj.put("email", user.getEmail());
      obj.put("tester", user.getType() != TypeUser.DEFAULT);
      obj.put("next_score", user.getNextScore());
      obj.put("place", user.getPlace());
      obj.put("rang", user.getRang() + 1);
      obj.put("rating", user.getRating());
      obj.put("score", user.getScore());
      return obj;
   }

   public static String parseHallOfFame(HallOfFame top) {
      JSONObject obj = new JSONObject();
      JSONArray array = new JSONArray();
      Iterator var4 = top.getData().iterator();

      while(var4.hasNext()) {
         User user = (User)var4.next();
         array.add(parseUserToJSONObject(user));
      }

      obj.put("users_data", array);
      return obj.toJSONString();
   }

   public static String parseChatLobbyMessage(ChatMessage msg) {
      JSONObject obj = new JSONObject();
      obj.put("name", msg.user.getNickname());
      obj.put("rang", msg.user.getRang() + 1);
      obj.put("message", msg.message);
      obj.put("addressed", msg.addressed);
      obj.put("nameTo", msg.userTo == null ? "NULL" : msg.userTo.getNickname());
      obj.put("rangTo", msg.userTo == null ? 0 : msg.userTo.getRang() + 1);
      obj.put("system", msg.system);
      obj.put("yellow", msg.yellowMessage);
      obj.put("chatPermissionsTo", msg.chatPerm);
      obj.put("chatPermissions", msg.chatPerm);
      obj.put("premiumTo", msg.hasPremium);
      obj.put("premium", msg.hasPremium);
      return obj.toJSONString();
   }

   public static String parseChatLobbyNewsObject(User user) {
      JSONObject obj = new JSONObject();
      obj.put("date", "16.01.2024");
      obj.put("text", "лето на дворе.");
      obj.put("user_name", user.getNickname());
      obj.put("id", 1);
      obj.put("icon_id", "");

      JSONObject obj2 = new JSONObject();
      obj2.put("date", "15.01.2024");
      obj2.put("text", "АОАОАОАОАОАОА");
      obj2.put("user_name", user.getNickname());
      obj2.put("id", 2);
      obj2.put("icon_id", "");

      JSONArray news = new JSONArray();
      news.add(obj);
      news.add(obj2);

      return news.toJSONString();
   }

   public static JSONObject parseChatLobbyMessageObject(ChatMessage msg) {
      JSONObject obj = new JSONObject();
      obj.put("name", msg.user == null ? "" : msg.user.getNickname());
      obj.put("rang", msg.user == null ? 0 : msg.user.getRang() + 1);
      obj.put("message", msg.message);
      obj.put("addressed", msg.addressed);
      obj.put("nameTo", msg.userTo == null ? "" : msg.userTo.getNickname());
      obj.put("rangTo", msg.userTo == null ? 0 : msg.userTo.getRang() + 1);
      obj.put("system", msg.system);
      obj.put("yellow", msg.yellowMessage);
      obj.put("chatPermissionsTo", msg.chatPerm);
      obj.put("chatPermissions", msg.chatPerm);
      obj.put("premiumTo", msg.hasPremium);
      obj.put("premium", msg.hasPremium);
      return obj;
   }

   public static String parseChatLobbyMessages(Collection<ChatMessage> messages) {
      JSONObject obj = new JSONObject();
      JSONArray array = new JSONArray();
      Iterator var4 = messages.iterator();

      while(var4.hasNext()) {
         ChatMessage msg = (ChatMessage)var4.next();
         array.add(parseChatLobbyMessageObject(msg));
      }

      obj.put("messages", array);
      return obj.toJSONString();
   }

   public static String parseGarageUser(User user) {
      try {
         Garage garage = user.getGarage();
         JSONObject obj = new JSONObject();
         JSONArray array = new JSONArray();
         Iterator var5 = garage.items.iterator();

         while(var5.hasNext()) {
            Item item = (Item)var5.next();
            JSONObject i = new JSONObject();
            JSONArray properts = new JSONArray();
            JSONArray modification = new JSONArray();
            i.put("id", item.id);
            i.put("name", item.name.localizatedString(user.getLocalization()));
            i.put("description", item.description.localizatedString(user.getLocalization()));
            i.put("isInventory", boolToString(item.isInventory));
            i.put("index", item.index);
            int value = Integer.parseInt(item.itemType.toString());
            i.put("type", value);
            i.put("modificationID", item.modificationIndex);
            i.put("next_price", item.nextPrice);
            i.put("next_rank", item.nextRankId);
            i.put("price", item.price);
            i.put("rank", item.rankId);
            i.put("count", item.count);
            int var11;
            int var12;
            if (item.propetys != null) {
               PropertyItem[] var13;
               var12 = (var13 = item.propetys).length;

               for(var11 = 0; var11 < var12; ++var11) {
                  PropertyItem prop = var13[var11];
                  if (prop != null && prop.property != null) {
                     properts.add(parseProperty(prop));
                  }
               }
            }

            if (item.modifications != null) {
               ModificationInfo[] var22;
               var12 = (var22 = item.modifications).length;

               for(var11 = 0; var11 < var12; ++var11) {
                  ModificationInfo mod = var22[var11];
                  JSONObject m = new JSONObject();
                  JSONArray prop = new JSONArray();
                  m.put("previewId", mod.previewId);
                  m.put("price", mod.price);
                  m.put("rank", mod.rank);
                  if (mod.propertys != null) {
                     PropertyItem[] var19;
                     int var18 = (var19 = mod.propertys).length;

                     for(int var17 = 0; var17 < var18; ++var17) {
                        PropertyItem a = var19[var17];
                        if (a != null && a.property != null) {
                           prop.add(parseProperty(a));
                        }
                     }
                  }

                  m.put("properts", prop);
                  modification.add(m);
               }
            }

            i.put("properts", properts);
            i.put("modification", modification);
            array.add(i);
         }

         obj.put("items", array);
         return obj.toString();
      } catch (Exception var20) {
         var20.printStackTrace();
         return null;
      }
   }

   public static String parseEffectsUser(User user) {
      try {
         Garage garage = user.getGarage();
         JSONObject obj = new JSONObject();
         JSONArray array = new JSONArray();
         Iterator var5 = garage.items.iterator();

         while(var5.hasNext()) {
            Item item = (Item)var5.next();
            JSONObject i = new JSONObject();
            if (item.itemType == ItemType.PLUGIN) {
               i.put("id", item.id + "_m0");
               i.put("time", garage.formatAsExponential(item.time));
               array.add(i);
            }
         }

         obj.put("effects", array);
         System.out.println(obj.toString());
         return obj.toString();
      } catch (Exception var20) {
         var20.printStackTrace();
         return null;
      }
   }

   public static String parseMarketItems(User user) {
      Garage garage = user.getGarage();
      JSONObject json = new JSONObject();
      JSONArray jarray = new JSONArray();

      for(Iterator var5 = GarageItemsLoader.items.values().iterator(); var5.hasNext(); json.put("items", jarray)) {
         Item item = (Item)var5.next();
         if (!garage.containsItem(item.id) && !item.specialItem) {
            JSONObject i = new JSONObject();
            JSONArray properts = new JSONArray();
            JSONArray modification = new JSONArray();
            i.put("id", item.id);
            i.put("name", item.name.localizatedString(user.getLocalization()));
            i.put("description", item.description.localizatedString(user.getLocalization()));
            i.put("isInventory", item.isInventory);
            i.put("discount", item.discount);
            i.put("index", item.index);
            int value = Integer.parseInt(item.itemType.toString());
            i.put("type", value);
            i.put("modificationID", 0);
            i.put("next_price", item.nextPrice);
            i.put("next_rank", item.nextRankId);
            i.put("price", item.price);
            i.put("rank", item.rankId);
            int var11;
            int var12;
            if (item.propetys != null) {
               PropertyItem[] var13;
               var12 = (var13 = item.propetys).length;

               for(var11 = 0; var11 < var12; ++var11) {
                  PropertyItem prop = var13[var11];
                  properts.add(parseProperty(prop));
               }
            }

            if (item.modifications != null) {
               ModificationInfo[] var21;
               var12 = (var21 = item.modifications).length;

               for(var11 = 0; var11 < var12; ++var11) {
                  ModificationInfo mod = var21[var11];
                  JSONObject m = new JSONObject();
                  JSONArray prop = new JSONArray();
                  m.put("previewId", mod.previewId);
                  m.put("price", mod.price);
                  m.put("rank", mod.rank);
                  if (mod.propertys != null) {
                     PropertyItem[] var19;
                     int var18 = (var19 = mod.propertys).length;

                     for(int var17 = 0; var17 < var18; ++var17) {
                        PropertyItem a = var19[var17];
                        prop.add(parseProperty(a));
                     }
                  }

                  m.put("properts", prop);
                  modification.add(m);
               }
            }

            i.put("properts", properts);
            i.put("modification", modification);
            jarray.add(i);
         }
      }

      return json.toString();
   }

   public static String parseItemInfo(Item item) {
      JSONObject obj = new JSONObject();
      obj.put("itemId", item.id);
      obj.put("count", item.count);
      return obj.toJSONString();
   }

   private static JSONObject parseProperty(PropertyItem item) {
      JSONObject h = new JSONObject();
      h.put("property", item.property.toString());
      h.put("value", item.value);
      return h;
   }

   public static String parseBattleMapList() {
      JSONObject json = new JSONObject();
      JSONArray jarray = new JSONArray();
      JSONArray jbattles = new JSONArray();
      Iterator var4 = MapsLoader.maps.values().iterator();

      while(var4.hasNext()) {
         Map map = (Map)var4.next();
         JSONObject jmap = new JSONObject();
         jmap.put("id", map.id.replace(".xml", ""));
         jmap.put("name", map.name);
         jmap.put("gameName", "тип gameName");
         jmap.put("maxPeople", map.maxPlayers);
         jmap.put("maxRank", map.maxRank);
         jmap.put("minRank", map.minRank);
         jmap.put("themeName", map.themeId);
         jmap.put("skyboxId", map.skyboxId);
         jmap.put("ctf", map.ctf);
         jmap.put("tdm", map.tdm);
         jmap.put("dom", false);
         jmap.put("hr", false);
         jarray.add(jmap);
      }

      json.put("items", jarray);
      var4 = BattlesList.getList().iterator();

      while(var4.hasNext()) {
         BattleInfo battle = (BattleInfo)var4.next();
         jbattles.add(parseBattleInfo(battle, 1));
      }

      json.put("battles", jbattles);
      return json.toString();
   }

   public static String parseBattleInfo(BattleInfo battle) {
      JSONObject json = new JSONObject();
      json.put("battleId", battle.battleId);
      json.put("mapId", battle.map.id);
      json.put("name", battle.name);
      json.put("previewId", battle.map.id + "_preview");
      json.put("team", battle.team);
      json.put("redPeople", battle.redPeople);
      json.put("bluePeople", battle.bluePeople);
      json.put("countPeople", battle.countPeople);
      json.put("maxPeople", battle.maxPeople);
      json.put("minRank", battle.minRank);
      json.put("maxRank", battle.maxRank);
      json.put("isPaid", battle.isPaid);
      json.put("friendsDm", 0);
      json.put("friendsRed", 0);
      json.put("friendsBlue", 0);
      return json.toJSONString();
   }

   public static JSONObject parseBattleInfo(BattleInfo battle, int i) {
      JSONObject json = new JSONObject();
      json.put("battleId", battle.battleId);
      json.put("mapId", battle.map.id);
      json.put("name", battle.name);
      json.put("previewId", battle.map.id + "_preview");
      json.put("team", battle.team);
      json.put("redPeople", battle.redPeople);
      json.put("bluePeople", battle.bluePeople);
      json.put("countPeople", battle.countPeople);
      json.put("maxPeople", battle.maxPeople);
      json.put("minRank", battle.minRank);
      json.put("maxRank", battle.maxRank);
      json.put("isPaid", battle.isPaid);
      return json;
   }

   public static String parseBattleInfoShow(BattleInfo battle, boolean spectator) {
      JSONObject json = new JSONObject();
      if (battle == null) {
         json.put("null_battle", true);
         return json.toJSONString();
      } else {
         try {
            JSONArray users = new JSONArray();
            if (battle != null && battle.model != null && battle.model.players != null) {
               Iterator var5 = battle.model.players.values().iterator();

               JSONObject obj_user;
               while(var5.hasNext()) {
                  BattlefieldPlayerController player = (BattlefieldPlayerController)var5.next();
                  obj_user = new JSONObject();
                  obj_user.put("nickname", player.parentLobby.getLocalUser().getNickname());
                  obj_user.put("rank", player.parentLobby.getLocalUser().getRang() + 1);
                  obj_user.put("kills", player.statistic.getKills());
                  obj_user.put("team_type", player.playerTeamType);
                  users.add(obj_user);
               }

               var5 = autoEntryServices.getPlayersByBattle(battle.model).iterator();

               while(var5.hasNext()) {
                  AutoEntryServices.Data player = (AutoEntryServices.Data)var5.next();
                  obj_user = new JSONObject();
                  User user = databaseManager.getUserById(player.userId);
                  obj_user.put("nickname", user.getNickname());
                  obj_user.put("rank", user.getRang() + 1);
                  obj_user.put("kills", player.statistic.getKills());
                  obj_user.put("team_type", player.teamType);
                  users.add(obj_user);
               }
            }

            json.put("users_in_battle", users);
            json.put("name", battle.name);
            json.put("maxPeople", battle.maxPeople);
            json.put("type", battle.battleType);
            json.put("battleId", battle.battleId);
            json.put("minRank", battle.minRank);
            json.put("maxRank", battle.maxRank);
            json.put("timeLimit", battle.time);
            json.put("timeCurrent", battle.model.getTimeLeft());
            json.put("killsLimt", battle.numKills);
            json.put("scoreRed", battle.scoreRed);
            json.put("scoreBlue", battle.scoreBlue);
            json.put("autobalance", battle.autobalance);
            json.put("friendlyFire", battle.friendlyFire);
            json.put("paidBattle", battle.isPaid);
            json.put("withoutBonuses", true);
            json.put("userAlreadyPaid", true);
            json.put("fullCash", true);
            json.put("spectator", spectator);
            json.put("previewId", battle.map.id + "_preview");
         } catch (Exception var8) {
            var8.printStackTrace();
            return json.toString();
         }

         return json.toJSONString();
      }
   }

   public static String parseBattleModelInfo(BattleInfo battle, boolean spectatorMode, Garage garageUser) {
      JSONObject json = new JSONObject();
      String[] garageResources = new String[]{garageUser.mountTurret.id + "_m" + garageUser.mountTurret.modificationIndex, garageUser.mountHull.id + "_m" + garageUser.mountHull.modificationIndex, garageUser.mountColormap.id + "_m0"};
      json.put("kick_period_ms", 125000);
      json.put("map_id", battle.map.id.replace(".xml", ""));
      json.put("invisible_time", 3500);
      json.put("skybox_id", battle.map.skyboxId);
      json.put("spectator", spectatorMode);
      json.put("sound_id", battle.map.mapTheme.getAmbientSoundId());
      json.put("game_mode", battle.map.mapTheme.getGameModeId());
      JSONObject lightParams = new JSONObject();
      lightParams.put("angleX", battle.map.angleX);
      lightParams.put("angleZ", battle.map.angleZ);
      lightParams.put("lightColor", battle.map.lightColor);
      lightParams.put("shadowColor", battle.map.shadowColor);
      lightParams.put("fogAlpha",  battle.map.fogAlpha);
      lightParams.put("fogColor", battle.map.fogColor);
      lightParams.put("ssaoColor",  battle.map.ssaoColor);
      json.put("light_params", lightParams.toJSONString());
      System.out.println(lightParams.toJSONString());
      json.put("resources", garageResources);
      return json.toJSONString();
   }

   public static String parseTankData(BattlefieldModel player, BattlefieldPlayerController controller, Garage garageUser, Vector3 pos, boolean stateNull, int icration, String idTank, String nickname, int rank) {
      JSONObject json = new JSONObject();
      json.put("battleId", player.battleInfo.battleId);
      json.put("colormap_id", garageUser.mountColormap.id + "_m0");
      json.put("hull_id", garageUser.mountHull.id + "_m" + garageUser.mountHull.modificationIndex);
      json.put("turret_id", garageUser.mountTurret.id + "_m" + garageUser.mountTurret.modificationIndex);
      json.put("team_type", controller.playerTeamType);
      if (pos == null) {
         pos = new Vector3(0.0F, 0.0F, 0.0F);
      }

      json.put("position", pos.x + "@" + pos.y + "@" + pos.z + "@" + pos.rot);
      json.put("incration", icration);
      json.put("tank_id", idTank);
      json.put("nickname", nickname);
      json.put("state", controller.tank.state);
      json.put("turn_speed", controller.tank.getHull().turnSpeed);
      json.put("speed", controller.tank.getHull().speed);
      json.put("maxSpeed", controller.tank.getHull().maxSpeed);
      json.put("acceleration", controller.tank.getHull().acceleration);
      json.put("reverseAcceleration", controller.tank.getHull().reverseAcceleration);
      json.put("deceleration", controller.tank.getHull().deceleration);
      json.put("turnMaxSpeed", controller.tank.getHull().turnMaxSpeed);
      json.put("turnAcceleration", controller.tank.getHull().turnAcceleration);
      json.put("turnReverseAcceleration", controller.tank.getHull().turnReverseAcceleration);
      json.put("turnDeceleration", controller.tank.getHull().turnDeceleration);
      json.put("sideAcceleration", controller.tank.getHull().sideAcceleration);
      json.put("damping", controller.tank.getHull().damping);
      json.put("turret_turn_speed", controller.tank.turretRotationSpeed);
      json.put("health", controller.tank.health);
      json.put("rank", rank + 1);
      json.put("mass", controller.tank.getHull().mass);
      json.put("power", controller.tank.getHull().power);
      json.put("kickback", controller.tank.getWeapon().getEntity().getShotData().kickback);
      json.put("turret_rotation_accel", controller.tank.getWeapon().getEntity().getShotData().turretRotationAccel);
      json.put("impact_force", controller.tank.getWeapon().getEntity().getShotData().impactCoeff);
      json.put("state_null", stateNull);
      return json.toJSONString();
   }

   public static String parseMoveCommand(BattlefieldPlayerController player) {
      Tank tank = player.tank;
      JSONObject json = new JSONObject();
      JSONObject pos = new JSONObject();
      JSONObject orient = new JSONObject();
      JSONObject line = new JSONObject();
      JSONObject angle = new JSONObject();
      pos.put("x", tank.position.x);
      pos.put("y", tank.position.y);
      pos.put("z", tank.position.z);
      orient.put("x", tank.orientation.x);
      orient.put("y", tank.orientation.y);
      orient.put("z", tank.orientation.z);
      line.put("x", tank.linVel.x);
      line.put("y", tank.linVel.y);
      line.put("z", tank.linVel.z);
      angle.put("x", tank.angVel.x);
      angle.put("y", tank.angVel.y);
      angle.put("z", tank.angVel.z);
      json.put("position", pos);
      json.put("orient", orient);
      json.put("line", line);
      json.put("angle", angle);
      json.put("turretDir", tank.turretDir);
      json.put("ctrlBits", tank.controllBits);
      json.put("tank_id", tank.id);
      return json.toJSONString();
   }

   public static String parseBattleChatMessage(BattleChatMessage msg) {
      JSONObject jobj = new JSONObject();
      jobj.put("nickname", msg.nickname);
      jobj.put("rank", msg.rank + 1);
      jobj.put("message", msg.message);
      jobj.put("team_type", msg.teamType);
      jobj.put("system", msg.system);
      jobj.put("team", msg.team);
      return jobj.toJSONString();
   }

   public static String parseBonusInfo(Bonus bonus, int inc, int disappearingTime) {
      JSONObject jobj = new JSONObject();
      jobj.put("id", bonus.type.toString() + "_" + inc);
      jobj.put("x", bonus.position.x);
      jobj.put("y", bonus.position.y);
      jobj.put("z", bonus.position.z);
      jobj.put("disappearing_time", disappearingTime);
      return jobj.toJSONString();
   }

   public static JSONObject parseSpecialEntity(IEntity entity) {
      JSONObject j = new JSONObject();
      switch($SWITCH_TABLE$gtanks$battles$tanks$weapons$EntityType()[entity.getType().ordinal()]) {
      case 2:
         FlamethrowerEntity fm = (FlamethrowerEntity)entity;
         j.put("cooling_speed", fm.coolingSpeed);
         j.put("cone_angle", fm.coneAngle);
         j.put("heating_speed", fm.heatingSpeed);
         j.put("heat_limit", fm.heatLimit);
         j.put("range", fm.range);
         j.put("target_detection_interval", fm.targetDetectionInterval);
         break;
      case 3:
         TwinsEntity te = (TwinsEntity)entity;
         j.put("shot_radius", te.shotRadius);
         j.put("shot_range", te.shotRange);
         j.put("shot_speed", te.shotSpeed);
      case 4:
         default:
            break;
      case 9:
         ShaftEntity sf = (ShaftEntity)entity;
         j.put("max_energy", sf.maxEnergy);
         j.put("charge_rate", sf.chargeRate);
         j.put("discharge_rate", sf.dischargeRate);
         j.put("elevation_angle_up", sf.elevationAngleUp);
         j.put("elevation_angle_down", sf.elevationAngleDown);
         j.put("vertical_targeting_speed", sf.verticalTargetingSpeed);
         j.put("horizontal_targeting_speed", sf.horizontalTargetingSpeed);
         j.put("inital_fov", sf.initialFOV);
         j.put("minimum_fov", sf.minimumFOV);
         j.put("shrubs_hiding_radius_min", sf.shrubsHidingRadiusMin);
         j.put("shrubs_hiding_radius_max", sf.shrubsHidingRadiusMax);
         j.put("impact_quick_shot", sf.impactQuickShot);
         break;
      case 5:
         IsidaEntity ie = (IsidaEntity)entity;
         j.put("angle", ie.maxAngle);
         j.put("capacity", ie.capacity);
         j.put("chargeRate", ie.chargeRate);
         j.put("tickPeriod", ie.tickPeriod);
         j.put("coneAngle", ie.lockAngle);
         j.put("dischargeRate", ie.dischargeRate);
         j.put("radius", ie.maxRadius);
         break;
      case 6:
         ThunderEntity the = (ThunderEntity)entity;
         j.put("impactForce", the.impactForce);
         j.put("maxSplashDamageRadius", the.maxSplashDamageRadius);
         j.put("minSplashDamagePercent", the.minSplashDamagePercent);
         j.put("minSplashDamageRadius", the.minSplashDamageRadius);
         break;
      case 7:
         FrezeeEntity frezeeEntity = (FrezeeEntity)entity;
         j.put("damageAreaConeAngle", frezeeEntity.damageAreaConeAngle);
         j.put("damageAreaRange", frezeeEntity.damageAreaRange);
         j.put("energyCapacity", frezeeEntity.energyCapacity);
         j.put("energyRechargeSpeed", frezeeEntity.energyRechargeSpeed);
         j.put("energyDischargeSpeed", frezeeEntity.energyDischargeSpeed);
         j.put("weaponTickMsec", frezeeEntity.weaponTickMsec);
         break;
      case 8:
         RicochetEntity ricochetEntity = (RicochetEntity)entity;
         j.put("energyCapacity", ricochetEntity.energyCapacity);
         j.put("energyPerShot", ricochetEntity.energyPerShot);
         j.put("energyRechargeSpeed", ricochetEntity.energyRechargeSpeed);
         j.put("shotDistance", ricochetEntity.shotDistance);
         j.put("shotRadius", ricochetEntity.shotRadius);
         j.put("shotSpeed", ricochetEntity.shotSpeed);
         break;
         case 12:
            VulcanEntity vu = (VulcanEntity)entity;
            j.put("energyCapacity", vu.energyCapacity);
            j.put("energyDischargeSpeed", vu.energyDischargeSpeed);
            j.put("energyRechargeSpeed", vu.energyRechargeSpeed);
            j.put("spinUpTime", vu.spinUpTime);
            j.put("weaponTickMsec", vu.weaponTickMsec);
            j.put("damageTickMsec", vu.damageTickMsec);
            j.put("spinDownTime", vu.spinDownTime);
            j.put("weaponTurnDecelerationCoeff", vu.weaponTurnDecelerationCoeff);
            j.put("recoilForce", vu.recoilForce);
            j.put("impactForce", vu.impactForce);
            break;
      case 10:
         SnowmanEntity se = (SnowmanEntity)entity;
         j.put("shot_radius", se.shotRadius);
         j.put("shot_range", se.shotRange);
         j.put("shot_speed", se.shotSpeed);
      }

      return j;
   }

   public static String parseWeapons(Collection<IEntity> weapons, HashMap<String, WeaponWeakeningData> wwds) {
      JSONObject obj = new JSONObject();
      JSONArray array = new JSONArray();
      Iterator var5 = weapons.iterator();

      while(var5.hasNext()) {
         IEntity entity = (IEntity)var5.next();
         JSONObject weapon = new JSONObject();
         WeaponWeakeningData wwd = (WeaponWeakeningData)wwds.get(entity.getShotData().id);
         weapon.put("auto_aiming_down", entity.getShotData().autoAimingAngleDown);
         weapon.put("auto_aiming_up", entity.getShotData().autoAimingAngleUp);
         weapon.put("num_rays_down", entity.getShotData().numRaysDown);
         weapon.put("num_rays_up", entity.getShotData().numRaysUp);
         weapon.put("reload", entity.getShotData().reloadMsec);
         weapon.put("id", entity.getShotData().id);
         if (wwd != null) {
            weapon.put("max_damage_radius", wwd.maximumDamageRadius);
            weapon.put("min_damage_radius", wwd.minimumDamageRadius);
            weapon.put("min_damage_percent", wwd.minimumDamagePercent);
            weapon.put("has_wwd", true);
         } else {
            weapon.put("has_wwd", false);
         }

         weapon.put("special_entity", parseSpecialEntity(entity));
         array.add(weapon);
      }

      obj.put("weapons", array);
      return obj.toJSONString();
   }

   public static String parseTankSpec(Tank tank, boolean notSmooth) {
      JSONObject obj = new JSONObject();
      obj.put("speed", tank.speed);
      obj.put("turnSpeed", tank.turnSpeed);
      obj.put("turretRotationSpeed", tank.turretRotationSpeed);
      obj.put("immediate", notSmooth);
      return obj.toString();
   }

   public static String boolToString(boolean src) {
      return src ? "true" : "false";
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$gtanks$battles$tanks$weapons$EntityType() {
      int[] var10000 = $SWITCH_TABLE$gtanks$battles$tanks$weapons$EntityType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[EntityType.values().length];

         try {
            var0[EntityType.FLAMETHROWER.ordinal()] = 2;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[EntityType.FREZZE.ordinal()] = 7;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[EntityType.ISIDA.ordinal()] = 5;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[EntityType.RAILGUN.ordinal()] = 4;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[EntityType.RICOCHET.ordinal()] = 8;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[EntityType.SHAFT.ordinal()] = 9;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[EntityType.SMOKY.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[EntityType.SNOWMAN.ordinal()] = 10;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[EntityType.THUNDER.ordinal()] = 6;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[EntityType.TWINS.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }

         try {
            var0[EntityType.VULCAN.ordinal()] = 12;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$gtanks$battles$tanks$weapons$EntityType = var0;
         return var0;
      }
   }
}
