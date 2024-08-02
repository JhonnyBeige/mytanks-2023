package gtanks.system.restart;

import gtanks.battles.BattlefieldModel;
import gtanks.battles.timer.schedulers.runtime.TankRespawnScheduler;
import gtanks.commands.Type;
import gtanks.lobby.battles.BattleInfo;
import gtanks.lobby.battles.BattlesList;
import gtanks.logger.Logger;
import gtanks.main.netty.NettyService;
import gtanks.services.LobbysServices;
import gtanks.services.annotations.ServicesInject;
import gtanks.system.quartz.QuartzService;
import gtanks.system.quartz.TimeType;
import gtanks.system.quartz.impl.QuartzServiceImpl;
import gtanks.users.locations.UserLocation;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Locale;

public class ServerRestartService {
   private static ServerRestartService instance = new ServerRestartService();
   @ServicesInject(
      target = LobbysServices.class
   )
   private static LobbysServices lobbysServices = LobbysServices.getInstance();
   @ServicesInject(
      target = QuartzServiceImpl.class
   )
   private static QuartzService quartzService = QuartzServiceImpl.inject();
   @ServicesInject(
      target = NettyService.class
   )
   private static NettyService nettyServices = NettyService.inject();

   private ServerRestartService() {
   }

   public void restart() {
      lobbysServices.sendCommandToAllUsers(Type.LOBBY, UserLocation.ALL, "server_halt");
      quartzService.addJob("ServerRestartJob", "Systems Jobs", (e) -> {
         TankRespawnScheduler.dispose();
         Iterator var3 = BattlesList.getList().iterator();

         while(var3.hasNext()) {
            BattleInfo battle = (BattleInfo)var3.next();
            BattlefieldModel model = battle.model;
            if (model != null) {
               model.tanksKillModel.restartBattle(false);
            }
         }

         quartzService.addJob("ServerRestartJob: Destroy", "Systems Jobs", (e_) -> {
            nettyServices.destroy();
            Logger.log("Server can be shutdowning!");
            this.disableSystemOutput();
         }, TimeType.SEC, 10L);
      }, TimeType.SEC, 40L);
   }

   private void disableSystemOutput() {
      System.setOut(new PrintStream(new OutputStream() {
         public void write(int b) {
         }
      }) {
         public void flush() {
         }

         public void close() {
         }

         public void write(int b) {
         }

         public void write(byte[] b) {
         }

         public void write(byte[] buf, int off, int len) {
         }

         public void print(boolean b) {
         }

         public void print(char c) {
         }

         public void print(int i) {
         }

         public void print(long l) {
         }

         public void print(float f) {
         }

         public void print(double d) {
         }

         public void print(char[] s) {
         }

         public void print(String s) {
         }

         public void print(Object obj) {
         }

         public void println() {
         }

         public void println(boolean x) {
         }

         public void println(char x) {
         }

         public void println(int x) {
         }

         public void println(long x) {
         }

         public void println(float x) {
         }

         public void println(double x) {
         }

         public void println(char[] x) {
         }

         public void println(String x) {
         }

         public void println(Object x) {
         }

         public PrintStream printf(String format, Object... args) {
            return this;
         }

         public PrintStream printf(Locale l, String format, Object... args) {
            return this;
         }

         public PrintStream format(String format, Object... args) {
            return this;
         }

         public PrintStream format(Locale l, String format, Object... args) {
            return this;
         }

         public PrintStream append(CharSequence csq) {
            return this;
         }

         public PrintStream append(CharSequence csq, int start, int end) {
            return this;
         }

         public PrintStream append(char c) {
            return this;
         }
      });
   }

   public static ServerRestartService inject() {
      return instance;
   }
}
