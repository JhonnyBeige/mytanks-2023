package gtanks.battles.tanks.weapons.ricochet;

import gtanks.RandomUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.anticheats.AnticheatModel;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.IWeapon;
import gtanks.battles.tanks.weapons.anticheats.FireableWeaponAnticheatModel;
import gtanks.commands.Type;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@AnticheatModel(
   name = "RicochetModel",
   actionInfo = "Child FireableWeaponAnticheatModel"
)
public class RicochetModel extends FireableWeaponAnticheatModel implements IWeapon {
   private RicochetEntity entity;
   private BattlefieldModel bfModel;
   private BattlefieldPlayerController player;

   public RicochetModel(RicochetEntity entity, BattlefieldModel bfModel, BattlefieldPlayerController player) {
      super(entity.getShotData().reloadMsec);
      this.entity = entity;
      this.bfModel = bfModel;
      this.player = player;
   }

   public void fire(String json) {
      JSONObject parser = null;

      try {
         parser = (JSONObject)(new JSONParser()).parse(json);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      if (!this.check((int)(long)parser.get("reloadTime"))) {
         this.bfModel.cheatDetected(this.player, this.getClass());
      } else {
         boolean selfHit = (Boolean)parser.get("self_hit");
         if (!selfHit) {
            this.bfModel.sendToAllPlayers(this.player, Type.BATTLE, "fire_ricochet", this.player.tank.id, json);
         }

         int distance = this.getValueByObject(parser.get("distance"));
         BattlefieldPlayerController victim = selfHit ? this.player : this.bfModel.getPlayer((String)parser.get("victimId"));
         if (victim != null) {
            this.onTarget(new BattlefieldPlayerController[]{victim}, distance);
         }
      }
   }

   public void startFire(String json) {
      this.bfModel.sendToAllPlayers(this.player, Type.BATTLE, "start_fire", this.player.tank.id, json);
   }

   public void onTarget(BattlefieldPlayerController[] targetsTanks, int distance) {
      BattlefieldPlayerController victim = targetsTanks[0];
      float damage = RandomUtils.getRandom(this.entity.damage_min, this.entity.damage_min);
      this.bfModel.tanksKillModel.damageTank(victim, this.player, damage, true);
   }

   public void HealFlame() {
   }

   public IEntity getEntity() {
      return this.entity;
   }

   private int getValueByObject(Object obj) {
      if (obj == null) {
         return 0;
      } else {
         try {
            return (int)Double.parseDouble(String.valueOf(obj));
         } catch (Exception var3) {
            return Integer.parseInt(String.valueOf(obj));
         }
      }
   }

   public void stopFire() {
   }
}
