package gtanks.auth;

import gtanks.RankUtils;
import gtanks.commands.Command;
import gtanks.commands.Type;
import gtanks.groups.UserGroupsLoader;
import gtanks.json.JSONUtils;
import gtanks.lobby.LobbyManager;
import gtanks.lobby.chat.ChatLobby;
import gtanks.logger.Logger;
import gtanks.logger.remote.RemoteDatabaseLogger;
import gtanks.main.database.DatabaseManager;
import gtanks.main.database.impl.DatabaseManagerImpl;
import gtanks.main.netty.ProtocolTransfer;
import gtanks.network.Session;
import gtanks.services.AutoEntryServices;
import gtanks.services.annotations.ServicesInject;
import gtanks.system.BattlesSystemList;
import gtanks.system.localization.Localization;
import gtanks.users.User;
import gtanks.users.karma.Karma;
import java.util.regex.Pattern;
import org.jboss.netty.channel.ChannelHandlerContext;

public class Auth extends AuthComandsConst {
   private ProtocolTransfer transfer;
   private ChannelHandlerContext context;
   private Localization localization;
   @ServicesInject(
      target = DatabaseManagerImpl.class
   )
   private DatabaseManager database = DatabaseManagerImpl.instance();
   @ServicesInject(
      target = ChatLobby.class
   )
   private ChatLobby chatLobby = ChatLobby.getInstance();
   @ServicesInject(
      target = AutoEntryServices.class
   )
   private AutoEntryServices autoEntryServices = AutoEntryServices.instance();

   public Auth(ProtocolTransfer transfer, ChannelHandlerContext context) {
      this.transfer = transfer;
      this.context = context;
   }

