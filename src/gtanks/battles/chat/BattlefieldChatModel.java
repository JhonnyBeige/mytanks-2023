package gtanks.battles.chat;

import gtanks.StringUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.bonuses.BonusType;
import gtanks.commands.Type;
import gtanks.json.JSONUtils;
import gtanks.lobby.LobbyManager;
import gtanks.main.database.DatabaseManager;
import gtanks.main.database.impl.DatabaseManagerImpl;
import gtanks.main.params.OnlineStats;
import gtanks.services.BanServices;
import gtanks.services.LobbysServices;
import gtanks.services.TanksServices;
import gtanks.services.annotations.ServicesInject;
import gtanks.services.ban.BanChatCommads;
import gtanks.services.ban.BanTimeType;
import gtanks.services.ban.BanType;
import gtanks.services.ban.DateFormater;
import gtanks.services.ban.block.BlockGameReason;
import gtanks.users.TypeUser;
import gtanks.users.User;
import gtanks.users.karma.Karma;
import java.util.Date;

public class BattlefieldChatModel {
   private BattlefieldModel bfModel;
   private final int MAX_WARNING = 5;
   @ServicesInject(
      target = TanksServices.class
   )
   private TanksServices tanksServices = TanksServices.getInstance();
   @ServicesInject(
      target = DatabaseManager.class
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

   public BattlefieldChatModel(BattlefieldModel bfModel) {
      this.bfModel = bfModel;
   }

   public void onMessage(BattlefieldPlayerController player, String message, boolean team) {
      if (!(message = message.trim()).isEmpty()) {
         Karma karma = this.database.getKarmaByUser(player.getUser());
         if (karma.isChatBanned()) {
            long currDate = System.currentTimeMillis();
            Date banTo = karma.getChatBannedBefore();
            long delta = currDate - banTo.getTime();
            if (delta <= 0L) {
               player.parentLobby.send(Type.LOBBY_CHAT, "system", StringUtils.concatStrings("Вы отключены от чата. Вы вернётесь в ЭФИР через ", DateFormater.formatTimeToUnban(delta), ". Причина: " + karma.getReasonChatBan()));
               return;
            }

            this.banServices.unbanChat(player.getUser());
         }

         if (!this.bfModel.battleInfo.team) {
            team = false;
         }

         String reason;
         if (message.startsWith("/")) {
            if (player.getUser().getType() == TypeUser.DEFAULT) {
               return;
            }

            String[] arguments = message.replace('/', ' ').trim().split(" ");
            if (!player.getUser().getUserGroup().isAvaliableChatCommand(arguments[0])) {
               return;
            }

            User victim_;
            label188: {
               int i;
               switch((reason = arguments[0]).hashCode()) {
               case -1422509655:
                  if (reason.equals("addcry")) {
                     this.tanksServices.addCrystall(player.parentLobby, this.getInt(arguments[1]));
                     break label188;
                  }
                  break;
               case -1217854831:
                  if (reason.equals("addscore")) {
                     i = this.getInt(arguments[1]);
                     if (player.parentLobby.getLocalUser().getScore() + i < 0) {
                        this.sendSystemMessage("[SERVER]: Ваше количество очков опыта не должно быть отрицательное!", player);
                     } else {
                        this.tanksServices.addScore(player.parentLobby, i);
                     }
                     break label188;
                  }
                  break;
               case -1012222381:
                  if (reason.equals("online")) {
                     this.sendSystemMessage("Current online: " + OnlineStats.getOnline() + "\nMax online: " + OnlineStats.getMaxOnline(), player);
                     break label188;
                  }
                  break;
               case -887328209:
                  if (reason.equals("system")) {
                     StringBuffer total = new StringBuffer();

                     for(i = 1; i < arguments.length; ++i) {
                        total.append(arguments[i]).append(" ");
                     }

                     this.sendSystemMessage(total.toString());
                     break label188;
                  }
                  break;
               case -372425125:
                  if (reason.equals("spawngold")) {
                     i = 0;

                     while(true) {
                        if (i >= Integer.parseInt(arguments[1])) {
                           break label188;
                        }

                        this.bfModel.bonusesSpawnService.spawnBonus(BonusType.GOLD);
                        ++i;
                     }
                  }
                  break;
               case 119:
                  if (reason.equals("w")) {
                     if (arguments.length < 3) {
                        return;
                     }

                     User giver = this.database.getUserById(arguments[1]);
                     if (giver == null) {
                        this.sendSystemMessage("[SERVER]: Игрок не найден!", player);
                     } else {
                        reason = StringUtils.concatMassive(arguments, 2);
                        this.sendSystemMessage(StringUtils.concatStrings("Танкист ", giver.getNickname(), " предупрежден. Причина: ", reason));
                     }
                     break label188;
                  }
                  break;
               case 3291718:
                  if (reason.equals("kick")) {
                     User _userForKick = this.database.getUserById(arguments[1]);
                     if (_userForKick == null) {
                        this.sendSystemMessage("[SERVER]: Игрок не найден", player);
                     } else {
                        LobbyManager _lobby = this.lobbyServices.getLobbyByUser(_userForKick);
                        if (_lobby != null) {
                           _lobby.kick();
                           this.sendSystemMessage(_userForKick.getNickname() + " кикнут");
                        }
                     }
                     break label188;
                  }
                  break;
               case 98246397:
                  if (reason.equals("getip")) {
                     if (arguments.length >= 2) {
                        User shower = this.database.getUserById(arguments[1]);
                        if (shower == null) {
                           return;
                        }

                        String ip = shower.getAntiCheatData().ip;
                        if (ip == null) {
                           ip = shower.getLastIP();
                        }

                        this.sendSystemMessage("IP user " + shower.getNickname() + " : " + ip, player);
                     }
                     break label188;
                  }
                  break;
               case 111426262:
                  if (reason.equals("unban")) {
                     if (arguments.length >= 2) {
                        User cu = this.database.getUserById(arguments[1]);
                        if (cu == null) {
                           this.sendSystemMessage("[SERVER]: Игрок не найден!", player);
                        } else {
                           this.banServices.unbanChat(cu);
                           this.sendSystemMessage("Танкисту " + cu.getNickname() + " был разрешён выход в эфир");
                        }
                     }
                     break label188;
                  }
                  break;
               case 873005567:
                  if (reason.equals("blockgame")) {
                     if (arguments.length < 3) {
                        return;
                     }

                     victim_ = this.database.getUserById(arguments[1]);
                     boolean var10 = false;

                     int reasonId;
                     try {
                        reasonId = Integer.parseInt(arguments[2]);
                     } catch (Exception var20) {
                        reasonId = 0;
                     }

                     if (victim_ == null) {
                        this.sendSystemMessage("[SERVER]: Игрок не найден!", player);
                     } else {
                        this.banServices.ban(BanType.GAME, BanTimeType.FOREVER, victim_, player.getUser(), BlockGameReason.getReasonById(reasonId).getReason());
                        LobbyManager lobby = this.lobbyServices.getLobbyByNick(victim_.getNickname());
                        if (lobby != null) {
                           lobby.kick();
                        }

                        this.sendSystemMessage(StringUtils.concatStrings("Танкист ", victim_.getNickname(), " был заблокирован и кикнут"));
                     }
                     break label188;
                  }
                  break;
               case 941444998:
                  if (reason.equals("unblockgame")) {
                     if (arguments.length < 2) {
                        return;
                     }

                     User av = this.database.getUserById(arguments[1]);
                     if (av == null) {
                        this.sendSystemMessage("[SERVER]: Игрок не найден!", player);
                     } else {
                        this.banServices.unblock(av);
                        this.sendSystemMessage(av.getNickname() + " разблокирован");
                     }
                     break label188;
                  }
                  break;
               case 2066192527:
                  if (reason.equals("spawncry")) {
                     i = 0;

                     while(true) {
                        if (i >= Integer.parseInt(arguments[1])) {
                           break label188;
                        }

                        this.bfModel.bonusesSpawnService.spawnBonus(BonusType.CRYSTALL);
                        ++i;
                     }
                  }
               }

               if (!message.startsWith("/ban")) {
                  this.sendSystemMessage("[SERVER]: Неизвестная команда!", player);
               }
            }

            if (message.startsWith("/ban")) {
               BanTimeType time = BanChatCommads.getTimeType(arguments[0]);
               if (arguments.length < 3) {
                  return;
               }

               reason = StringUtils.concatMassive(arguments, 2);
               if (time == null) {
                  this.sendSystemMessage("[SERVER]: Команда бана не найдена!", player);
                  return;
               }

               victim_ = this.database.getUserById(arguments[1]);
               if (victim_ == null) {
                  this.sendSystemMessage("[SERVER]: Игрок не найден!", player);
                  return;
               }

               this.banServices.ban(BanType.CHAT, time, victim_, player.getUser(), reason);
               this.sendSystemMessage(StringUtils.concatStrings("Танкист ", victim_.getNickname(), " лишен права выхода в эфир ", time.getNameType(), " Причина: ", reason));
            }
         } else {
            if (message.length() >= 399) {
               message = null;
               return;
            }

            if (!player.parentLobby.getChatFloodController().detected(message)) {
               player.parentLobby.timer = System.currentTimeMillis();
               this.sendMessage(new BattleChatMessage(player.getUser().getNickname(), player.getUser().getRang(), message, player.playerTeamType, team, false));
            } else {
               if (player.getUser().getWarnings() >= 5) {
                  BanTimeType time = BanTimeType.FIVE_MINUTES;
                  reason = "Флуд.";
                  this.banServices.ban(BanType.CHAT, time, player.getUser(), player.getUser(), reason);
                  this.sendSystemMessage(StringUtils.concatStrings("Танкист ", player.getUser().getNickname(), " лишен права выхода в эфир ", time.getNameType(), " Причина: ", reason));
                  return;
               }

               this.sendSystemMessage("Танкист " + player.getUser().getNickname() + "  предупрежден. Причина: Флуд.");
               player.getUser().addWarning();
            }
         }

      }
   }

   public void sendSystemMessage(String message) {
      if (message == null) {
         message = " ";
      }

      this.sendMessage(new BattleChatMessage((String)null, 0, message, "NONE", false, true));
   }

   public void sendSystemMessage(String message, BattlefieldPlayerController player) {
      if (message == null) {
         message = " ";
      }

      this.sendMessage(new BattleChatMessage((String)null, 0, message, "NONE", false, true), player);
   }

   private void sendMessage(BattleChatMessage msg) {
      this.bfModel.sendToAllPlayers(Type.BATTLE, "chat", JSONUtils.parseBattleChatMessage(msg));
   }

   private void sendMessage(BattleChatMessage msg, BattlefieldPlayerController controller) {
      controller.send(Type.BATTLE, "chat", JSONUtils.parseBattleChatMessage(msg));
   }

   public int getInt(String src) {
      try {
         return Integer.parseInt(src);
      } catch (Exception var3) {
         return Integer.MAX_VALUE;
      }
   }
}
