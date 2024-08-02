package gtanks.battles.bonuses;

public enum BonusType {
   GOLD {
      public String toString() {
         return "gold";
      }
   },
   CRYSTALL {
      public String toString() {
         return "crystall";
      }
   },
   ARMOR {
      public String toString() {
         return "armor";
      }
   },
   HEALTH {
      public String toString() {
         return "health";
      }
   },
   DAMAGE {
      public String toString() {
         return "damage";
      }
   },
   NITRO {
      public String toString() {
         return "nitro";
      }
   };

   private BonusType() {
   }

   // $FF: synthetic method
   BonusType(BonusType var3) {
      this();
   }
}
