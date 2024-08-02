package gtanks.battles.bonuses;

import gtanks.RandomUtils;
import gtanks.battles.BattlefieldModel;
import gtanks.battles.tanks.math.Vector3;
import gtanks.logger.Logger;
import gtanks.logger.Type;
import java.util.Random;

public class BonusesSpawnService implements Runnable {
   private static final int DISAPPEARING_TIME_DROP = 30;
   private static final int DISAPPEARING_TIME_MONEY = 300;
   public BattlefieldModel battlefieldModel;
   private Random random = new Random();
   private int inc = 0;
   private int prevFund = 0;
   private int crystallFund;
   private int goldFund;
   private int nextGoldFund;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$gtanks$battles$bonuses$BonusType;

   public BonusesSpawnService(BattlefieldModel model) {
      this.battlefieldModel = model;
      this.nextGoldFund = (int)RandomUtils.getRandom(700.0F, 730.0F);
   }

   public void spawnRandomDrop() {
      int id = this.random.nextInt(4);
      BonusType bonusType = null;
      switch(id) {
      case 0:
         bonusType = BonusType.ARMOR;
         break;
      case 1:
         bonusType = BonusType.HEALTH;
         break;
      case 2:
         bonusType = BonusType.DAMAGE;
         break;
      case 3:
         bonusType = BonusType.NITRO;
      }

      int count = this.random.nextInt(4);

      for(int i = 0; i < count; ++i) {
         this.spawnBonus(bonusType);
      }

   }

   public void spawnRandomBonus() {
      boolean wasSpawned = this.random.nextBoolean();
      if (wasSpawned && this.battlefieldModel.players.size() > 0) {
         int id = this.random.nextInt(5);
         BonusType bonusType = null;
         switch(id) {
         case 0:
            bonusType = BonusType.NITRO;
            break;
         case 1:
            bonusType = BonusType.ARMOR;
            break;
         case 2:
            bonusType = BonusType.HEALTH;
            break;
         case 3:
            bonusType = BonusType.DAMAGE;
            break;
         case 4:
            bonusType = BonusType.NITRO;
         }

         int count = this.random.nextInt(4);

         for(int i = 0; i < count; ++i) {
            this.spawnBonus(bonusType);
         }
      }

   }

   public void spawnBonus(BonusType type) {
      BonusRegion region = null;
      Bonus bonus = null;
      int index;
      switch($SWITCH_TABLE$gtanks$battles$bonuses$BonusType()[type.ordinal()]) {
      case 1:
         if (this.battlefieldModel.battleInfo.map.goldsRegions.size() > 0) {
            index = this.random.nextInt(this.battlefieldModel.battleInfo.map.goldsRegions.size());
            region = (BonusRegion)this.battlefieldModel.battleInfo.map.goldsRegions.get(index);
            bonus = new Bonus(this.getRandomSpawnPostiton(region), BonusType.GOLD);
            this.battlefieldModel.spawnBonus(bonus, this.inc, 300);
         }
         break;
      case 2:
         if (this.battlefieldModel.battleInfo.map.crystallsRegions.size() > 0) {
            index = this.random.nextInt(this.battlefieldModel.battleInfo.map.crystallsRegions.size());
            region = (BonusRegion)this.battlefieldModel.battleInfo.map.crystallsRegions.get(index);
            bonus = new Bonus(this.getRandomSpawnPostiton(region), BonusType.CRYSTALL);
            this.battlefieldModel.spawnBonus(bonus, this.inc, 300);
         }
         break;
      case 3:
         if (this.battlefieldModel.battleInfo.map.armorsRegions.size() > 0) {
            index = this.random.nextInt(this.battlefieldModel.battleInfo.map.armorsRegions.size());
            region = (BonusRegion)this.battlefieldModel.battleInfo.map.armorsRegions.get(index);
            bonus = new Bonus(this.getRandomSpawnPostiton(region), BonusType.ARMOR);
            this.battlefieldModel.spawnBonus(bonus, this.inc, 30);
         }
         break;
      case 4:
         if (this.battlefieldModel.battleInfo.map.healthsRegions.size() > 0) {
            index = this.random.nextInt(this.battlefieldModel.battleInfo.map.healthsRegions.size());
            region = (BonusRegion)this.battlefieldModel.battleInfo.map.healthsRegions.get(index);
            bonus = new Bonus(this.getRandomSpawnPostiton(region), BonusType.HEALTH);
            this.battlefieldModel.spawnBonus(bonus, this.inc, 30);
         }
         break;
      case 5:
         if (this.battlefieldModel.battleInfo.map.damagesRegions.size() > 0) {
            index = this.random.nextInt(this.battlefieldModel.battleInfo.map.damagesRegions.size());
            region = (BonusRegion)this.battlefieldModel.battleInfo.map.damagesRegions.get(index);
            bonus = new Bonus(this.getRandomSpawnPostiton(region), BonusType.DAMAGE);
            this.battlefieldModel.spawnBonus(bonus, this.inc, 30);
         }
         break;
      case 6:
         if (this.battlefieldModel.battleInfo.map.nitrosRegions.size() > 0) {
            index = this.random.nextInt(this.battlefieldModel.battleInfo.map.nitrosRegions.size());
            region = (BonusRegion)this.battlefieldModel.battleInfo.map.nitrosRegions.get(index);
            bonus = new Bonus(this.getRandomSpawnPostiton(region), BonusType.NITRO);
            this.battlefieldModel.spawnBonus(bonus, this.inc, 30);
         }
      }

      ++this.inc;
   }

   public void battleFinished() {
      this.prevFund = 0;
      this.crystallFund = 0;
      this.goldFund = 0;
      this.nextGoldFund = (int)RandomUtils.getRandom(700.0F, 730.0F);
   }

   private Vector3 getRandomSpawnPostiton(BonusRegion region) {
      Vector3 f = new Vector3(0.0F, 0.0F, 0.0F);
      Random rand = new Random();
      f.x = region.min.x + (region.max.x - region.min.x) * rand.nextFloat();
      f.y = region.min.y + (region.max.y - region.min.y) * rand.nextFloat();
      f.z = region.max.z;
      return f;
   }

   public void updatedFund() {
      int deff = (int)this.battlefieldModel.tanksKillModel.getBattleFund() - this.prevFund;
      this.goldFund += deff;
      this.crystallFund += deff;
      if (this.goldFund >= this.nextGoldFund) {
         this.spawnBonus(BonusType.GOLD);
         this.nextGoldFund = (int)RandomUtils.getRandom(700.0F, 730.0F);
         this.goldFund = 0;
      }

      if (this.crystallFund >= 6) {
         for(int i = 0; i < (int)RandomUtils.getRandom(1.0F, 6.0F); ++i) {
            this.spawnBonus(BonusType.CRYSTALL);
         }

         this.crystallFund = 0;
      }

      this.prevFund = (int)this.battlefieldModel.tanksKillModel.getBattleFund();
   }

   public void run() {
      if (this.battlefieldModel.battleInfo.map.crystallsRegions.size() <= 0 && this.battlefieldModel.battleInfo.map.goldsRegions.size() <= 0) {
         this.battlefieldModel = null;
      }

      while(this.battlefieldModel != null) {
         try {
            Thread.sleep(5000L);
            if (this.battlefieldModel == null || this.battlefieldModel.players == null) {
               break;
            }

            this.spawnRandomBonus();
         } catch (InterruptedException var2) {
            Logger.log(Type.ERROR, var2.getMessage());
         }
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
