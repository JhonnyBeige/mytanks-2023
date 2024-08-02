package gtanks.services.ban;

public class BanChatCommads {
   public static final String BAN_FIVE_MINUTES = "banminutes";
   public static final String BAN_ONE_HOUR = "banhour";
   public static final String BAN_ONE_DAY = "banday";
   public static final String BAN_ONE_WEEK = "banweek";
   public static final String BAN_ONE_MONTH = "banmonth";
   public static final String BAN_HALF_YEAR = "banhalfyear";
   public static final String BAN_FOREVER = "banforever";

   public static BanTimeType getTimeType(String cmd) {
      BanTimeType time = null;
      switch(cmd.hashCode()) {
      case -1856448559:
         if (cmd.equals("banmonth")) {
            time = BanTimeType.ONE_MONTH;
         }
         break;
      case -1807367696:
         if (cmd.equals("banminutes")) {
            time = BanTimeType.FIVE_MINUTES;
         }
         break;
      case -1396352723:
         if (cmd.equals("banday")) {
            time = BanTimeType.ONE_DAY;
         }
         break;
      case -337128845:
         if (cmd.equals("banhour")) {
            time = BanTimeType.ONE_HOUR;
         }
         break;
      case -336692093:
         if (cmd.equals("banweek")) {
            time = BanTimeType.ONE_WEEK;
         }
         break;
      case 745035384:
         if (cmd.equals("banforever")) {
            time = BanTimeType.FOREVER;
         }
         break;
      case 1101001727:
         if (cmd.equals("banhalfyear")) {
            time = BanTimeType.HALF_YEAR;
         }
      }

      return time;
   }
}
