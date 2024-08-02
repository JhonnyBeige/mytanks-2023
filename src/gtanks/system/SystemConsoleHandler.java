package gtanks.system;

import gtanks.battles.tanks.loaders.HullsFactory;
import gtanks.battles.tanks.loaders.WeaponsFactory;
import gtanks.logger.Logger;
import gtanks.logger.Type;
import gtanks.main.params.OnlineStats;
import gtanks.services.annotations.ServicesInject;
import gtanks.system.restart.ServerRestartService;
import java.util.Scanner;

public class SystemConsoleHandler extends Thread {
   private static final SystemConsoleHandler instance = new SystemConsoleHandler();
   @ServicesInject(
      target = ServerRestartService.class
   )
   private static ServerRestartService serverRestartService = ServerRestartService.inject();

   public static SystemConsoleHandler getInstance() {
      return instance;
   }

   private SystemConsoleHandler() {
      this.setName("SystemConsoleHandler thread");
   }

   private void onCommand(String input) {
      String[] spaceSplit = input.replace("/", "").split(" ");
      String var3;
      switch((var3 = spaceSplit[0]).hashCode()) {
      case -1012222381:
         if (var3.equals("online")) {
            System.out.println(this.getOnlineInfoString());
         }
         break;
      case 3636:
         if (var3.equals("rf")) {
            Logger.log(Type.WARNING, "Attention! The factories of weapons and hulls will be reloaded!");
            WeaponsFactory.init("weapons/");
            HullsFactory.init("hulls/");
         }
         break;
      case 3198785:
         if (var3.equals("help")) {
            System.out.println(this.getHelpString());
         }
         break;
      case 1097506319:
         if (var3.equals("restart")) {
            serverRestartService.restart();
         }
      }

   }

   private String getOnlineInfoString() {
      return "\n Total online: " + OnlineStats.getOnline() + "\n Max online: " + OnlineStats.getMaxOnline() + "\n";
   }

   private String getHelpString() {
      return "rf - reload item's factories.\nonline - print current online.";
   }

   public void run() {
      Throwable var1 = null;
      Object var2 = null;

      try {
         Scanner scn = new Scanner(System.in);

         try {
            String input = "";

            while(true) {
               input = scn.nextLine();
               this.onCommand(input);
            }
         } finally {
            if (scn != null) {
               scn.close();
            }

         }
      } catch (Throwable var10) {
         if (var1 == null) {
            var1 = var10;
         } else if (var1 != var10) {
            var1.addSuppressed(var10);
         }

         try {
            throw var1;
         } catch (Throwable e) {
            throw new RuntimeException(e);
         }
      }
   }
}
