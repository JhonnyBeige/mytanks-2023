package gtanks.battles.tanks.weapons.vulcan;

import gtanks.RandomUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.anticheats.AnticheatModel;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.IWeapon;
import gtanks.battles.tanks.weapons.WeaponUtils;
import gtanks.battles.tanks.weapons.WeaponWeakeningData;
import gtanks.battles.tanks.weapons.anticheats.FireableWeaponAnticheatModel;
import gtanks.commands.Type;
import gtanks.logger.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@AnticheatModel(
   name = "VulcanModel",
   actionInfo = "Child FireableWeaponAnticheatModel"
)
public class VulcanModel extends FireableWeaponAnticheatModel implements IWeapon {
   private BattlefieldModel bfModel;
   private BattlefieldPlayerController player;
   private VulcanEntity entity;
   private WeaponWeakeningData weakeingData;

   public VulcanModel(VulcanEntity entity, WeaponWeakeningData weakeingData, BattlefieldModel bfModel, BattlefieldPlayerController player) {
      super(entity.getShotData().reloadMsec);
      this.entity = entity;
      this.bfModel = bfModel;
      this.player = player;
      this.weakeingData = weakeingData;
   }

   public void fire(String json) {
      JSONParser js = new JSONParser();
      JSONObject jo = null;

      try {
         jo = (JSONObject) js.parse(json);
      } catch (ParseException var5) {
         var5.printStackTrace();
      }

      if (jo != null) {
         this.bfModel.fire(this.player, json);
         BattlefieldPlayerController victim = (BattlefieldPlayerController)this.bfModel.players.get(jo.get("victimId"));

         if (victim != null) {
            double distance = Double.parseDouble(String.valueOf(jo.get("distance")));
            this.onTarget(new BattlefieldPlayerController[]{victim}, (int) distance);
         }
      }
   }

   public void HealFlame() {
      System.out.println("[VulcanModel]::HealFlame() Warning!");
      //вырезал потому что работало не правильно.
   }

   public void startFire(String json) {
      this.bfModel.sendToAllPlayers(this.player, Type.BATTLE, "start_fire", this.player.tank.id, json);
   }

   public void onTarget(BattlefieldPlayerController[] targetsTanks, int distance) {
      if (targetsTanks.length != 0) {
         if (targetsTanks.length > 1) {
            Logger.log("VulcanModel::onTarget() Warning! targetsTanks length = " + targetsTanks.length);
         }

         for (BattlefieldPlayerController target : targetsTanks) {
            if (target != null) {
               float damage = RandomUtils.getRandom(this.entity.damage_min, this.entity.damage_max);
               if ((double)distance >= this.weakeingData.minimumDamageRadius) {
                  damage = WeaponUtils.calculateDamageFromDistance(damage, (int)this.weakeingData.minimumDamagePercent);
               }

               this.bfModel.tanksKillModel.damageTank(target, this.player, damage, true);
            }
         }
      }
   }


   public IEntity getEntity() {
      return this.entity;
   }

   public void stopFire() {
      this.bfModel.stopFire(this.player);

   }
}
