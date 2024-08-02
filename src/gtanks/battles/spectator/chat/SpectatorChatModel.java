package gtanks.battles.spectator.chat;

import gtanks.StringUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.bonuses.BonusType;
import gtanks.battles.chat.BattlefieldChatModel;
import gtanks.battles.spectator.SpectatorController;
import gtanks.battles.spectator.SpectatorModel;
import gtanks.commands.Type;
import gtanks.lobby.LobbyManager;
import gtanks.main.database.DatabaseManager;
import gtanks.main.database.impl.DatabaseManagerImpl;
import gtanks.services.BanServices;
import gtanks.services.LobbysServices;
import gtanks.services.annotations.ServicesInject;
import gtanks.services.ban.BanChatCommads;
import gtanks.services.ban.BanTimeType;
import gtanks.services.ban.BanType;
import gtanks.services.ban.block.BlockGameReason;
import gtanks.users.User;

public class SpectatorChatModel {
   private static final String CHAT_SPECTATOR_COMAND = "spectator_message";
   private SpectatorModel spModel;
   private BattlefieldModel bfModel;
   private BattlefieldChatModel chatModel;
   @ServicesInject(
      target = DatabaseManagerImpl.class
   )
   private DatabaseManager database = DatabaseManagerImpl.instance();
   @ServicesInject(
      target = LobbysServices.class
   )
   private LobbysServices lobbyServices = LobbysServices.getInstance();
   @ServicesInject(
      target = BanServices.class
   )
   private BanServices banServices = BanServices.getInstance();

   public SpectatorChatModel(SpectatorModel spModel) {
      this.spModel = spModel;
      this.bfModel = spModel.getBattleModel();
      this.chatModel = this.bfModel.chatModel;
   }

   public void onMessage(String message, SpectatorController spectator) {
      if (message.startsWith("/")) {
         String[] arguments = message.replace('/', ' ').trim().split(" ");
         if (!spectator.getUser().getUserGroup().isAvaliableChatCommand(arguments[0])) {
            return;
         }

         String var4;
         int i;
         label116:
         switch((var4 = arguments[0]).hashCode()) {
         case -372425125:
            if (!var4.equals("spawngold")) {
               break;
            }

            i = 0;

            while(true) {
               if (i >= Integer.parseInt(arguments[1])) {
                  break label116;
               }

               this.spModel.getBattleModel().bonusesSpawnService.spawnBonus(BonusType.GOLD);
               ++i;
            }
         case 119:
            if (var4.equals("w")) {
               if (arguments.length < 3) {
                  return;
               }

               User giver = this.database.getUserById(arguments[1]);
               if (giver != null) {
                  String reason = StringUtils.concatMassive(arguments, 2);
                  this.chatModel.sendSystemMessage(StringUtils.concatStrings("Танкист ", giver.getNickname(), " предупрежден. Причина: ", reason));
               }
            }
            break;
         case 3291718:
            if (var4.equals("kick")) {
               User _userForKick = this.database.getUserById(arguments[1]);
               if (_userForKick != null) {
                  LobbyManager _lobby = this.lobbyServices.getLobbyByUser(_userForKick);
                  if (_lobby != null) {
                     _lobby.kick();
                     this.chatModel.sendSystemMessage(_userForKick.getNickname() + " кикнут");
                  }
               }
            }
            break;
         case 111426262:
            if (var4.equals("unban") && arguments.length >= 2) {
               User cu = this.database.getUserById(arguments[1]);
               if (cu != null) {
                  this.banServices.unbanChat(cu);
                  this.chatModel.sendSystemMessage("Танкисту " + cu.getNickname() + " был разрешён выход в эфир");
               }
            }
            break;
         case 873005567:
            if (var4.equals("blockgame")) {
               if (arguments.length < 3) {
                  return;
               }

               User victim_ = this.database.getUserById(arguments[1]);
               boolean var6 = false;

               int reasonId;
               try {
                  reasonId = Integer.parseInt(arguments[2]);
               } catch (Exception var14) {
                  reasonId = 0;
               }

               if (victim_ != null) {
                  this.banServices.ban(BanType.GAME, BanTimeType.FOREVER, victim_, spectator.getUser(), BlockGameReason.getReasonById(reasonId).getReason());
                  LobbyManager lobby = this.lobbyServices.getLobbyByNick(victim_.getNickname());
                  if (lobby != null) {
                     lobby.kick();
                  }

                  this.chatModel.sendSystemMessage(StringUtils.concatStrings("Танкист ", victim_.getNickname(), " был заблокирован и кикнут"));
               }
            }
            break;
         case 941444998:
            if (var4.equals("unblockgame")) {
               if (arguments.length < 2) {
                  return;
               }

               User av = this.database.getUserById(arguments[1]);
               if (av != null) {
                  this.banServices.unblock(av);
                  this.chatModel.sendSystemMessage(av.getNickname() + " разблокирован");
               }
            }
            break;
         case 2066192527:
            if (var4.equals("spawncry")) {
               for(i = 0; i < Integer.parseInt(arguments[1]); ++i) {
                  this.spModel.getBattleModel().bonusesSpawnService.spawnBonus(BonusType.CRYSTALL);
               }
            }
         }

         if (message.startsWith("/ban")) {
            BanTimeType time = BanChatCommads.getTimeType(arguments[0]);
            if (arguments.length < 3) {
               return;
            }

            String reason = StringUtils.concatMassive(arguments, 2);
            if (time == null) {
               return;
            }

            User _victim = this.database.getUserById(arguments[1]);
            if (_victim == null) {
               return;
            }

            this.banServices.ban(BanType.CHAT, time, _victim, spectator.getUser(), reason);
            this.chatModel.sendSystemMessage(StringUtils.concatStrings("Танкист ", _victim.getNickname(), " лишен права выхода в эфир ", time.getNameType(), " Причина: ", reason));
         }
      } else {
         this.spModel.getBattleModel().sendToAllPlayers(Type.BATTLE, "spectator_message", message);
      }

   }
}
