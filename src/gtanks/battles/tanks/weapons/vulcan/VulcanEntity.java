package gtanks.battles.tanks.weapons.vulcan;

import gtanks.battles.tanks.weapons.EntityType;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.ShotData;

public class VulcanEntity implements IEntity {
   public float energyCapacity;
   public float energyDischargeSpeed;
   public float energyRechargeSpeed;
   public float spinUpTime;
   public float weaponTickMsec;
   public float damageTickMsec;
   public float spinDownTime;
   public float weaponTurnDecelerationCoeff;
   public float recoilForce;
   public float impactForce;
   public float damage_min;
   public float damage_max;
   public float flameSpeed;
   private ShotData shotData;
   public final EntityType type;

   public VulcanEntity(float damage_min, float damage_max, float flameSpeed, ShotData shotData, float energyCapacity, float energyDischargeSpeed, float energyRechargeSpeed, float spinUpTime, float weaponTickMsec, float damageTickMsec, float spinDownTime, float weaponTurnDecelerationCoeff, float recoilForce, float impactForce) {
      this.type = EntityType.VULCAN;
      this.energyCapacity = energyCapacity;
      this.energyDischargeSpeed = energyDischargeSpeed;
      this.energyRechargeSpeed = energyRechargeSpeed;
      this.spinUpTime = spinUpTime;
      this.weaponTickMsec = weaponTickMsec;
      this.damageTickMsec = damageTickMsec;
      this.spinDownTime = spinDownTime;
      this.weaponTurnDecelerationCoeff = weaponTurnDecelerationCoeff;
      this.recoilForce = recoilForce;
      this.impactForce = impactForce;
      this.shotData = shotData;
      this.damage_min = damage_min;
      this.damage_max = damage_max;
      this.flameSpeed = flameSpeed;
   }

   public ShotData getShotData() {
      return this.shotData;
   }

   public EntityType getType() {
      return this.type;
   }
}
