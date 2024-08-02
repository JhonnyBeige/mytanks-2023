package gtanks.battles.tanks.loaders;

import gtanks.StringUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.tanks.weapons.IEntity;
import gtanks.battles.tanks.weapons.IWeapon;
import gtanks.battles.tanks.weapons.ShotData;
import gtanks.battles.tanks.weapons.WeaponWeakeningData;
import gtanks.battles.tanks.weapons.flamethrower.FlamethrowerEntity;
import gtanks.battles.tanks.weapons.flamethrower.FlamethrowerModel;
import gtanks.battles.tanks.weapons.frezee.FrezeeEntity;
import gtanks.battles.tanks.weapons.frezee.FrezeeModel;
import gtanks.battles.tanks.weapons.isida.IsidaEntity;
import gtanks.battles.tanks.weapons.isida.IsidaModel;
import gtanks.battles.tanks.weapons.railgun.RailgunEntity;
import gtanks.battles.tanks.weapons.railgun.RailgunModel;
import gtanks.battles.tanks.weapons.ricochet.RicochetEntity;
import gtanks.battles.tanks.weapons.ricochet.RicochetModel;
import gtanks.battles.tanks.weapons.shaft.ShaftEntity;
import gtanks.battles.tanks.weapons.shaft.ShaftModel;
import gtanks.battles.tanks.weapons.smoky.SmokyEntity;
import gtanks.battles.tanks.weapons.smoky.SmokyModel;
import gtanks.battles.tanks.weapons.snowman.SnowmanEntity;
import gtanks.battles.tanks.weapons.snowman.SnowmanModel;
import gtanks.battles.tanks.weapons.thunder.ThunderEntity;
import gtanks.battles.tanks.weapons.thunder.ThunderModel;
import gtanks.battles.tanks.weapons.twins.TwinsEntity;
import gtanks.battles.tanks.weapons.twins.TwinsModel;
import gtanks.battles.tanks.weapons.vulcan.VulcanEntity;
import gtanks.battles.tanks.weapons.vulcan.VulcanModel;
import gtanks.exceptions.GTanksServerException;
import gtanks.json.JSONUtils;
import gtanks.logger.Logger;
import gtanks.logger.Type;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WeaponsFactory {
   private static HashMap<String, IEntity> weapons = new HashMap();
   private static HashMap<String, WeaponWeakeningData> wwd = new HashMap();
   private static String jsonListWeapons;

   public static IWeapon getWeapon(String turretId, BattlefieldPlayerController tank, BattlefieldModel battle) {
      String turret = turretId.split("_m")[0];
      IWeapon weapon = null;
      switch(turret) {
      case "snowman":
         if (turret.equals("snowman")) {
            weapon = new SnowmanModel((SnowmanEntity)getEntity(turretId), getWwd(turretId), tank, battle);
            return (IWeapon)weapon;
         }
         break;
      case "ricochet":
         if (turret.equals("ricochet")) {
            weapon = new RicochetModel((RicochetEntity)getEntity(turretId), battle, tank);
            return (IWeapon)weapon;
         }
         break;
      case "thunder":
         if (turret.equals("thunder")) {
            weapon = new ThunderModel((ThunderEntity)getEntity(turretId), battle, tank);
            return (IWeapon)weapon;
         }
         break;
      case "frezee":
         if (turret.equals("frezee")) {
            weapon = new FrezeeModel((FrezeeEntity)getEntity(turretId), battle, tank);
            return (IWeapon)weapon;
         }
         break;
      case "isida":
         if (turret.equals("isida")) {
            weapon = new IsidaModel((IsidaEntity)getEntity(turretId), tank, battle);
            return (IWeapon)weapon;
         }
         break;
      case "shaft":
         if (turret.equals("shaft")) {
            weapon = new ShaftModel((ShaftEntity)getEntity(turretId), getWwd(turretId), battle, tank);
            return (IWeapon)weapon;
         }
         break;
      case "smoky":
         if (turret.equals("smoky")) {
            weapon = new SmokyModel((SmokyEntity)getEntity(turretId), getWwd(turretId), battle, tank);
            return (IWeapon)weapon;
         }
         break;
      case "twins":
         if (turret.equals("twins")) {
            weapon = new TwinsModel((TwinsEntity)getEntity(turretId), getWwd(turretId), tank, battle);
            return (IWeapon)weapon;
         }
         break;
      case "railgun":
         if (turret.equals("railgun")) {
            weapon = new RailgunModel((RailgunEntity)getEntity(turretId), tank, battle);
            return (IWeapon)weapon;
         }
         break;
         case "vulcan":
            if (turret.equals("vulcan")) {
               weapon = new VulcanModel((VulcanEntity)getEntity(turretId), getWwd(turretId), battle, tank);
               return (IWeapon)weapon;
            }
         break;
      case "flamethrower":
         if (turret.equals("flamethrower")) {
            weapon = new FlamethrowerModel((FlamethrowerEntity)getEntity(turretId), battle, tank);
            return (IWeapon)weapon;
         }
      }

      weapon = new RailgunModel((RailgunEntity)getEntity("railgun_m0"), tank, battle);
      return (IWeapon)weapon;
   }

   public static void init(String path2config) {
      weapons.clear();
      Logger.log("Weapons Factory inited. Loading weapons...");

      try {
         File folder = new File(path2config);
         File[] var5;
         int var4 = (var5 = folder.listFiles()).length;

         for(int var3 = 0; var3 < var4; ++var3) {
            File config = var5[var3];
            if (!config.getName().endsWith(".cfg")) {
               throw new GTanksServerException("In folder " + path2config + " find non-configuration file: " + config.getName());
            }

            Logger.log("Loading " + config.getName() + "...");
            parse(config);
         }

         jsonListWeapons = JSONUtils.parseWeapons(getEntitys(), wwd);
      } catch (Exception var6) {
         var6.printStackTrace();
         Logger.log(Type.ERROR, "Loading entitys weapons failed. " + var6.getMessage());
      }

   }

   private static void parse(File json) throws FileNotFoundException, IOException, ParseException {
      JSONParser parser = new JSONParser();
      JSONObject jobj = (JSONObject)parser.parse((Reader)(new FileReader(json)));
      String type = (String)jobj.get("type");

      String id;
      Object entity;
      for(Iterator var5 = ((JSONArray)jobj.get("params")).iterator(); var5.hasNext(); weapons.put(id, (IEntity) entity)) {
         Object item = var5.next();
         JSONObject jitem = (JSONObject)item;
         String modification = (String)jitem.get("modification");
         id = StringUtils.concatStrings(type, "_", modification);
         ShotData shotData = new ShotData(id, getDouble(jitem.get("autoAimingAngleDown")), getDouble(jitem.get("autoAimingAngleUp")), (int)(long)jitem.get("numRaysDown"), (int)(long)jitem.get("numRaysUp"), (int)(long)jitem.get("reloadMsec"), (float)(double)jitem.get("impactCoeff"), (float)(double)jitem.get("kickback"), (float)(double)jitem.get("turretRotationAccel"), (float)(double)jitem.get("turretRotationSpeed"));
         entity = null;
         switch(type) {
         case "snowman":
            if (type.equals("snowman")) {
               WeaponWeakeningData wwdSnowman = new WeaponWeakeningData((Double)jitem.get("max_damage_radius"), (Double)jitem.get("min_damage_percent"), (Double)jitem.get("min_damage_radius"));
               entity = new SnowmanEntity((float)(double)jitem.get("shot_range"), (float)(double)jitem.get("shot_speed"), (float)(double)jitem.get("shot_radius"), (float)(double)jitem.get("min_damage"), (float)(double)jitem.get("max_damage"), (float)(double)jitem.get("frezee_speed"), shotData);
               WeaponsFactory.wwd.put(id, wwdSnowman);
            }
            break;
         case "ricochet":
            if (type.equals("ricochet")) {
               WeaponWeakeningData wwdRicochet = new WeaponWeakeningData((Double)jitem.get("max_damage_radius"), (Double)jitem.get("min_damage_percent"), (Double)jitem.get("min_damage_radius"));
               entity = new RicochetEntity((float)(double)jitem.get("shotRadius"), (float)(double)jitem.get("shotSpeed"), (int)(long)jitem.get("energyCapacity"), (int)(long)jitem.get("energyPerShot"), (float)(double)jitem.get("energyRechargeSpeed"), (float)(double)jitem.get("shotDistance"), (float)(double)jitem.get("min_damage"), (float)(double)jitem.get("max_damage"), shotData);
               WeaponsFactory.wwd.put(id, wwdRicochet);
            }
            break;
         case "thunder":
            if (type.equals("thunder")) {
               WeaponWeakeningData wwdThunder = new WeaponWeakeningData((Double)jitem.get("maxSplashDamageRadius"), (Double)jitem.get("minSplashDamageRadius"), (Double)jitem.get("minSplashDamagePercent"));
               entity = new ThunderEntity((float)(double)jitem.get("maxSplashDamageRadius"), (float)(double)jitem.get("minSplashDamageRadius"), (float)(double)jitem.get("minSplashDamagePercent"), (float)(double)jitem.get("impactForce"), shotData, (float)(double)jitem.get("min_damage"), (float)(double)jitem.get("max_damage"), wwdThunder);
               WeaponsFactory.wwd.put(id, wwdThunder);
            }
            break;
         case "frezee":
            if (type.equals("frezee")) {
               entity = new FrezeeEntity((float)(double)jitem.get("damageAreaConeAngle"), (float)(double)jitem.get("damageAreaRange"), (int)(long)jitem.get("energyCapacity"), (int)(long)jitem.get("energyDischargeSpeed"), (int)(long)jitem.get("energyRechargeSpeed"), (int)(long)jitem.get("weaponTickMsec"), (float)(double)jitem.get("coolingSpeed"), (float)(double)jitem.get("min_damage"), (float)(double)jitem.get("max_damage"), shotData);
            }
            break;
         case "isida":
            if (type.equals("isida")) {
               entity = new IsidaEntity((int)(long)jitem.get("capacity"), (int)(long)jitem.get("chargeRate"), (int)(long)jitem.get("dischargeRate"), (int)(long)jitem.get("tickPeriod"), (float)(double)jitem.get("lockAngle"), (float)(double)jitem.get("lockAngleCos"), (float)(double)jitem.get("maxAngle"), (float)(double)jitem.get("maxAngleCos"), (float)(double)jitem.get("maxRadius"), shotData, (float)(double)jitem.get("min_damage"), (float)(double)jitem.get("max_damage"));
            }
            break;
         case "shaft":
            if (type.equals("shaft")) {
               WeaponWeakeningData shaftwwd = new WeaponWeakeningData((Double)jitem.get("max_damage_radius"), (Double)jitem.get("min_damage_percent"), (Double)jitem.get("min_damage_radius"));
               entity = new ShaftEntity((float)((double)jitem.get("min_damage")), (float)(double)jitem.get("max_damage"), (float)(double)jitem.get("fov_max_damage"), (float)(double)jitem.get("max_energy"), (float)(double)jitem.get("charge_rate"), (float)(double)jitem.get("discharge_rate"), (float)(double)jitem.get("elevation_angle_up"), (float)(double)jitem.get("elevation_angle_down"), (float)(double)jitem.get("vertical_targeting_speed"), (float)(double)jitem.get("horizontal_targeting_speed"), (float)(double)jitem.get("inital_fov"), (float)(double)jitem.get("minimum_fov"), (float)(double)jitem.get("shrubs_hiding_radius_min"), (float)(double)jitem.get("shrubs_hiding_radius_max"), (float)(double)jitem.get("impact_quick_shot"), shotData);
               wwd.put(id, shaftwwd);
            }
            break;
         case "smoky":
            if (type.equals("smoky")) {
               WeaponWeakeningData wwd = new WeaponWeakeningData((Double)jitem.get("max_damage_radius"), (Double)jitem.get("min_damage_percent"), (Double)jitem.get("min_damage_radius"));
               entity = new SmokyEntity(shotData, (float)(double)jitem.get("min_damage"), (float)(double)jitem.get("max_damage"));
               WeaponsFactory.wwd.put(id, wwd);
            }
            break;
         case "twins":
            if (type.equals("twins")) {
               WeaponWeakeningData wwdTwins = new WeaponWeakeningData((Double)jitem.get("max_damage_radius"), (Double)jitem.get("min_damage_percent"), (Double)jitem.get("min_damage_radius"));
               entity = new TwinsEntity((float)(double)jitem.get("shot_range"), (float)(double)jitem.get("shot_speed"), (float)(double)jitem.get("shot_radius"), (float)(double)jitem.get("min_damage"), (float)(double)jitem.get("max_damage"), shotData);
               WeaponsFactory.wwd.put(id, wwdTwins);
            }
            break;
         case "railgun":
            if (type.equals("railgun")) {
               entity = new RailgunEntity(shotData, (int)(long)jitem.get("charingTime"), (int)(long)jitem.get("weakeningCoeff"), (float)(double)jitem.get("min_damage"), (float)(double)jitem.get("max_damage"));
            }
            break;
            case "vulcan":
               if (type.equals("vulcan")) {
                  WeaponWeakeningData wwd = new WeaponWeakeningData((Double)jitem.get("max_damage_radius"), (Double)jitem.get("min_damage_percent"), (Double)jitem.get("min_damage_radius"));
                  entity = new VulcanEntity((float)(double)jitem.get("min_damage"), (float)(double)jitem.get("max_damage"), (float)(double)jitem.get("flame_speed"), shotData, (float)(long)jitem.get("energyCapacity"), (float)(long)jitem.get("energyDischargeSpeed"), (float)(long)jitem.get("energyRechargeSpeed"), (float)(double)jitem.get("spinUpTime"), (float)(double)jitem.get("weaponTickMsec"), (float)(double)jitem.get("damageTickMsec"), (float)(double)jitem.get("spinDownTime"), (float)(double)jitem.get("weaponTurnDecelerationCoeff"), (float)(double)jitem.get("recoilForce"), (float)(double)jitem.get("impactForce"));
                  WeaponsFactory.wwd.put(id, wwd);
               }
               break;
         case "flamethrower":
            if (type.equals("flamethrower")) {
               entity = new FlamethrowerEntity((int)(long)jitem.get("target_detection_interval"), (float)(double)jitem.get("range"), (float)(double)jitem.get("cone_angle"), (int)(long)jitem.get("heating_speed"), (int)(long)jitem.get("cooling_speed"), (int)(long)jitem.get("heat_limit"), shotData, (float)(double)jitem.get("max_damage"), (float)(double)jitem.get("min_damage"));
            }
         }
      }

   }

   public static WeaponWeakeningData getWwd(String id) {
      return (WeaponWeakeningData)wwd.get(id);
   }

   public static IEntity getEntity(String id) {
      return (IEntity)weapons.get(id);
   }

   public static String getId(IEntity entity) {
      String id = null;
      Iterator var3 = weapons.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<String, IEntity> entry = (Entry)var3.next();
         if (((IEntity)entry.getValue()).equals(entity)) {
            id = (String)entry.getKey();
         }
      }

      return id;
   }

   private static double getDouble(Object obj) {
      try {
         return (Double)obj;
      } catch (Exception var2) {
         return (double)(Long)obj;
      }
   }

   public static Collection<IEntity> getEntitys() {
      return weapons.values();
   }

   public static String getJSONList() {
      return jsonListWeapons;
   }
}
