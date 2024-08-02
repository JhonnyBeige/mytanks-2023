package gtanks.battles;

import gtanks.StringUtils;
import gtanks.battles.comands.BattlefieldPlayerComandsConst;
import gtanks.battles.ctf.flags.FlagServer;
import gtanks.battles.effects.Effect;
import gtanks.battles.inventory.InventoryController;
import gtanks.battles.tanks.Tank;
import gtanks.battles.tanks.colormaps.ColormapsFactory;
import gtanks.battles.tanks.loaders.HullsFactory;
import gtanks.battles.tanks.loaders.WeaponsFactory;
import gtanks.battles.tanks.math.Vector3;
import gtanks.battles.tanks.statistic.PlayerStatistic;
import gtanks.commands.Command;
import gtanks.json.JSONUtils;
import gtanks.lobby.LobbyManager;
import gtanks.logger.Logger;
import gtanks.logger.Type;
import gtanks.network.listeners.IDisconnectListener;
import gtanks.services.AutoEntryServices;
import gtanks.services.LobbysServices;
import gtanks.services.annotations.ServicesInject;
import gtanks.users.User;
import gtanks.users.garage.Garage;
import gtanks.users.locations.UserLocation;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BattlefieldPlayerController extends BattlefieldPlayerComandsConst implements IDisconnectListener, Comparable<BattlefieldPlayerController> {
   public LobbyManager parentLobby;
   public BattlefieldModel battle;
   public Tank tank;
   public PlayerStatistic statistic;
   public String playerTeamType;
   public FlagServer flag;
   public InventoryController inventory;
   public long timer;
   public long lastFireTime;
   public boolean userInited = false;
   @ServicesInject(
      target = LobbysServices.class
   )
   private LobbysServices lobbysServices = LobbysServices.getInstance();
   @ServicesInject(
      target = AutoEntryServices.class
   )
   private AutoEntryServices autoEntryServices = AutoEntryServices.instance();
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$gtanks$commands$Type;

   public BattlefieldPlayerController(LobbyManager parent, BattlefieldModel battle, String playerTeamType) {
      this.parentLobby = parent;
      this.battle = battle;
      this.playerTeamType = playerTeamType;
      this.tank = new Tank((Vector3)null);
      this.tank.setHull(HullsFactory.getHull(this.getGarage().mountHull.getId()));
      this.tank.setWeapon(WeaponsFactory.getWeapon(this.getGarage().mountTurret.getId(), this, battle));
      this.tank.setColormap(ColormapsFactory.getColormap(this.getGarage().mountColormap.getId()));
      this.statistic = new PlayerStatistic(0, 0, 0);
      this.inventory = new InventoryController(this);
      battle.addPlayer(this);
      this.sendShotsData();
   }

   public User getUser() {
      return this.parentLobby.getLocalUser();
   }

   public Garage getGarage() {
      return this.parentLobby.getLocalUser().getGarage();
   }

   public void executeCommand(Command cmd) {
      try {
         switch($SWITCH_TABLE$gtanks$commands$Type()[cmd.type.ordinal()]) {
         case 3:
         case 8:
            break;
         case 4:
         case 5:
         case 6:
         default:
            Logger.log(Type.ERROR, "User " + this.parentLobby.getLocalUser().getNickname() + "[" + this.parentLobby.networker.toString() + "] send unknowed request!");
            break;
         case 7:
            if (cmd.args[0].equals("get_init_data_local_tank")) {
               this.battle.initLocalTank(this);
            } else if (cmd.args[0].equals("ping")) {
               send(gtanks.commands.Type.BATTLE, "pong");
            } else if (cmd.args[0].equals("activate_tank")) {
               this.battle.activateTank(this);
            } else if (cmd.args[0].equals("start_heat_effect")) {
               this.tank.getWeapon().HealFlame();
            } else if (cmd.args[0].equals("suicide")) {
               this.battle.respawnPlayer(this, true);
            } else if (cmd.args[0].equals("move")) {
               this.parseAndMove(cmd.args);
            } else if (cmd.args[0].equals("chat")) {
               this.battle.chatModel.onMessage(this, cmd.args[1], Boolean.valueOf(cmd.args[2]));
            } else if (cmd.args[0].equals("attempt_to_take_bonus")) {
               this.battle.onTakeBonus(this, cmd.args[1]);
            } else if (cmd.args[0].equals("start_fire")) {
               if (this.tank.state.equals("active")) {
                  this.tank.getWeapon().startFire(cmd.args.length >= 2 ? cmd.args[1] : "");
               }
            } else if (cmd.args[0].equals("fire")) {
               if (this.tank.state.equals("active")) {
                  this.tank.getWeapon().fire(cmd.args[1]);
               }
            } else if (cmd.args[0].equals("i_exit_from_battle")) {
               this.parentLobby.onExitFromBattle();
            } else if (cmd.args[0].equals("stop_fire")) {
               this.tank.getWeapon().stopFire();
            } else if (cmd.args[0].equals("exit_from_statistic")) {
               this.parentLobby.onExitFromStatistic();
            } else if (cmd.args[0].equals("attempt_to_take_flag")) {
               this.battle.ctfModel.attemptToTakeFlag(this, cmd.args[1]);
            } else if (cmd.args[0].equals("flag_drop")) {
               this.parseAndDropFlag(cmd.args[1]);
            } else if (cmd.args[0].equals("speedhack_detected")) {
               this.battle.cheatDetected(this, this.getClass());
            } else if (cmd.args[0].equals("activate_item")) {
               Vector3 _tankPos;
               try {
                  _tankPos = new Vector3(Float.parseFloat(cmd.args[2]), Float.parseFloat(cmd.args[3]), Float.parseFloat(cmd.args[4]));
               } catch (Exception var4) {
                  _tankPos = new Vector3(0.0F, 0.0F, 0.0F);
               }

               this.inventory.activateItem(cmd.args[1], _tankPos);
            } else if (cmd.args[0].equals("mine_hit")) {
               this.battle.battleMinesModel.hitMine(this, cmd.args[1]);
            } else if (cmd.args[0].equals("check_md5_map")) {
               this.battle.mapChecksumModel.check(this, cmd.args[1]);
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   private void parseAndDropFlag(String json) {
      try {
         JSONObject _json = (JSONObject)(new JSONParser()).parse(json);
         this.battle.ctfModel.dropFlag(this, new Vector3((float)(double)_json.get("x"), (float)(double)_json.get("y"), (float)(double)_json.get("z")));
      } catch (ParseException var3) {
         var3.printStackTrace();
      }

   }

   public void sendShotsData() {
      this.send(gtanks.commands.Type.BATTLE, "init_shots_data", WeaponsFactory.getJSONList());
   }

   public void parseAndMove(String[] args) {
      try {
         Vector3 pos = new Vector3(0.0F, 0.0F, 0.0F);
         Vector3 orient = new Vector3(0.0F, 0.0F, 0.0F);
         Vector3 line = new Vector3(0.0F, 0.0F, 0.0F);
         Vector3 ange = new Vector3(0.0F, 0.0F, 0.0F);
         float turretDir = 0.0F;
         String[] temp = args[1].split("@");
         pos.x = Float.parseFloat(temp[0]);
         pos.y = Float.parseFloat(temp[1]);
         pos.z = Float.parseFloat(temp[2]);
         orient.x = Float.parseFloat(temp[3]);
         orient.y = Float.parseFloat(temp[4]);
         orient.z = Float.parseFloat(temp[5]);
         line.x = Float.parseFloat(temp[6]);
         line.y = Float.parseFloat(temp[7]);
         line.z = Float.parseFloat(temp[8]);
         ange.x = Float.parseFloat(temp[9]);
         ange.y = Float.parseFloat(temp[10]);
         ange.z = Float.parseFloat(temp[11]);
         temp = null;
         turretDir = Float.parseFloat(args[2]);
         int bits = Integer.parseInt(args[3]);
         if (this.tank.position == null) {
            this.tank.position = new Vector3(0.0F, 0.0F, 0.0F);
         }

         this.tank.position = pos;
         this.tank.orientation = orient;
         this.tank.linVel = line;
         this.tank.angVel = ange;
         this.tank.turretDir = (double)turretDir;
         this.tank.controllBits = bits;
         this.battle.moveTank(this);
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }

   public void clearEffects() {
      while(this.tank.activeEffects.size() > 0) {
         ((Effect)this.tank.activeEffects.get(0)).deactivate();
      }

   }

   public void toggleTeamType() {
      if (!this.playerTeamType.equals("NONE")) {
         if (this.playerTeamType.equals("BLUE")) {
            this.playerTeamType = "RED";
            ++this.battle.battleInfo.redPeople;
            --this.battle.battleInfo.bluePeople;
         } else {
            this.playerTeamType = "BLUE";
            --this.battle.battleInfo.redPeople;
            ++this.battle.battleInfo.bluePeople;
         }

         this.lobbysServices.sendCommandToAllUsers(gtanks.commands.Type.LOBBY, UserLocation.BATTLESELECT, "update_count_users_in_team_battle", JSONUtils.parseUpdateCoundPeoplesCommand(this.battle.battleInfo));
         this.battle.sendToAllPlayers(gtanks.commands.Type.BATTLE, "change_user_team", this.tank.id, this.playerTeamType);
      }
   }

   public void destroy(boolean cache) {
      this.battle.removeUser(this, cache);
      if (!cache) {
         this.lobbysServices.sendCommandToAllUsers(gtanks.commands.Type.LOBBY, UserLocation.BATTLESELECT, "remove_player_from_battle", JSONUtils.parseRemovePlayerComand(this));
         if (!this.battle.battleInfo.team) {
            this.lobbysServices.sendCommandToAllUsers(gtanks.commands.Type.LOBBY, UserLocation.BATTLESELECT, StringUtils.concatStrings("update_count_users_in_dm_battle", ";", this.battle.battleInfo.battleId, ";", String.valueOf(this.battle.players.size())));
         } else {
            this.lobbysServices.sendCommandToAllUsers(gtanks.commands.Type.LOBBY, UserLocation.BATTLESELECT, "update_count_users_in_team_battle", JSONUtils.parseUpdateCoundPeoplesCommand(this.battle.battleInfo));
         }
      }

      this.parentLobby = null;
      this.battle = null;
      this.tank = null;
   }

   public void send(gtanks.commands.Type type, String... args) {
      if (this.parentLobby != null) {
         this.parentLobby.send(type, args);
      }

   }

   public void onDisconnect() {
      this.autoEntryServices.userExit(this);
      this.destroy(true);
   }

   public int compareTo(BattlefieldPlayerController o) {
      return (int)(o.statistic.getScore() - this.statistic.getScore());
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$gtanks$commands$Type() {
      int[] var10000 = $SWITCH_TABLE$gtanks$commands$Type;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[gtanks.commands.Type.values().length];

         try {
            var0[gtanks.commands.Type.AUTH.ordinal()] = 1;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[gtanks.commands.Type.BATTLE.ordinal()] = 7;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[gtanks.commands.Type.CHAT.ordinal()] = 4;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[gtanks.commands.Type.GARAGE.ordinal()] = 3;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[gtanks.commands.Type.HTTP.ordinal()] = 10;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[gtanks.commands.Type.LOBBY.ordinal()] = 5;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[gtanks.commands.Type.LOBBY_CHAT.ordinal()] = 6;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[gtanks.commands.Type.PING.ordinal()] = 8;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[gtanks.commands.Type.REGISTRATON.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[gtanks.commands.Type.SYSTEM.ordinal()] = 11;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[gtanks.commands.Type.UNKNOWN.ordinal()] = 9;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$gtanks$commands$Type = var0;
         return var0;
      }
   }
}
