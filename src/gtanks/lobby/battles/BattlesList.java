package gtanks.lobby.battles;

import gtanks.StringUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.commands.Type;
import gtanks.json.JSONUtils;
import gtanks.services.LobbysServices;
import gtanks.services.annotations.ServicesInject;
import gtanks.users.locations.UserLocation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class BattlesList extends BattlesListComandsConst {
   private static ArrayList<BattleInfo> battles = new ArrayList();
   private static int countBattles = 0;
   @ServicesInject(
      target = LobbysServices.class
   )
   private static LobbysServices lobbysServices = LobbysServices.getInstance();

   public static boolean tryCreateBatle(BattleInfo btl) {
      btl.battleId = generateId(btl.name, btl.map.id);
      if (getBattleInfoById(btl.battleId) != null) {
         return false;
      } else {
         battles.add(btl);
         ++countBattles;
         lobbysServices.sendCommandToAllUsers(Type.LOBBY, UserLocation.BATTLESELECT, "create_battle", JSONUtils.parseBattleInfo(btl));
         BattlefieldModel model = new BattlefieldModel(btl);
         btl.model = model;
         return true;
      }
   }

   public static void removeBattle(BattleInfo battle) {
      if (battle != null) {
         lobbysServices.sendCommandToAllUsers(Type.LOBBY, UserLocation.BATTLESELECT, StringUtils.concatStrings("remove_battle", ";", battle.battleId));
         if (battle.model != null && battle.model.players != null) {
            Iterator var2 = battle.model.players.values().iterator();

            while(var2.hasNext()) {
               BattlefieldPlayerController player = (BattlefieldPlayerController)var2.next();
               player.parentLobby.kick();
            }
         }

         battle.model.destroy();
         battles.remove(battle);
      }
   }

   public static ArrayList<BattleInfo> getList() {
      return battles;
   }

   private static String generateId(String gameName, String mapId) {
      String id = (new Random()).nextInt(50000) + "@" + gameName + "@" + "#" + countBattles;
      return id;
   }

   public static BattleInfo getBattleInfoById(String id) {
      Iterator var2 = battles.iterator();

      while(var2.hasNext()) {
         BattleInfo battle = (BattleInfo)var2.next();
         if (battle.battleId.equals(id)) {
            return battle;
         }
      }

      return null;
   }
}
