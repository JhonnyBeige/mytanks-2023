package gtanks.services;

import gtanks.collections.FastHashMap;
import gtanks.commands.Type;
import gtanks.lobby.LobbyManager;
import gtanks.users.User;
import gtanks.users.locations.UserLocation;
import java.util.Iterator;

public class LobbysServices {
   private static LobbysServices instance = new LobbysServices();
   public FastHashMap<String, LobbyManager> lobbys = new FastHashMap();

   public static LobbysServices getInstance() {
      return instance;
   }

   private LobbysServices() {
   }

   public void addLobby(LobbyManager lobby) {
      this.lobbys.put(lobby.getLocalUser().getNickname(), lobby);
   }

   public void removeLobby(LobbyManager lobby) {
      this.lobbys.remove(lobby.getLocalUser(), lobby);
   }

   public boolean containsLobby(LobbyManager lobby) {
      return this.lobbys.containsKey(lobby.getLocalUser());
   }

   public LobbyManager getLobbyByUser(User user) {
      return (LobbyManager)this.lobbys.get(user.getNickname());
   }

   public LobbyManager getLobbyByNick(String nick) {
      LobbyManager lobbyManager = null;
      Iterator var4 = this.lobbys.iterator();

      while(var4.hasNext()) {
         LobbyManager lobby = (LobbyManager)var4.next();
         if (lobby.getLocalUser().getNickname().equals(nick)) {
            lobbyManager = lobby;
            break;
         }
      }

      return lobbyManager;
   }

   public void sendCommandToAllUsers(Type type, UserLocation onlyFor, String... args) {
      try {
         Iterator var5 = this.lobbys.values().iterator();

         while(true) {
            LobbyManager lobby;
            do {
               do {
                  if (!var5.hasNext()) {
                     return;
                  }

                  lobby = (LobbyManager)var5.next();
               } while(lobby == null);
            } while(onlyFor != UserLocation.ALL && lobby.getLocalUser().getUserLocation() != onlyFor);

            lobby.send(type, args);
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   public void sendCommandToAllUsersBesides(Type type, UserLocation besides, String... args) {
      try {
         Iterator var5 = this.lobbys.values().iterator();

         while(var5.hasNext()) {
            LobbyManager lobby = (LobbyManager)var5.next();
            if (lobby != null && lobby.getLocalUser().getUserLocation() != besides) {
               lobby.send(type, args);
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }
}
