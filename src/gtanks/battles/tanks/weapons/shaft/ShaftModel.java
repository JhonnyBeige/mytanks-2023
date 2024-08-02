package gtanks.battles.tanks.weapons.shaft;

import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.anticheats.AnticheatModel;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.IWeapon;
import gtanks.battles.tanks.weapons.WeaponWeakeningData;
import gtanks.battles.tanks.weapons.anticheats.FireableWeaponAnticheatModel;
import gtanks.logger.Logger;
import gtanks.RandomUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@AnticheatModel(
        name = "ShaftModel",
        actionInfo = "Child FireableWeaponAnticheatModel"
)
public class ShaftModel extends FireableWeaponAnticheatModel implements IWeapon {
   private BattlefieldModel bfModel;
   private BattlefieldPlayerController player;
   private ShaftEntity entity;
   private WeaponWeakeningData weakeingData;

   public ShaftModel(ShaftEntity entity, WeaponWeakeningData weakeingData, BattlefieldModel bfModel, BattlefieldPlayerController player) {
      super(entity.getShotData().reloadMsec);
      this.entity = entity;
      this.bfModel = bfModel;
      this.player = player;
      this.weakeingData = weakeingData;
   }

   public void fire(String json) {
      JSONObject jsonObj = parseJson(json);
      if (jsonObj == null) return;

      JSONArray targets = (JSONArray) jsonObj.get("targets");
      Number energy = (Number) jsonObj.get("energy");
      List<String> ids = extractIds(targets);

      this.bfModel.fire(this.player, json);
      BattlefieldPlayerController[] tanksArray = getTanksArray(ids);

      this.onTargetDamage(tanksArray, 0, energy.doubleValue());
   }

   public void quickFire(String json) {
      JSONObject jsonObj = parseJson(json);
      if (jsonObj == null) return;

      JSONArray targets = (JSONArray) jsonObj.get("targets");
      List<String> targetIds = extractIds(targets);

      this.bfModel.quickFire(this.player, json);
      BattlefieldPlayerController[] tanksArray = getTanksArray(targetIds);

      this.onTargetQuickShot(tanksArray);
   }

   public void startFire(String json) {
   }

   public void onTargetDamage(BattlefieldPlayerController[] targetsTanks, int distance, double energy) {
      if (targetsTanks.length != 0) {
         if (energy < 0.0) {
            this.bfModel.cheatDetected(this.player, this.getClass());
            return;
         }

         float damage = (float) ((float) this.entity.fov_damage_max * ((this.entity.maxEnergy - energy) / this.entity.maxEnergy));
         if (damage < 30.0f) {
            damage = this.entity.damage_min;
         }

         for (BattlefieldPlayerController target : targetsTanks) {
            this.bfModel.tanksKillModel.damageTank(target, this.player, damage, true);
            damage /= 2.0f;
         }
      }
   }

   public void onTargetQuickShot(BattlefieldPlayerController[] targetsTanks) {
      if (targetsTanks.length != 0) {
         float damage = RandomUtils.getRandom(this.entity.damage_min, this.entity.damage_max);
         for (BattlefieldPlayerController target : targetsTanks) {
            this.bfModel.tanksKillModel.damageTank(target, this.player, damage, true);
            damage /= 2.0f;
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

   public void onTarget(BattlefieldPlayerController[] targets, int distance) {
   }

   private JSONObject parseJson(String json) {
      JSONParser parser = new JSONParser();
      try {
         return (JSONObject) parser.parse(json);
      } catch (ParseException e) {
         Logger.log("Failed to parse JSON: " + e.getMessage());
         return null;
      }
   }

   private List<String> extractIds(JSONArray targets) {
      List<String> ids = new ArrayList<>();
      for (Object target : targets) {
         JSONObject targetObj = (JSONObject) ((JSONObject) target).get("target");
         ids.add(targetObj.get("id").toString());
      }
      return ids;
   }

   private BattlefieldPlayerController[] getTanksArray(List<String> ids) {
      BattlefieldPlayerController[] tanksArray = new BattlefieldPlayerController[ids.size()];
      for (int i = 0; i < ids.size(); ++i) {
         tanksArray[i] = this.bfModel.players.get(ids.get(i));
      }
      return tanksArray;
   }
}

