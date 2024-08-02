package gtanks.lobby;

import gtanks.StringUtils;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.maps.Map;
import gtanks.battles.maps.MapsLoader;
import gtanks.battles.spectator.SpectatorController;
import gtanks.commands.Command;
import gtanks.commands.Type;
import gtanks.json.JSONUtils;
import gtanks.lobby.battles.BattleInfo;
import gtanks.lobby.battles.BattlesList;
import gtanks.lobby.chat.ChatLobby;
import gtanks.lobby.chat.ChatMessage;
import gtanks.lobby.chat.flood.FloodController;
import gtanks.lobby.chat.flood.LobbyFloodController;
import gtanks.lobby.top.HallOfFame;
import gtanks.logger.Logger;
import gtanks.main.database.DatabaseManager;
import gtanks.main.database.impl.DatabaseManagerImpl;
import gtanks.main.netty.ProtocolTransfer;
import gtanks.main.params.OnlineStats;
import gtanks.network.listeners.DisconnectListener;
import gtanks.rmi.payments.shop.SHOP;
import gtanks.services.AutoEntryServices;
import gtanks.services.FriendsServices;
import gtanks.services.LobbysServices;
import gtanks.services.TanksServices;
import gtanks.services.annotations.ServicesInject;
import gtanks.system.dailybonus.DailyBonusService;
import gtanks.users.TypeUser;
import gtanks.users.User;
import gtanks.users.garage.Garage;
import gtanks.users.garage.GarageItemsLoader;
import gtanks.users.garage.enums.ItemType;
import gtanks.users.garage.items.Item;
import gtanks.users.locations.UserLocation;
import java.io.IOException;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LobbyManager extends LobbyComandsConst {
   private User localUser;
   public ProtocolTransfer networker;
   public BattlefieldPlayerController battle;
   public SpectatorController spectatorController;
   @ServicesInject(
      target = DatabaseManagerImpl.class
   )
   private static DatabaseManager database = DatabaseManagerImpl.instance();
   @ServicesInject(
      target = HallOfFame.class
   )
   private static HallOfFame top = HallOfFame.getInstance();
   @ServicesInject(
      target = LobbysServices.class
   )
   private static LobbysServices lobbysServices = LobbysServices.getInstance();
   @ServicesInject(
           target = TanksServices.class)
   private static TanksServices tanksServices = TanksServices.getInstance();
   @ServicesInject(
      target = ChatLobby.class
   )
   private static ChatLobby chatLobby = ChatLobby.getInstance();
   @ServicesInject(
      target = DailyBonusService.class
   )
   private static DailyBonusService dailyBonusService = DailyBonusService.instance();
   @ServicesInject(
      target = AutoEntryServices.class
   )
   private AutoEntryServices autoEntryServices = AutoEntryServices.instance();
   private FloodController chatFloodController;
   public DisconnectListener disconnectListener;
   public long timer;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$gtanks$commands$Type;

   public LobbyManager(ProtocolTransfer networker, User localUser) {
      this.networker = networker;
      this.localUser = localUser;
      this.disconnectListener = new DisconnectListener();
      this.setChatFloodController(new LobbyFloodController());
      this.timer = System.currentTimeMillis();
      this.localUser.setUserLocation(UserLocation.GARAGE);
      lobbysServices.addLobby(this);
      OnlineStats.addOnline();
      dailyBonusService.userInited(this);
   }

   public void send(Type type, String... args) {
      try {
         this.networker.send(type, args);
      } catch (IOException var4) {
      }

   }

   public void executeCommand(Command cmd) {
      try {
         String _name;
         switch($SWITCH_TABLE$gtanks$commands$Type()[cmd.type.ordinal()]) {
         case 3:
            if (cmd.args[0].equals("try_mount_item")) {
               if (this.localUser.getGarage().mountItem(cmd.args[1])) {
                  this.send(Type.GARAGE, "mount_item", cmd.args[1]);
                  this.localUser.getGarage().parseJSONData();
                  database.update(this.localUser.getGarage());
               } else {
                  this.send(Type.GARAGE, "try_mount_item_NO");
               }
            }

            if (cmd.args[0].equals("try_update_item")) {
               this.onTryUpdateItem(cmd.args[1]);
            }

            if (cmd.args[0].equals("get_garage_data") && this.localUser.getGarage().mountHull != null && this.localUser.getGarage().mountTurret != null && this.localUser.getGarage().mountColormap != null) {
               this.send(Type.GARAGE, "init_mounted_item", StringUtils.concatStrings(this.localUser.getGarage().mountHull.id, "_m", String.valueOf(this.localUser.getGarage().mountHull.modificationIndex)));
               this.send(Type.GARAGE, "init_mounted_item", StringUtils.concatStrings(this.localUser.getGarage().mountTurret.id, "_m", String.valueOf(this.localUser.getGarage().mountTurret.modificationIndex)));
               this.send(Type.GARAGE, "init_mounted_item", StringUtils.concatStrings(this.localUser.getGarage().mountColormap.id, "_m", String.valueOf(this.localUser.getGarage().mountColormap.modificationIndex)));
            }

            if (cmd.args[0].equals("try_buy_item")) {
               this.onTryBuyItem(cmd.args[1], Integer.parseInt(cmd.args[2]));
            }
         case 4:
         case 8:
         case 9:
         case 10:
         default:
            break;
         case 5:
            if (cmd.args[0].equals("get_hall_of_fame_data")) {
               this.localUser.setUserLocation(UserLocation.HALL_OF_FAME);
               this.send(Type.LOBBY, "init_hall_of_fame", JSONUtils.parseHallOfFame(top));
            }

            if (cmd.args[0].equals("get_garage_data")) {
               this.sendGarage();
            }

            if (cmd.args[0].equals("get_data_init_battle_select")) {
               this.sendMapsInit();
            }

            if (cmd.args[0].equals("check_battleName_for_forbidden_words")) {
               _name = cmd.args.length > 0 ? cmd.args[1] : "";
               this.checkBattleName(_name);
            }

            if (cmd.args[0].equals("try_create_battle_dm")) {
               this.tryCreateBattleDM(cmd.args[1], cmd.args[2], Integer.parseInt(cmd.args[3]), Integer.parseInt(cmd.args[4]), Integer.parseInt(cmd.args[5]), Integer.parseInt(cmd.args[6]), Integer.parseInt(cmd.args[7]), this.stringToBoolean(cmd.args[8]), this.stringToBoolean(cmd.args[9]), this.stringToBoolean(cmd.args[10]));
            }

            if (cmd.args[0].equals("try_create_battle_tdm")) {
               this.tryCreateTDMBattle(cmd.args[1]);
            }

            if (cmd.args[0].equals("try_create_battle_ctf")) {
               this.tryCreateCTFBattle(cmd.args[1]);
            }

            if (cmd.args[0].equals("get_show_battle_info")) {
               this.sendBattleInfo(cmd.args[1]);
            }

            if (cmd.args[0].equals("enter_battle")) {
               this.onEnterInBattle(cmd.args[1]);
            }

            cmd.args[0].equals("bug_report");
            cmd.args[0].equals("screenshot");
            if (cmd.args[0].equals("enter_battle_team")) {
               this.onEnterInTeamBattle(cmd.args[1], Boolean.parseBoolean(cmd.args[2]));
            }

            if (cmd.args[0].equals("enter_battle_spectator")) {
               if (this.getLocalUser().getType() == TypeUser.DEFAULT) {
                  return;
               }

               this.enterInBattleBySpectator(cmd.args[1]);
            }

            if (cmd.args[0].equals("user_inited")) {
               dailyBonusService.userLoaded(this);
            }

            if (cmd.args[0].equals("get_friends")) {
               FriendsServices.getFriends(this, getLocalUser().getNickname());
            }

            if (cmd.args[0].equals("show_quests")) {
               int countQuest = 1;
               JSONObject data = new JSONObject();
               JSONArray challengesArray = new JSONArray();

               for (int i = 0; i < countQuest; i++) {
                  JSONObject challenge = new JSONObject();
                  challenge.put("description", "Ти крутий, це якщо що квест під id: " + i);
                  challenge.put("id", "win_cry");
                  challenge.put("target_progress", 100);
                  challenge.put("progress", 50);
                  challenge.put("completed", false);

                  JSONArray prizesArray = new JSONArray();
                  prizesArray.put("отримати пізд х1");
                  prizesArray.put("отримати пізд х20");
                  challenge.put("prizes", prizesArray);

                  challengesArray.put(challenge);
               }

               data.put("challenges", challengesArray);
               data.put("changeCost", 10);
               data.put("weeklyLevel", 2);
               data.put("weeklyProgress", 60);
               data.put("completeForToday", true);

               this.send(Type.LOBBY, "show_quests", data.toString());
            }

            if (cmd.args[0].equals("get_shop")) {
               this.send(Type.LOBBY, "open_shop", SHOP.getShop());
            }

            if (cmd.args[0].equals("shop_buy_item")) {

               JSONObject donate = new JSONObject();
               donate.put("count", 99999);
               donate.put("name", cmd.args[1]);

               this.send(Type.LOBBY, "donate_successfully", String.valueOf(donate));
            }

            if (cmd.args[0].equals("show_profile")) {
               JSONObject configuration = new JSONObject();
               configuration.put("emailNotice", false);
               configuration.put("isComfirmEmail", false);

               this.send(Type.LOBBY, "show_profile", String.valueOf(configuration));
            }


            if (cmd.args[0].equals("get_spins_url")) {
               this.send(Type.LOBBY, "navigate_url", "http://127.0.0.1/");
            }

            if (cmd.args[0].equals("get_profile")) {
               JSONObject itemsGift = new JSONObject();
               itemsGift.put("userid", localUser.getNickname());
               itemsGift.put("giftid", "0");
               itemsGift.put("image", "");
               itemsGift.put("name", localUser.getNickname());
               itemsGift.put("status", "online");
               itemsGift.put("message", "MyTanks");
               itemsGift.put("date", "01.01.2023");

               JSONObject parserInfo = new JSONObject();
               parserInfo.put("userId", localUser.getNickname());
               parserInfo.put("rank", localUser.getRang() + 1);
               parserInfo.put("spins", 0);
               parserInfo.put("emeralds", 0);
               parserInfo.put("incomingGifts", 0);
               parserInfo.put("outcomingGifts", 0);

               this.send(Type.LOBBY, "open_profile", String.valueOf(itemsGift), String.valueOf(parserInfo));
            }
            break;
         case 6:
            chatLobby.addMessage(new ChatMessage(this.localUser, cmd.args[0], this.stringToBoolean(cmd.args[1]), cmd.args[2].equals("NULL") ? null : database.getUserById(cmd.args[2]), this));
            break;
         case 7:
            if (this.battle != null) {
               this.battle.executeCommand(cmd);
            }

            if (this.spectatorController != null) {
               this.spectatorController.executeCommand(cmd);
            }
            break;
         case 11:
            _name = cmd.args[0];
            if (_name.equals("c01")) {
               this.kick();
            }
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   private void enterInBattleBySpectator(String battleId) {
      BattleInfo battle = BattlesList.getBattleInfoById(battleId);
      if (battle != null) {
         battle.model.spectatorModel.addSpectator(this.spectatorController = new SpectatorController(this, battle.model, battle.model.spectatorModel));
         this.localUser.setUserLocation(UserLocation.BATTLE);
         this.send(Type.BATTLE, "init_battle_model", JSONUtils.parseBattleModelInfo(battle, true, this.getLocalUser().getGarage()));
         Logger.log("User " + this.localUser.getNickname() + " enter in battle by spectator.");
      }
   }

   private void sendTableMessage(String msg) {
      this.send(Type.LOBBY, "server_message", msg);
   }

   private void tryCreateCTFBattle(String json) {
      if (System.currentTimeMillis() - this.localUser.getAntiCheatData().lastTimeCreationBattle <= 300000L) {
         if (this.localUser.getAntiCheatData().countCreatedBattles >= 3) {
            if (this.localUser.getAntiCheatData().countWarningForFludCreateBattle >= 5) {
               this.kick();
            }

            this.sendTableMessage("Вы можете создавать не более трех битв в течении 5 минут.");
            ++this.localUser.getAntiCheatData().countWarningForFludCreateBattle;
            return;
         }
      } else {
         this.localUser.getAntiCheatData().countCreatedBattles = 0;
         this.localUser.getAntiCheatData().countWarningForFludCreateBattle = 0;
      }

      JSONObject parser = null;

      try {
         parser = (JSONObject)(new JSONParser()).parse(json);
      } catch (ParseException var5) {
         var5.printStackTrace();
      }

      BattleInfo battle = new BattleInfo();
      battle.battleType = "CTF";
      battle.isPaid = (Boolean)parser.get("pay");
      battle.isPrivate = (Boolean)parser.get("privateBattle");
      battle.friendlyFire = (Boolean)parser.get("frielndyFire");
      battle.name = (String)parser.get("gameName");
      battle.map = (Map)MapsLoader.maps.get((String)parser.get("mapId"));
      battle.maxPeople = (int)(long)parser.get("numPlayers");
      battle.numFlags = (int)(long)parser.get("numFlags");
      battle.minRank = (int)(long)parser.get("minRang");
      battle.maxRank = (int)(long)parser.get("maxRang");
      battle.team = true;
      battle.time = (int)(long)parser.get("time");
      battle.autobalance = (Boolean)parser.get("autoBalance");
      Map map = battle.map;
      if (battle.maxRank < battle.minRank) {
         battle.maxRank = battle.minRank;
      }

      if (battle.maxPeople < 2) {
         battle.maxPeople = 2;
      }

      if (battle.time <= 0 && battle.numFlags <= 0) {
         battle.time = 15;
         battle.numFlags = 0;
      }

      if (battle.maxPeople > map.maxPlayers) {
         battle.maxPeople = map.maxPlayers;
      }

      if (battle.numKills > 999) {
         battle.numKills = 999;
      }

      BattlesList.tryCreateBatle(battle);
      this.localUser.getAntiCheatData().lastTimeCreationBattle = System.currentTimeMillis();
      ++this.localUser.getAntiCheatData().countCreatedBattles;
   }

   private void tryCreateTDMBattle(String json) {
      if (System.currentTimeMillis() - this.localUser.getAntiCheatData().lastTimeCreationBattle <= 300000L) {
         if (this.localUser.getAntiCheatData().countCreatedBattles >= 3) {
            if (this.localUser.getAntiCheatData().countWarningForFludCreateBattle >= 5) {
               this.kick();
            }

            this.sendTableMessage("Вы можете создавать не более трех битв в течении 5 минут.");
            ++this.localUser.getAntiCheatData().countWarningForFludCreateBattle;
            return;
         }
      } else {
         this.localUser.getAntiCheatData().countCreatedBattles = 0;
         this.localUser.getAntiCheatData().countWarningForFludCreateBattle = 0;
      }

      JSONObject parser = null;

      try {
         parser = (JSONObject)(new JSONParser()).parse(json);
      } catch (ParseException var5) {
         var5.printStackTrace();
      }

      BattleInfo battle = new BattleInfo();
      battle.battleType = "TDM";
      battle.isPaid = (Boolean)parser.get("pay");
      battle.isPrivate = (Boolean)parser.get("privateBattle");
      battle.friendlyFire = (Boolean)parser.get("frielndyFire");
      battle.name = (String)parser.get("gameName");
      battle.map = (Map)MapsLoader.maps.get((String)parser.get("mapId"));
      battle.maxPeople = (int)(long)parser.get("numPlayers");
      battle.numKills = (int)(long)parser.get("numKills");
      battle.minRank = (int)(long)parser.get("minRang");
      battle.maxRank = (int)(long)parser.get("maxRang");
      battle.team = true;
      battle.time = (int)(long)parser.get("time");
      battle.autobalance = (Boolean)parser.get("autoBalance");
      Map map = battle.map;
      if (battle.maxRank < battle.minRank) {
         battle.maxRank = battle.minRank;
      }

      if (battle.maxPeople < 2) {
         battle.maxPeople = 2;
      }

      if (battle.time <= 0 && battle.numKills <= 0) {
         battle.time = 900;
         battle.numKills = 0;
      }

      if (battle.maxPeople > map.maxPlayers) {
         battle.maxPeople = map.maxPlayers;
      }

      if (battle.numKills > 999) {
         battle.numKills = 999;
      }

      BattlesList.tryCreateBatle(battle);
      this.localUser.getAntiCheatData().lastTimeCreationBattle = System.currentTimeMillis();
      ++this.localUser.getAntiCheatData().countCreatedBattles;
   }

   public void onExitFromBattle() {
      if (this.battle != null) {
         if (this.autoEntryServices.removePlayer(this.battle.battle, this.getLocalUser().getNickname(), this.battle.playerTeamType, this.battle.battle.battleInfo.team)) {
            this.battle.destroy(true);
         } else {
            this.battle.destroy(false);
         }

         this.battle = null;
         this.disconnectListener.removeListener(this.battle);
      }

      if (this.spectatorController != null) {
         this.spectatorController.onDisconnect();
         this.spectatorController = null;
      }

      this.send(Type.LOBBY_CHAT, "init_messages", JSONUtils.parseChatLobbyMessages(chatLobby.getMessages()), JSONUtils.parseChatLobbyNewsObject(this.getLocalUser()));
   }

   public void onExitFromStatistic() {
      this.onExitFromBattle();
      this.sendMapsInit();
   }

   private void onEnterInTeamBattle(String battleId, boolean red) {
      this.send(Type.LOBBY, "start_battle");
      this.localUser.setUserLocation(UserLocation.BATTLE);
      if (this.battle == null) {
         BattleInfo battleInfo = BattlesList.getBattleInfoById(battleId);
         if (battleInfo != null) {
            if (battleInfo.model.players.size() < battleInfo.maxPeople * 2) {
               if (red) {
                  ++battleInfo.redPeople;
               } else {
                  ++battleInfo.bluePeople;
               }

               this.battle = new BattlefieldPlayerController(this, battleInfo.model, red ? "RED" : "BLUE");
               this.disconnectListener.addListener(this.battle);
               lobbysServices.sendCommandToAllUsers(Type.LOBBY, UserLocation.BATTLESELECT, "update_count_users_in_team_battle", JSONUtils.parseUpdateCoundPeoplesCommand(battleInfo));
               this.send(Type.BATTLE, "init_battle_model", JSONUtils.parseBattleModelInfo(battleInfo, false, this.getLocalUser().getGarage()));
               lobbysServices.sendCommandToAllUsers(Type.LOBBY, UserLocation.BATTLESELECT, "add_player_to_battle", JSONUtils.parseAddPlayerComand(this.battle, battleInfo));
            }
         }
      }
   }

   public void onEnterInBattle(String battleId) {
      this.send(Type.LOBBY, "start_battle");
      this.localUser.setUserLocation(UserLocation.BATTLE);
      this.autoEntryServices.removePlayer(this.getLocalUser().getNickname());
      if (this.battle == null) {
         BattleInfo battleInfo = BattlesList.getBattleInfoById(battleId);
         if (battleInfo != null) {
            if (battleInfo.model.players.size() < battleInfo.maxPeople) {
               this.battle = new BattlefieldPlayerController(this, battleInfo.model, "NONE");
               this.disconnectListener.addListener(this.battle);
               ++battleInfo.countPeople;
               System.out.println("incration");
               if (!battleInfo.team) {
                  lobbysServices.sendCommandToAllUsers(Type.LOBBY, UserLocation.BATTLESELECT, StringUtils.concatStrings("update_count_users_in_dm_battle", ";", battleId, ";", String.valueOf(this.battle.battle.battleInfo.countPeople)));
               } else {
                  lobbysServices.sendCommandToAllUsers(Type.LOBBY, UserLocation.BATTLESELECT, "update_count_users_in_team_battle", JSONUtils.parseUpdateCoundPeoplesCommand(battleInfo));
               }

               this.send(Type.BATTLE, "init_battle_model", JSONUtils.parseBattleModelInfo(battleInfo, false, this.getLocalUser().getGarage()));
               lobbysServices.sendCommandToAllUsers(Type.LOBBY, UserLocation.BATTLESELECT, "add_player_to_battle", JSONUtils.parseAddPlayerComand(this.battle, battleInfo));
            }
         }
      }
   }

   private void sendBattleInfo(String id) {
      this.send(Type.LOBBY, "show_battle_info", JSONUtils.parseBattleInfoShow(BattlesList.getBattleInfoById(id), this.getLocalUser().getType() != TypeUser.DEFAULT && this.getLocalUser().getType() != TypeUser.TESTER));
   }

   private void tryCreateBattleDM(String gameName, String mapId, int time, int kills, int maxPlayers, int minRang, int maxRang, boolean isPrivate, boolean pay, boolean mm) {
      if (System.currentTimeMillis() - this.localUser.getAntiCheatData().lastTimeCreationBattle <= 300000L) {
         if (this.localUser.getAntiCheatData().countCreatedBattles >= 3) {
            if (this.localUser.getAntiCheatData().countWarningForFludCreateBattle >= 5) {
               this.kick();
            }

            this.sendTableMessage("Вы можете создавать не более трех битв в течении 5 минут.");
            ++this.localUser.getAntiCheatData().countWarningForFludCreateBattle;
            return;
         }
      } else {
         this.localUser.getAntiCheatData().countCreatedBattles = 0;
         this.localUser.getAntiCheatData().countWarningForFludCreateBattle = 0;
      }

      BattleInfo battle = new BattleInfo();
      Map map = (Map)MapsLoader.maps.get(mapId);
      if (maxRang < minRang) {
         maxRang = minRang;
      }

      if (maxPlayers < 2) {
         maxPlayers = 2;
      }

      if (time <= 0 && kills <= 0) {
         time = 900;
         kills = 0;
      }

      if (maxPlayers > map.maxPlayers) {
         maxPlayers = map.maxPlayers;
      }

      if (kills > 999) {
         kills = 999;
      }

      battle.name = gameName;
      battle.map = (Map)MapsLoader.maps.get(mapId);
      battle.time = time;
      battle.numKills = kills;
      battle.maxPeople = maxPlayers;
      battle.minRank = minRang;
      battle.countPeople = 0;
      battle.maxRank = maxRang;
      battle.team = false;
      battle.isPrivate = isPrivate;
      battle.isPaid = pay;
      BattlesList.tryCreateBatle(battle);
      this.localUser.getAntiCheatData().lastTimeCreationBattle = System.currentTimeMillis();
      ++this.localUser.getAntiCheatData().countCreatedBattles;
   }

   private void checkBattleName(String name) {
      this.send(Type.LOBBY, "check_battle_name", name);
   }

   private void sendMapsInit() {
      this.localUser.setUserLocation(UserLocation.BATTLESELECT);
      this.send(Type.LOBBY, "init_battle_select", JSONUtils.parseBattleMapList());
   }

   private void sendGarage() {
      this.localUser.setUserLocation(UserLocation.GARAGE);
      this.send(Type.GARAGE, "init_garage_items", JSONUtils.parseGarageUser(this.localUser).trim());
      this.send(Type.GARAGE, "init_market", JSONUtils.parseMarketItems(this.localUser));
   }

   public synchronized void onTryUpdateItem(String id) {
      Item item = this.localUser.getGarage().getItemById(id.substring(0, id.length() - 3));
      int modificationID = Integer.parseInt(id.substring(id.length() - 1));
      if (this.checkMoney(item.modifications[modificationID + 1].price)) {
         if (this.getLocalUser().getRang() + 1 < item.modifications[modificationID + 1].rank) {
            return;
         }

         if (this.localUser.getGarage().updateItem(id)) {
            this.send(Type.GARAGE, "update_item", id);
            this.addCrystall(-item.modifications[modificationID + 1].price);
            this.localUser.getGarage().parseJSONData();
            database.update(this.localUser.getGarage());
         }
      } else {
         this.send(Type.GARAGE, "try_update_NO");
      }

   }

   public synchronized void onTryBuyItem(String itemId, int count) {
      if (count > 0 && count <= 9999) {
         Item item = (Item)GarageItemsLoader.items.get(itemId.substring(0, itemId.length() - 3));
         Item fromUser = null;
         int price = item.price * count;
         int itemRang = item.modifications[0].rank;
         if (this.checkMoney(price)) {
            if (this.getLocalUser().getRang() + 1 < itemRang) {
               return;
            }

            if ((fromUser = this.localUser.getGarage().buyItem(itemId, count, 0)) != null) {
               this.send(Type.GARAGE, "buy_item", StringUtils.concatStrings(item.id, "_m", String.valueOf(item.modificationIndex)), JSONUtils.parseItemInfo(fromUser));
               this.addCrystall(-price);
               this.localUser.getGarage().parseJSONData();
               database.update(this.localUser.getGarage());

               if (item.itemType == ItemType.INVENTORY && item.id.equals("1000_scores")) {
                  int scores = 1000 * count;
                  tanksServices.addScore(this, scores);
               }
               if (item.itemType == ItemType.PLUGIN) {
                  Garage.updateLobbyTime(this, item);
               }

            } else {
               this.send(Type.GARAGE, "try_buy_item_NO");
            }
         }

      } else {
         this.crystallToZero();
      }
   }

   private boolean checkMoney(int buyValue) {
      return this.localUser.getCrystall() - buyValue >= 0;
   }

   public synchronized void addCrystall(int value) {
      this.localUser.addCrystall(value);
      this.send(Type.LOBBY, "add_crystall", String.valueOf(this.localUser.getCrystall()));
      database.update(this.localUser);
   }

   public void crystallToZero() {
      this.localUser.setCrystall(0);
      this.send(Type.LOBBY, "add_crystall", String.valueOf(this.localUser.getCrystall()));
      database.update(this.localUser);
   }

   private boolean stringToBoolean(String src) {
      return src.toLowerCase().equals("true");
   }

   public void onDisconnect() {
      database.uncache(this.localUser.getNickname());
      lobbysServices.removeLobby(this);
      OnlineStats.removeOnline();
      if (this.spectatorController != null) {
         this.spectatorController.onDisconnect();
         this.spectatorController = null;
      }

      if (this.battle != null) {
         this.battle.onDisconnect();
         this.battle = null;
      }

      this.localUser.session = null;
   }

   public void kick() {
      this.networker.closeConnection();
   }

   public User getLocalUser() {
      return this.localUser;
   }

   public void setLocalUser(User localUser) {
      this.localUser = localUser;
   }

   public FloodController getChatFloodController() {
      return this.chatFloodController;
   }

   public void setChatFloodController(FloodController chatFloodController) {
      this.chatFloodController = chatFloodController;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$gtanks$commands$Type() {
      int[] var10000 = $SWITCH_TABLE$gtanks$commands$Type;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Type.values().length];

         try {
            var0[Type.AUTH.ordinal()] = 1;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[Type.BATTLE.ordinal()] = 7;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[Type.CHAT.ordinal()] = 4;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[Type.GARAGE.ordinal()] = 3;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[Type.HTTP.ordinal()] = 10;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[Type.LOBBY.ordinal()] = 5;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[Type.LOBBY_CHAT.ordinal()] = 6;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[Type.PING.ordinal()] = 8;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Type.REGISTRATON.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Type.SYSTEM.ordinal()] = 11;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Type.UNKNOWN.ordinal()] = 9;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$gtanks$commands$Type = var0;
         return var0;
      }
   }
}