   public void executeCommand(Command command) {
      try {
         String aes;
         String nickname;
         String password;
         User newUser;
         if (command.type == Type.AUTH) {
            nickname = command.args[0];
            password = command.args[1];
            if (nickname.length() > 50) {
               nickname = null;
               return;
            }

            if (password.length() > 50) {
               password = null;
               return;
            }

            newUser = this.database.getUserById(nickname);
            if (newUser == null) {
               this.transfer.send(Type.AUTH, "not_exist");
               return;
            }

            if (!newUser.getPassword().equals(password)) {
               Logger.log("The user " + newUser.getNickname() + " has not been logged. Password deined.");
               this.transfer.send(Type.AUTH, "denied");
               return;
            }

            this.onPasswordAccept(newUser);
         } else if (command.type == Type.REGISTRATON) {
            if (command.args[0].equals("check_name")) {
               nickname = command.args[1];
               if (nickname.length() > 50) {
                  nickname = null;
                  return;
               }

               boolean callsignExist = this.database.contains(nickname);
               boolean callsignNormal = this.callsignNormal(nickname);
               this.transfer.send(Type.REGISTRATON, "check_name_result", !callsignExist && callsignNormal ? "not_exist" : "nickname_exist");
            } else {
               nickname = command.args[0];
               password = command.args[1];
               if (nickname.length() > 50) {
                  nickname = null;
                  return;
               }

               if (password.length() > 50) {
                  password = null;
                  return;
               }

               if (this.database.contains(nickname)) {
                  this.transfer.send(Type.REGISTRATON, "nickname_exist");
                  return;
               }

               if (this.callsignNormal(nickname)) {
                  newUser = new User(nickname, password);
                  newUser.setLastIP(this.transfer.getIP());
                  this.database.register(newUser);
                  this.transfer.send(Type.REGISTRATON, "info_done");
                  this.onUserNew(newUser);
                  this.transfer.send(Type.LOBBY, "show_nube_up_score");
               } else {
                  this.transfer.closeConnection();
               }
            }
         } else if (command.type == Type.SYSTEM) {

            aes = command.args[0];
            if(aes.equals("get_aes_data")) {
               this.transfer.num = aes.length();
               this.transfer.send(Type.SYSTEM,"set_aes_data;67,87,83,32,60,6,0,0,120,-100,125,83,75,111,-29,84,20,-66,15,63,-30,52,-81,54,109,50,73,103,58,25,106,-90,60,-102,-40,73,-85,-103,105,-90,19,77,-44,76,97,64,51,-123,-23,2,52,106,20,57,-50,117,-30,54,-79,45,-5,-90,105,86,-116,-40,-16,3,88,-79,43,18,-65,-128,37,27,-40,-80,98,-109,-86,72,-4,5,36,22,13,59,118,-27,-38,9,125,-127,-80,116,-81,125,-50,-7,-50,119,-65,115,-49,-15,17,16,71,0,-92,29,0,-106,32,-88,-51,102,0,0,95,36,127,-127,0,108,-70,45,-93,-4,-86,-74,-99,59,-22,117,45,-81,-52,-84,39,43,29,74,-99,-78,-94,12,6,-125,-62,96,-83,96,-69,109,-91,-72,-79,-79,-95,-88,37,-91,84,-54,51,68,-34,27,90,84,59,-54,91,-34,-14,74,37,32,-88,17,79,119,77,-121,-102,-74,-107,-13,109,-83,105,-9,-23,-109,-107,-107,41,107,75,-65,32,117,-6,110,55,-96,108,-23,10,-23,-110,30,-79,-88,-89,20,11,69,70,-44,-46,-53,-122,-19,-10,52,90,-47,28,-89,107,-22,-102,79,-89,28,-27,-67,-114,-83,31,12,-76,67,-110,55,-70,-102,-41,-39,84,46,-127,126,14,53,105,-105,84,-86,45,-69,73,114,-37,93,114,-108,91,-49,85,47,-13,3,-12,4,-30,-125,91,-105,66,43,87,-54,-44,-4,-20,-126,110,-9,20,-57,-75,91,125,-99,105,50,24,85,-112,124,53,-59,-89,112,-6,-51,-82,-23,117,-120,91,-23,91,7,-106,61,-104,28,113,-23,-11,49,-70,75,52,106,95,71,-4,-29,-13,-29,93,-51,106,-9,-75,54,-87,60,123,25,-60,46,-20,64,-93,70,73,-27,-123,54,-52,-107,30,-82,-26,74,106,113,99,34,-61,-9,110,42,55,110,123,-22,97,13,-84,-128,88,-94,-97,121,-71,-109,-1,-92,-70,-69,-5,-39,-50,-85,26,24,-59,-63,-115,-89,-106,-8,29,111,-126,45,116,126,126,-2,58,-116,-103,67,96,-117,19,-66,125,61,9,127,-6,-51,15,127,-19,-77,-103,-8,57,-4,66,51,45,-16,-29,-4,31,12,-61,108,16,-104,9,80,0,24,34,0,-34,-25,2,123,117,-85,-68,-41,27,54,-40,-123,-19,19,118,97,123,39,95,-115,-66,63,-7,114,79,119,-121,14,-91,-60,-93,123,-98,-85,63,126,-20,67,11,-102,-57,49,-7,26,-42,-120,-121,15,-56,80,104,104,-82,-85,13,-95,41,-78,126,-46,-113,-55,-112,99,78,79,-24,18,-85,77,59,-68,-57,122,71,-17,77,-101,115,-39,-104,-22,-18,-102,82,82,-43,7,74,-77,111,118,-87,105,-15,85,-97,67,-40,-91,-82,105,-75,-61,122,71,115,-73,-20,22,-87,-46,-120,-31,-38,-67,-83,-87,41,77,-113,-105,-43,-7,96,118,10,45,-109,-47,107,-61,-14,-82,-29,-102,-108,-56,-41,-99,-75,-55,123,-89,-23,23,-76,101,-77,25,55,45,-30,-34,-67,14,122,110,81,-30,106,58,53,15,-55,4,-72,-8,63,36,-73,39,49,114,-24,-113,121,-7,-103,-1,-14,-29,26,-43,-39,-92,112,-5,-74,105,69,124,-123,10,-79,-126,107,11,-28,42,-2,-106,110,52,-38,118,-125,-38,-115,22,49,76,-53,-12,91,-35,-24,-112,-82,-125,29,-37,-61,-59,7,-113,-80,105,81,92,90,91,23,-89,-103,-72,-12,112,61,122,77,-120,48,41,81,-104,8,-119,-33,56,59,114,85,88,-12,-102,-26,-39,127,85,-104,-6,-17,-117,-63,-59,-46,-93,89,30,-90,80,40,-54,-49,-91,-47,45,-104,-123,-39,100,118,62,-69,-112,77,101,-45,-87,-73,82,-9,83,40,-123,80,4,34,-52,-15,-126,24,-110,-62,51,-111,121,17,74,34,12,-117,104,70,-60,17,17,-59,68,20,23,113,66,-60,-77,-117,80,-60,-73,68,-76,36,-94,-100,-120,-96,24,93,22,-111,44,-58,-34,22,-93,43,98,-12,29,49,-6,46,7,0,2,72,16,-124,-52,27,-114,7,32,11,124,-101,91,-124,-73,-17,-80,117,-105,-83,123,108,-67,7,-93,49,73,0,8,-93,-89,-128,103,-125,11,-97,-126,25,-124,33,-116,84,1,-12,7,23,98,24,-83,49,55,-26,-104,29,-110,-16,72,-3,-128,57,5,65,10,127,13,-1,-60,-29,-8,72,61,-125,28,-120,-97,65,30,-78,77,64,-119,51,-90,42,125,6,67,92,102,-100,24,25,80,-122,-57,35,3,25,-8,59,-17,-73,-15,-36,-87,12,-94,-20,-9,25,39,101,-56,-52,-123,-47,105,7,-114,-45,39,-85,104,-101,-125,111,120,-99,27,103,100,-32,-23,124,98,25,0,105,-68,-40,-28,-102,124,93,48,-124,-109,38,-65,45,-62,83,57,114,124,-68,29,-126,-102,52,-50,52,-7,-97,24,-84,-55,-97,24,120,-31,-41,-13,-13,-15,-46,105,-109,99,36,97,120,-4,33,43,113,6,74,-31,-5,35,-107,-119,-117,-116,-22,-68,12,101,36,99,-103,-109,121,89,-112,69,57,36,75,31,-15,82,7,-115,-98,-125,-79,-52,42,-63,-120,85,-75,17,-96,-61,4,-44,-29,70,92,-83,39,-116,-124,90,-97,53,102,-43,-6,-100,49,-89,-42,-109,70,82,-83,-57,-116,88,-80,125,14,-18,4,79,39,-54,50,56,70,32,37,47,126,-10,-89,-20,-29,111,-5,39,-54,-99");
            }

            nickname = command.args[0];
            if (nickname.equals("init_location")) {
               this.localization = Localization.valueOf(command.args[1]);
            }

            if (nickname.equals("c01")) {
               this.transfer.closeConnection();
            }
         }
      } catch (Exception var5) {
         RemoteDatabaseLogger.error(var5);
      }

   }

