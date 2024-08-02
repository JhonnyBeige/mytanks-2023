package gtanks.lobby.chat;

import gtanks.lobby.LobbyManager;
import gtanks.users.TypeUser;
import gtanks.users.User;

public class ChatMessage {
   public User user;
   public String message;
   public boolean addressed;
   public boolean system;
   public User userTo;
   public LobbyManager localLobby;
   public boolean yellowMessage;
   public int chatPerm;
   public boolean hasPremium;

   public ChatMessage(User user, String message, boolean addressed, User userTo, boolean yellowMessage, LobbyManager localLobby) {
      this.user = user;
      this.message = message;
      this.addressed = addressed;
      this.userTo = userTo;
      this.yellowMessage = yellowMessage;
      this.localLobby = localLobby;
      this.chatPerm = determineChatPerm(user);
      this.hasPremium = premium(user);
   }

   public ChatMessage(User user, String message, boolean addressed, User userTo, LobbyManager localLobby) {
      this.user = user;
      this.message = message;
      this.addressed = addressed;
      this.userTo = userTo;
      this.localLobby = localLobby;
      this.chatPerm = determineChatPerm(user);
      this.hasPremium = premium(user);
   }

   private int determineChatPerm(User user) {
      TypeUser userType = user.getType();
      if (userType == TypeUser.ADMIN) {
         return 1;
      } else if (userType == TypeUser.MODERATOR) {
         return 2;
      } else if (userType == TypeUser.DEFAULT) {
         return 3;
      } else if (userType == TypeUser.TESTER) {
         return 4;
      }
      return 0;
   }

   private boolean premium(User user) {
      return user.getGarage().containsItem("premium_account");
   }

   public String toString() {
      return (this.system ? "SYSTEM: " : this.user.getNickname() + ": ") + (this.addressed ? "->" + this.userTo.getNickname() : "") + this.message;
   }
}