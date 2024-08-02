package gtanks.users.garage.enums;

public enum ItemType {
   WEAPON {
      public String toString() {
         return "1";
      }
   },
   ARMOR {
      public String toString() {
         return "2";
      }
   },
   COLOR {
      public String toString() {
         return "3";
      }
   },
   INVENTORY {
      public String toString() {
         return "4";
      }
   },
   PLUGIN {
      public String toString() {
         return "5";
      }
   },
   KIT {
      public String toString() {
         return "6";
      }
   };

   private ItemType() {
   }

   // $FF: synthetic method
   ItemType(ItemType var3) {
      this();
   }
}
