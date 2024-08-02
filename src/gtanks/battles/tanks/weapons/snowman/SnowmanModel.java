package gtanks.battles.tanks.weapons.snowman;

import gtanks.RandomUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.anticheats.AnticheatModel;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.IWeapon;
import gtanks.battles.tanks.weapons.WeaponUtils;
import gtanks.battles.tanks.weapons.WeaponWeakeningData;
import gtanks.battles.tanks.weapons.anticheats.FireableWeaponAnticheatModel;
import gtanks.battles.tanks.weapons.frezee.effects.FrezeeEffectModel;
import gtanks.commands.Type;
import gtanks.logger.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@AnticheatModel(
   name = "SnowmanModel",
   actionInfo = "Child FireableWeaponAnticheatModel"
)
public class SnowmanModel extends FireableWeaponAnticheatModel implements IWeapon {
   private BattlefieldModel bfModel;
   private BattlefieldPlayerController player;
   private WeaponWeakeningData weakeingData;
   private SnowmanEntity entity;

   public SnowmanModel(SnowmanEntity snowmanEntity, WeaponWeakeningData weakeingData, BattlefieldPlayerController tank, BattlefieldModel battle) {
      super(snowmanEntity.getShotData().reloadMsec);
      this.bfModel = battle;
      this.player = tank;
      this.entity = snowmanEntity;
      this.weakeingData = weakeingData;
   }

   public void startFire(String json) {
      this.bfModel.sendToAllPlayers(this.player, Type.BATTLE, "start_fire_snowman", this.player.tank.id, json);
   }

   public void fire(String json) {
      this.bfModel.fire(this.player, json);

      try {
         JSONObject parser = (JSONObject)(new JSONParser()).parse(json);
         if (!this.check((int)(long)parser.get("reloadTime"))) {
            this.bfModel.cheatDetected(this.player, this.getClass());
            return;
         }

         BattlefieldPlayerController victim = this.bfModel.getPlayer((String)parser.get("victimId"));
         this.onTarget(new BattlefieldPlayerController[]{victim}, (int)(long)parser.get("distance"));
      } catch (Exception var4) {
         Logger.log("Error in parsing json SnowmanModel().fire() Data: " + json);
      }

   }

   public void onTarget(BattlefieldPlayerController[] targetsTanks, int distance) {
      float damage = RandomUtils.getRandom(this.entity.damage_min, this.entity.damage_max);
      if (targetsTanks.length != 0) {
         if (targetsTanks[0] != null) {
            if ((double)distance >= this.weakeingData.minimumDamageRadius) {
               damage = WeaponUtils.calculateDamageFromDistance(damage, (int)this.weakeingData.minimumDamagePercent);
            }

            if (targetsTanks[0].tank.frezeeEffect == null) {
               targetsTanks[0].tank.frezeeEffect = new FrezeeEffectModel(this.entity.frezeeSpeed, targetsTanks[0].tank, this.bfModel);
               targetsTanks[0].tank.frezeeEffect.setStartSpecFromTank();
            }

            targetsTanks[0].tank.frezeeEffect.update();
            this.bfModel.tanksKillModel.damageTank(targetsTanks[0], this.player, damage, true);
         }
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
