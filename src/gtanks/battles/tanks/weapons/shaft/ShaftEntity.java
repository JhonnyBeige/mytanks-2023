package gtanks.battles.tanks.weapons.shaft;

import gtanks.battles.tanks.weapons.EntityType;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.ShotData;

public class ShaftEntity implements IEntity {
   public float damage_min;
   public float damage_max;
   public float maxEnergy;
   public float chargeRate;
   public float dischargeRate;
   public float elevationAngleUp;
   public float elevationAngleDown;
   public float verticalTargetingSpeed;
   public float horizontalTargetingSpeed;
   public float initialFOV;
   public float minimumFOV;
   public float shrubsHidingRadiusMin;
   public float shrubsHidingRadiusMax;
   public float impactQuickShot;
   public float fov_damage_max;
   private ShotData shotData;
   public final EntityType type = EntityType.SHAFT;

   public ShaftEntity(float damage_min, float damage_max, float fov_damage_max, float maxEnergy, float chargeRate, float dischargeRate, float elevationAngleUp, float elevationAngleDown, float verticalTargetingSpeed, float horizontalTargetingSpeed, float initialFOV, float minimumFOV, float shrubsHidingRadiusMin, float shrubsHidingRadiusMax, float impactQuickShot, ShotData shotData) {
      this.damage_min = damage_min;
      this.damage_max = damage_max;
      this.fov_damage_max = fov_damage_max;
      this.maxEnergy = maxEnergy;
      this.chargeRate = chargeRate;
      this.dischargeRate = dischargeRate;
      this.elevationAngleUp = elevationAngleUp;
      this.elevationAngleDown = elevationAngleDown;
      this.verticalTargetingSpeed = verticalTargetingSpeed;
      this.horizontalTargetingSpeed = horizontalTargetingSpeed;
      this.initialFOV = initialFOV;
      this.minimumFOV = minimumFOV;
      this.shrubsHidingRadiusMin = shrubsHidingRadiusMin;
      this.shrubsHidingRadiusMax = shrubsHidingRadiusMax;
      this.impactQuickShot = impactQuickShot;
      this.shotData = shotData;
   }

   public ShotData getShotData() {
      return this.shotData;
   }

   public EntityType getType() {
      return this.type;
   }
}


