package gtanks.system;

import gtanks.battles.maps.MapsLoader;
import gtanks.lobby.battles.BattleInfo;
import gtanks.lobby.battles.BattlesList;

public class BattlesSystemList {
    public static BattleInfo goToBattle;

    public static void init() {
        MapConfigurationDefault();
    }

    public static void MapConfigurationDefault() {

        BattleInfo battleinfo = new BattleInfo();
        battleinfo.system = true;
        battleinfo.battleType = "DM";
        battleinfo.team = false;
        battleinfo.numKills = 999;
        battleinfo.minRank = 1;
        battleinfo.maxRank = 28;
        battleinfo.isPaid = false;
        battleinfo.isPrivate = false;
        battleinfo.friendlyFire = false;
        battleinfo.withBonus = true;
        battleinfo.name = "System Battle";
        battleinfo.map = MapsLoader.maps.get("map_sandbox");
        battleinfo.maxPeople = 99;
        battleinfo.autobalance = false;
        battleinfo.time = 99999;

        if (battleinfo != null) {
            BattlesList.tryCreateBatle(battleinfo);
        } else {
            return;
        }

        BattlesGC.cancelRemoving(battleinfo.model);
        goToBattle = BattlesList.getBattleInfoById(battleinfo.battleId);
    }
}
