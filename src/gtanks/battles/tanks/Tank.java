package gtanks.battles.tanks;

import gtanks.StringUtils;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.effects.Effect;
import gtanks.battles.effects.EffectType;
import gtanks.battles.tanks.colormaps.Colormap;
import gtanks.battles.tanks.data.DamageTankData;
import gtanks.battles.tanks.hulls.Hull;
import gtanks.battles.tanks.math.Vector3;
import gtanks.battles.tanks.weapons.IWeapon;
import gtanks.battles.tanks.weapons.flamethrower.effects.FlamethrowerEffectModel;
import gtanks.battles.tanks.weapons.frezee.effects.FrezeeEffectModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Tank {
   public static final int MAX_HEALTH_TANK = 10000;
   public Vector3 position;
   public Vector3 orientation;
   public Vector3 linVel;
   public Vector3 angVel;
   public double turretDir;
   public int controllBits;
   private IWeapon weapon;
   private Hull hull;
   private Colormap colormap;
   public String id;
   public float speed;
   public float turnSpeed;
   public float turretRotationSpeed;
   public int health = 10000;
   public int incrationId;
   public String state = "active";
   public FrezeeEffectModel frezeeEffect;
   public FlamethrowerEffectModel flameEffect;
   public ArrayList<Effect> activeEffects;
   public LinkedHashMap<BattlefieldPlayerController, DamageTankData> lastDamagers;

   public Tank(Vector3 position) {
      this.position = position;
      this.activeEffects = new ArrayList();
      this.lastDamagers = new LinkedHashMap();
   }

   public IWeapon getWeapon() {
      return this.weapon;
   }

   public Hull getHull() {
      return this.hull;
   }

   public void setWeapon(IWeapon weapon) {
      this.weapon = weapon;
      this.turretRotationSpeed = weapon.getEntity().getShotData().turretRotationSpeed;
   }

   public void setHull(Hull hull) {
      this.hull = hull;
      this.speed = hull.speed;
      this.turnSpeed = hull.turnSpeed;
   }

   public String dump() {
      return StringUtils.concatStrings("-------TANK DUMP-------\n\t\ttank id: ", this.id, "\n\t\thealth: ", String.valueOf(this.health), "\n\t\tweapon: ", String.valueOf(this.weapon), "\n\t\thull: ", String.valueOf(this.hull), "\n\t\tstate: ", this.state);
   }

   public Colormap getColormap() {
      return this.colormap;
   }

   public void setColormap(Colormap colormap) {
      this.colormap = colormap;
   }

   public boolean isUsedEffect(EffectType type) {
      Iterator var3 = this.activeEffects.iterator();

      while(var3.hasNext()) {
         Effect effect = (Effect)var3.next();
         if (effect.getEffectType() == type) {
            return true;
         }
      }

      return false;
   }
}
