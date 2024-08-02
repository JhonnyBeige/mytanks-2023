package gtanks.battles.tanks.weapons.frezee;

import gtanks.RandomUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.IWeapon;
import gtanks.battles.tanks.weapons.anticheats.TickableWeaponAnticheatModel;
import gtanks.battles.tanks.weapons.frezee.effects.FrezeeEffectModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FrezeeModel extends TickableWeaponAnticheatModel implements IWeapon {
   private FrezeeEntity entity;
   private BattlefieldModel bfModel;
   private BattlefieldPlayerController player;

   public FrezeeModel(FrezeeEntity entity, BattlefieldModel bfModel, BattlefieldPlayerController player) {
      super(entity.weaponTickMsec);
      this.entity = entity;
      this.bfModel = bfModel;
      this.player = player;
   }

   public void fire(String json) {
      JSONArray victims = null;
      JSONArray distances = null;

      try {
         JSONObject obj = (JSONObject)(new JSONParser()).parse(json);
         victims = (JSONArray)obj.get("victims");
         distances = (JSONArray)obj.get("targetDistances");
         if (!this.check((int)(long)obj.get("tickPeriod"))) {
            this.bfModel.cheatDetected(this.player, this.getClass());
            return;
         }
      } catch (ParseException var8) {
         var8.printStackTrace();
      }

      for(int i = 0; i < victims.size(); ++i) {
         String victimId = (String)victims.get(i);
         int distance = this.getValueByObject(distances.get(i));
         BattlefieldPlayerController victim = this.bfModel.getPlayer(victimId);
         if (victim != null && !((float)((int)(victim.tank.position.distanceTo(this.player.tank.position) / 100.0D)) > this.entity.damageAreaRange)) {
            this.onTarget(new BattlefieldPlayerController[]{this.bfModel.getPlayer(victimId)}, distance);
         }
      }

   }

   public void HealFlame() {
   }

   public void startFire(String json) {
      this.bfModel.startFire(this.player);
   }

   public void stopFire() {
      this.bfModel.stopFire(this.player);
   }

   public void onTarget(BattlefieldPlayerController[] targetsTanks, int distance) {
      float damage = RandomUtils.getRandom(this.entity.damage_min, this.entity.damage_min) / 2.0F;
      if (!((float)distance > this.entity.damageAreaRange)) {
         this.bfModel.tanksKillModel.damageTank(targetsTanks[0], this.player, damage, true);
         BattlefieldPlayerController victim = targetsTanks[0];
         if (victim != null && victim.tank != null) {
            boolean canFrezee = true;
            if (this.bfModel.battleInfo.team) {
               canFrezee = !this.player.playerTeamType.equals(victim.playerTeamType);
            }

            if (canFrezee) {
               if (victim.tank.frezeeEffect == null) {
                  victim.tank.frezeeEffect = new FrezeeEffectModel(this.entity.coolingSpeed, victim.tank, this.bfModel);
                  victim.tank.frezeeEffect.setStartSpecFromTank();
               }

               victim.tank.frezeeEffect.update();
            }

         }
      }
   }

   public IEntity getEntity() {
      return this.entity;
   }

   private int getValueByObject(Object obj) {
      try {
         return (int)Double.parseDouble(String.valueOf(obj));
      } catch (Exception var3) {
         return Integer.parseInt(String.valueOf(obj));
      }
   }
}