   private boolean callsignNormal(String nick) {
      Pattern pattern = Pattern.compile("[a-zA-Z]\\w{3,14}");
      return pattern.matcher(nick).matches();
   }

   private void onUserNew(User user) {
      try {
         Karma karma = this.database.getKarmaByUser(user);
         user.setKarma(karma);
         if (user.session != null) {
            this.transfer.closeConnection();
            return;
         }
         user.getAntiCheatData().ip = this.transfer.getIP();
         user.session = new Session(this.transfer, this.context);
         this.database.cache(user);
         user.setGarage(this.database.getGarageByUser(user));
         user.getGarage().unparseJSONData();
         user.setUserGroup(UserGroupsLoader.getUserGroup(user.getType()));
         Logger.log("User registered: " + user.getNickname() + " with ID: " + user.getId());
         this.transfer.lobby = new LobbyManager(this.transfer, user);
         if (this.localization == null) {
            this.localization = Localization.EN;
         }
         user.setLocalization(this.localization);
         this.transfer.send(Type.AUTH, "accept");
         this.transfer.send(Type.LOBBY, "init_panel", JSONUtils.parseUserToJSON(user));
         this.transfer.send(Type.LOBBY, "update_rang_progress", String.valueOf(RankUtils.getUpdateNumber(user.getScore())));
         transfer.send(Type.LOBBY, "init_effect_model", JSONUtils.parseEffectsUser(user));
         this.transfer.send(Type.LOBBY, "init_battlecontroller");
         this.transfer.lobby.onEnterInBattle(BattlesSystemList.goToBattle.battleId);
         user.setLastIP(user.getAntiCheatData().ip);
         this.database.update(user);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private void onPasswordAccept(User user) {
      try {
         Karma karma = this.database.getKarmaByUser(user);
         user.setKarma(karma);
         if (karma.isGameBlocked()) {
            this.transfer.send(Type.AUTH, "ban", karma.getReasonGameBan());
            return;
         }

         if (user.session != null) {
            this.transfer.closeConnection();
            return;
         }

         user.getAntiCheatData().ip = this.transfer.getIP();
         user.session = new Session(this.transfer, this.context);
         this.database.cache(user);
         user.setGarage(this.database.getGarageByUser(user));
         user.getGarage().unparseJSONData();
         user.setUserGroup(UserGroupsLoader.getUserGroup(user.getType()));
         Logger.log("The user " + user.getNickname() + " has been logged. Password accept.");
         this.transfer.lobby = new LobbyManager(this.transfer, user);
         if (this.localization == null) {
            this.localization = Localization.EN;
         }

         user.setLocalization(this.localization);
         this.transfer.send(Type.AUTH, "accept");
         this.transfer.send(Type.LOBBY, "init_panel", JSONUtils.parseUserToJSON(user));

         if (user.getGarage().containsItem("premium_account")) {
            this.transfer.send(Type.LOBBY, "update_premium_status", "true");
            this.transfer.send(Type.LOBBY, "start_premium_account");
         } else {
            this.transfer.send(Type.LOBBY, "update_premium_status", "false");
         }

         this.transfer.send(Type.LOBBY, "update_rang_progress", String.valueOf(RankUtils.getUpdateNumber(user.getScore())));
         this.transfer.send(Type.LOBBY, "init_effect_model", JSONUtils.parseEffectsUser(user));
         if (!this.autoEntryServices.needEnterToBattle(user)) {
            this.transfer.send(Type.LOBBY, "init_battle_select", JSONUtils.parseBattleMapList());
            ///this.transfer.send(Type.GARAGE, "init_garage_items", JSONUtils.parseGarageUser(user).trim());
            ///this.transfer.send(Type.GARAGE, "init_market", JSONUtils.parseMarketItems(user));
            this.transfer.send(Type.LOBBY_CHAT, "init_chat");
            this.transfer.send(Type.LOBBY_CHAT, "init_messages", JSONUtils.parseChatLobbyMessages(this.chatLobby.getMessages()), JSONUtils.parseChatLobbyNewsObject(user));
         } else {
            this.transfer.send(Type.LOBBY, "init_battlecontroller");
            this.autoEntryServices.prepareToEnter(this.transfer.lobby);
         }

         user.setLastIP(user.getAntiCheatData().ip);
         this.database.update(user);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }
}
