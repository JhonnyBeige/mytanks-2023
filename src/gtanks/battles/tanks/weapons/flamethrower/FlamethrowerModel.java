package gtanks.battles.tanks.weapons.flamethrower;

import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.IWeapon;
import gtanks.battles.tanks.weapons.anticheats.TickableWeaponAnticheatModel;
import gtanks.battles.tanks.weapons.flamethrower.effects.FlamethrowerEffectModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FlamethrowerModel extends TickableWeaponAnticheatModel implements IWeapon {
   private FlamethrowerEntity entity;
   public BattlefieldModel bfModel;
   public BattlefieldPlayerController player;

   public FlamethrowerModel(FlamethrowerEntity entity, BattlefieldModel bfModel, BattlefieldPlayerController player) {
      super(entity.targetDetectionInterval);
      this.entity = entity;
      this.bfModel = bfModel;
      this.player = player;
   }

   public void startFire(String json) {
      this.bfModel.startFire(this.player);
   }

   public void stopFire() {
      this.bfModel.stopFire(this.player);
   }

   public void HealFlame() {
   }

   public void fire(String json) {
      try {
         JSONObject parser = (JSONObject)(new JSONParser()).parse(json);
         JSONArray arrayTanks = (JSONArray)parser.get("targetsIds");
         if (!this.check((int)(long)parser.get("tickPeriod"))) {
            this.bfModel.cheatDetected(this.player, this.getClass());
            return;
         }

         if (arrayTanks.size() == 0) {
            return;
         }

         BattlefieldPlayerController[] targetVictim = new BattlefieldPlayerController[arrayTanks.size()];

         for(int i = 0; i < arrayTanks.size(); ++i) {
            BattlefieldPlayerController target = this.bfModel.getPlayer((String)arrayTanks.get(i));
            if (target != null && (float)((int)(target.tank.position.distanceTo(this.player.tank.position) / 100.0D)) <= this.entity.range) {
               targetVictim[i] = target;
            }
         }

         this.onTarget(targetVictim, 0);
      } catch (ParseException var7) {
         var7.printStackTrace();
      }

   }

   public void onTarget(BattlefieldPlayerController[] targetsTanks, int distance) {
      this.bfModel.tanksKillModel.damageTank(targetsTanks[0], this.player, this.entity.damage_max, true);
      BattlefieldPlayerController victim = targetsTanks[0];
      if (victim != null && victim.tank != null) {
         boolean canFlame = true;
         if (this.bfModel.battleInfo.team) {
            canFlame = !this.player.playerTeamType.equals(victim.playerTeamType);
         }
         try {
            if (canFlame) {
               if (victim.tank.flameEffect == null) {
                  victim.tank.flameEffect = new FlamethrowerEffectModel(this.entity.coolingSpeed, victim.tank, this.bfModel, targetsTanks[0], this.player, this.entity.damage_max);
                  victim.tank.flameEffect.setStartSpecFromTank();
               }
               victim.tank.flameEffect.update();
            }
         }  catch (NullPointerException e) {}
      }
   }

   public IEntity getEntity() {
      return this.entity;
   }
}
