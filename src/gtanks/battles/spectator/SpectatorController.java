package gtanks.battles.spectator;

import gtanks.battles.BattlefieldModel;
import gtanks.battles.spectator.comands.SpectatorControllerComandsConst;
import gtanks.battles.tanks.loaders.WeaponsFactory;
import gtanks.commands.Command;
import gtanks.commands.Type;
import gtanks.json.JSONUtils;
import gtanks.lobby.LobbyManager;
import gtanks.logger.Logger;
import gtanks.network.listeners.IDisconnectListener;
import gtanks.users.User;

public class SpectatorController extends SpectatorControllerComandsConst implements IDisconnectListener {
   private static final String NULL_JSON_STRING = "{}";
   private LobbyManager lobby;
   private BattlefieldModel bfModel;
   private SpectatorModel specModel;
   private boolean inited;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$gtanks$commands$Type;

   public SpectatorController(LobbyManager lobby, BattlefieldModel bfModel, SpectatorModel specModel) {
      this.lobby = lobby;
      this.bfModel = bfModel;
      this.specModel = specModel;
   }

   public void executeCommand(Command cmd) {
      switch($SWITCH_TABLE$gtanks$commands$Type()[cmd.type.ordinal()]) {
      case 7:
         if (cmd.args[0].equals("spectator_user_init")) {
            this.initUser();
         }

         if (cmd.args[0].equals("i_exit_from_battle")) {
            this.lobby.onExitFromBattle();
         }

         if (cmd.args[0].equals("chat")) {
            this.specModel.getChatModel().onMessage(cmd.args[1], this);
         }
         break;
      default:
         Logger.log("[executeCommand(Command)::SpectatorController] : non-battle command " + cmd);
      }

   }

   private void initUser() {
      try {
         this.inited = true;
         this.sendShotsData();
         if (this.bfModel.battleInfo.battleType.equals("CTF")) {
            this.sendCommand(Type.BATTLE, "init_ctf_model", JSONUtils.parseCTFModelData(this.bfModel));
         }

         this.sendCommand(Type.BATTLE, "init_gui_model", JSONUtils.parseBattleData(this.bfModel));
         this.sendCommand(Type.BATTLE, "init_inventory", "{}");
         this.bfModel.battleMinesModel.initModel(this);
         this.bfModel.battleMinesModel.sendMines(this);
         this.bfModel.sendAllTanks(this);
         this.bfModel.effectsModel.sendInitData(this);
      } catch (Exception var2) {
         this.lobby.kick();
      }

   }

   public String getId() {
      return this.lobby.getLocalUser().getNickname();
   }

   public User getUser() {
      return this.lobby.getLocalUser();
   }

   public void sendCommand(Type type, String... args) {
      if (this.inited) {
         this.lobby.send(type, args);
      }

   }

   private void sendShotsData() {
      this.sendCommand(Type.BATTLE, "init_shots_data", WeaponsFactory.getJSONList());
   }

   public void onDisconnect() {
      this.bfModel.spectatorModel.removeSpectator(this);
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
