package gtanks.battles.bonuses.model;

import gtanks.battles.BattlefieldModel;
import gtanks.battles.BattlefieldPlayerController;
import gtanks.battles.bonuses.Bonus;
import gtanks.battles.bonuses.BonusType;
import gtanks.battles.effects.Effect;
import gtanks.battles.effects.EffectType;
import gtanks.battles.effects.impl.ArmorEffect;
import gtanks.battles.effects.impl.DamageEffect;
import gtanks.battles.effects.impl.HealthEffect;
import gtanks.battles.effects.impl.NitroEffect;
import gtanks.battles.tanks.math.Vector3;
import gtanks.commands.Type;
import gtanks.main.database.DatabaseManager;
import gtanks.main.database.impl.DatabaseManagerImpl;
import gtanks.services.annotations.ServicesInject;

public class BonusTakeModel {
   private static final String SET_CRY = "set_cry";
   private static final String ENABLE_EFFECT_COMAND = "enable_effect";
   private static final int CRYSTALL_BONUS_COST = 1;
   private static final int GOLD_BONUS_COST = 100;
   private BattlefieldModel bfModel;
   @ServicesInject(
      target = DatabaseManagerImpl.class
   )
   private DatabaseManager database = DatabaseManagerImpl.instance();
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$gtanks$battles$bonuses$BonusType;

   public BonusTakeModel(BattlefieldModel bfModel) {
      this.bfModel = bfModel;
   }

   public boolean onTakeBonus(Bonus bonus, Vector3 realtimePosTank, BattlefieldPlayerController player) {
      switch($SWITCH_TABLE$gtanks$battles$bonuses$BonusType()[bonus.type.ordinal()]) {
      case 1:
         this.bfModel.sendUserLogMessage(player.parentLobby.getLocalUser().getNickname(), "взял золотой ящик");
         player.parentLobby.getLocalUser().addCrystall(100);
         player.send(Type.BATTLE, "set_cry", String.valueOf(player.parentLobby.getLocalUser().getCrystall()));
         this.database.update(player.getUser());
         break;
      case 2:
         player.parentLobby.getLocalUser().addCrystall(1);
         player.send(Type.BATTLE, "set_cry", String.valueOf(player.parentLobby.getLocalUser().getCrystall()));
         this.database.update(player.getUser());
         break;
      case 3:
         this.activateDrop(new ArmorEffect(), player);
         break;
      case 4:
         this.activateDrop(new HealthEffect(), player);
         break;
      case 5:
         this.activateDrop(new DamageEffect(), player);
         break;
      case 6:
         this.activateDrop(new NitroEffect(), player);
      }

      return true;
   }

   private void activateDrop(Effect effect, BattlefieldPlayerController player) {
      if (!player.tank.isUsedEffect(effect.getEffectType())) {
         effect.activate(player, false, player.tank.position);
         player.battle.sendToAllPlayers(Type.BATTLE, "enable_effect", player.getUser().getNickname(), String.valueOf(effect.getID()), effect.getEffectType() == EffectType.HEALTH ? String.valueOf(10000) : String.valueOf(40000));
      }

   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$gtanks$battles$bonuses$BonusType() {
      int[] var10000 = $SWITCH_TABLE$gtanks$battles$bonuses$BonusType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[BonusType.values().length];

         try {
            var0[BonusType.ARMOR.ordinal()] = 3;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[BonusType.CRYSTALL.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[BonusType.DAMAGE.ordinal()] = 5;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[BonusType.GOLD.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[BonusType.HEALTH.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[BonusType.NITRO.ordinal()] = 6;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$gtanks$battles$bonuses$BonusType = var0;
         return var0;
      }
   }
}
