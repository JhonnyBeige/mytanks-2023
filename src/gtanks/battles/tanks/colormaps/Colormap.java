package gtanks.battles.tanks.colormaps;

import gtanks.battles.tanks.weapons.EntityType;
import java.util.HashMap;

public class Colormap {
   private HashMap<ColormapResistanceType, Integer> resistances = new HashMap();
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$gtanks$battles$tanks$weapons$EntityType;

   public void addResistance(ColormapResistanceType type, int percent) {
      this.resistances.put(type, percent);
   }

   public Integer getResistance(EntityType weaponType) {
      return (Integer)this.resistances.get(this.getResistanceTypeByWeapon(weaponType));
   }

   private ColormapResistanceType getResistanceTypeByWeapon(EntityType weaponType) {
      ColormapResistanceType type = null;
      switch($SWITCH_TABLE$gtanks$battles$tanks$weapons$EntityType()[weaponType.ordinal()]) {
      case 1:
         type = ColormapResistanceType.SMOKY;
         break;
      case 2:
         type = ColormapResistanceType.FLAMETHROWER;
         break;
      case 3:
         type = ColormapResistanceType.TWINS;
         break;
      case 4:
         type = ColormapResistanceType.RAILGUN;
         break;
      case 5:
         type = ColormapResistanceType.ISIDA;
         break;
      case 6:
         type = ColormapResistanceType.THUNDER;
         break;
      case 7:
         type = ColormapResistanceType.FREZEE;
         break;
      case 8:
         type = ColormapResistanceType.RICOCHET;
      }

      return type;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$gtanks$battles$tanks$weapons$EntityType() {
      int[] var10000 = $SWITCH_TABLE$gtanks$battles$tanks$weapons$EntityType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[EntityType.values().length];

         try {
            var0[EntityType.FLAMETHROWER.ordinal()] = 2;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[EntityType.FREZZE.ordinal()] = 7;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[EntityType.ISIDA.ordinal()] = 5;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[EntityType.RAILGUN.ordinal()] = 4;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[EntityType.RICOCHET.ordinal()] = 8;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[EntityType.SHAFT.ordinal()] = 9;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[EntityType.SMOKY.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[EntityType.SNOWMAN.ordinal()] = 10;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[EntityType.THUNDER.ordinal()] = 6;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[EntityType.TWINS.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$gtanks$battles$tanks$weapons$EntityType = var0;
         return var0;
      }
   }
}
