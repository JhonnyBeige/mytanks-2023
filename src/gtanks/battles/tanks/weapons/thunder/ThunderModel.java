package gtanks.battles.tanks.weapons.thunder;

import gtanks.RandomUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.anticheats.AnticheatModel;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.IWeapon;
import gtanks.battles.tanks.weapons.anticheats.FireableWeaponAnticheatModel;
import gtanks.commands.Type;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@AnticheatModel(
   name = "ThunderModel",
   actionInfo = "Child FireableWeaponAnticheatModel"
)
public class ThunderModel extends FireableWeaponAnticheatModel implements IWeapon {
   private ThunderEntity entity;
   private BattlefieldModel bfModel;
   private BattlefieldPlayerController player;

   public ThunderModel(ThunderEntity entity, BattlefieldModel bfModel, BattlefieldPlayerController player) {
      super(entity.getShotData().reloadMsec);
      this.entity = entity;
      this.bfModel = bfModel;
      this.player = player;
   }

   public void fire(String json) {
      JSONObject parser = null;

      try {
         parser = (JSONObject)(new JSONParser()).parse(json);
      } catch (ParseException var10) {
         var10.printStackTrace();
      }

      if (!this.check((int)(long)parser.get("reloadTime"))) {
         this.bfModel.cheatDetected(this.player, this.getClass());
      } else {
         this.bfModel.sendToAllPlayers(this.player, Type.BATTLE, "fire", this.player.tank.id, json);
         String mainTargetId = (String)parser.get("mainTargetId");
         if (mainTargetId != null) {
            this.onTarget(new BattlefieldPlayerController[]{this.bfModel.getPlayer(mainTargetId)}, (int)(long)parser.get("distance"));
         }

         JSONArray splashVictims = (JSONArray)parser.get("splashTargetIds");
         JSONArray splashVictimsDistances = (JSONArray)parser.get("splashTargetDistances");
         if (splashVictims != null && splashVictims.size() > 0) {
            for(int i = 0; i < splashVictims.size(); ++i) {
               String victimid = (String)splashVictims.get(i);
               float distance = (float)(double)splashVictimsDistances.get(i);
               float damage = RandomUtils.getRandom(this.entity.damage_min, this.entity.damage_min);
               if (distance >= this.entity.minSplashDamageRadius && damage <= this.entity.maxSplashDamageRadius) {
                  damage -= damage / 100.0F * 25.0F;
               }

               if (distance <= this.entity.maxSplashDamageRadius) {
                  this.bfModel.tanksKillModel.damageTank(this.bfModel.getPlayer(victimid), this.player, damage, true);
               }
            }

         }
      }
   }

   public void HealFlame() {
   }


   public void onTarget(BattlefieldPlayerController[] targetsTanks, int distance) {
      float damage = RandomUtils.getRandom(this.entity.damage_min, this.entity.damage_max);
      this.bfModel.tanksKillModel.damageTank(targetsTanks[0], this.player, damage, true);
   }

   public IEntity getEntity() {
      return this.entity;
   }

   public void startFire(String json) {
   }

   public void stopFire() {
   }
}
