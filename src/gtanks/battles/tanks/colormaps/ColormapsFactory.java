package gtanks.battles.tanks.colormaps;

import gtanks.users.garage.enums.PropertyType;
import java.util.HashMap;
import java.util.Map;

public class ColormapsFactory {
   private static Map<String, Colormap> colormaps = new HashMap();
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$gtanks$users$garage$enums$PropertyType;

   public static void addColormap(String id, Colormap colormap) {
      colormaps.put(id, colormap);
   }

   public static Colormap getColormap(String id) {
      return (Colormap)colormaps.get(id);
   }

   public static ColormapResistanceType getResistanceType(PropertyType pType) {
      ColormapResistanceType type = null;
      switch($SWITCH_TABLE$gtanks$users$garage$enums$PropertyType()[pType.ordinal()]) {
      case 9:
         type = ColormapResistanceType.SMOKY;
         break;
      case 10:
         type = ColormapResistanceType.TWINS;
         break;
      case 11:
         type = ColormapResistanceType.RAILGUN;
         break;
      case 12:
         type = ColormapResistanceType.ISIDA;
      case 13:
      case 14:
      default:
         break;
      case 15:
         type = ColormapResistanceType.FLAMETHROWER;
         break;
      case 16:
         type = ColormapResistanceType.THUNDER;
         break;
      case 17:
         type = ColormapResistanceType.FREZEE;
         break;
      case 18:
         type = ColormapResistanceType.RICOCHET;
      }

      return type;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$gtanks$users$garage$enums$PropertyType() {
      int[] var10000 = $SWITCH_TABLE$gtanks$users$garage$enums$PropertyType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[PropertyType.values().length];

         try {
            var0[PropertyType.AIMING_ERROR.ordinal()] = 3;
         } catch (NoSuchFieldError var23) {
         }

         try {
            var0[PropertyType.ARMOR.ordinal()] = 13;
         } catch (NoSuchFieldError var22) {
         }

         try {
            var0[PropertyType.CONE_ANGLE.ordinal()] = 4;
         } catch (NoSuchFieldError var21) {
         }

         try {
            var0[PropertyType.DAMAGE.ordinal()] = 1;
         } catch (NoSuchFieldError var20) {
         }

         try {
            var0[PropertyType.DAMAGE_PER_SECOND.ordinal()] = 2;
         } catch (NoSuchFieldError var19) {
         }

         try {
            var0[PropertyType.FIRE_RESISTANCE.ordinal()] = 15;
         } catch (NoSuchFieldError var18) {
         }

         try {
            var0[PropertyType.FREEZE_RESISTANCE.ordinal()] = 17;
         } catch (NoSuchFieldError var17) {
         }

         try {
            var0[PropertyType.HEALING_RADUIS.ordinal()] = 19;
         } catch (NoSuchFieldError var16) {
         }

         try {
            var0[PropertyType.HEAL_RATE.ordinal()] = 20;
         } catch (NoSuchFieldError var15) {
         }

         try {
            var0[PropertyType.MECH_RESISTANCE.ordinal()] = 9;
         } catch (NoSuchFieldError var14) {
         }

         try {
            var0[PropertyType.PLASMA_RESISTANCE.ordinal()] = 10;
         } catch (NoSuchFieldError var13) {
         }

         try {
            var0[PropertyType.RAIL_RESISTANCE.ordinal()] = 11;
         } catch (NoSuchFieldError var12) {
         }

         try {
            var0[PropertyType.RICOCHET_RESISTANCE.ordinal()] = 18;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[PropertyType.SHOT_AREA.ordinal()] = 5;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[PropertyType.SHOT_FREQUENCY.ordinal()] = 6;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[PropertyType.SHOT_RANGE.ordinal()] = 7;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[PropertyType.SPEED.ordinal()] = 22;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[PropertyType.THUNDER_RESISTANCE.ordinal()] = 16;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[PropertyType.TURN_SPEED.ordinal()] = 8;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[PropertyType.TURRET_TURN_SPEED.ordinal()] = 14;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[PropertyType.UNKNOWN.ordinal()] = 23;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[PropertyType.VAMPIRE_RATE.ordinal()] = 21;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[PropertyType.VAMPIRE_RESISTANCE.ordinal()] = 12;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$gtanks$users$garage$enums$PropertyType = var0;
         return var0;
      }
   }
}
