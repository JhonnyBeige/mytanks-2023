package gtanks.battles.tanks.weapons.smoky;

import gtanks.RandomUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.anticheats.AnticheatModel;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.IWeapon;
import gtanks.battles.tanks.weapons.WeaponUtils;
import gtanks.battles.tanks.weapons.WeaponWeakeningData;
import gtanks.battles.tanks.weapons.anticheats.FireableWeaponAnticheatModel;
import gtanks.logger.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@AnticheatModel(
   name = "SmokyModel",
   actionInfo = "Child FireableWeaponAnticheatModel"
)
public class SmokyModel extends FireableWeaponAnticheatModel implements IWeapon {
   private BattlefieldModel bfModel;
   private BattlefieldPlayerController player;
   private SmokyEntity entity;
   private WeaponWeakeningData weakeingData;

   public SmokyModel(SmokyEntity entity, WeaponWeakeningData weakeingData, BattlefieldModel bfModel, BattlefieldPlayerController player) {
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
         jo = (JSONObject)js.parse(json);
      } catch (ParseException var5) {
         var5.printStackTrace();
      }

      if (!this.check((int)(long)jo.get("reloadTime"))) {
         this.bfModel.cheatDetected(this.player, this.getClass());
      } else {
         this.bfModel.fire(this.player, json);
         BattlefieldPlayerController victim = (BattlefieldPlayerController)this.bfModel.players.get(jo.get("victimId"));
         if (victim != null) {
            this.onTarget(new BattlefieldPlayerController[]{victim}, (int)Double.parseDouble(String.valueOf(jo.get("distance"))));
         }
      }
   }

   public void startFire(String json) {
   }

   public void onTarget(BattlefieldPlayerController[] targetsTanks, int distance) {
      if (targetsTanks.length != 0) {
         if (targetsTanks.length > 1) {
            Logger.log("SmokyModel::onTarget() Warning! targetsTanks length = " + targetsTanks.length);
         }

         float damage = RandomUtils.getRandom(this.entity.damage_min, this.entity.damage_max);
         if ((double)distance >= this.weakeingData.minimumDamageRadius) {
            damage = WeaponUtils.calculateDamageFromDistance(damage, (int)this.weakeingData.minimumDamagePercent);
         }

         this.bfModel.tanksKillModel.damageTank(targetsTanks[0], this.player, damage, true);
      }
   }

   public void HealFlame() {
   }


   public IEntity getEntity() {
      return this.entity;
   }

   public void stopFire() {
   }
}
