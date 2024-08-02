package gtanks.main;

import gtanks.RankUtils;
import gtanks.battles.maps.MapsLoader;
import gtanks.battles.tanks.loaders.HullsFactory;
import gtanks.battles.tanks.loaders.WeaponsFactory;
import gtanks.groups.UserGroupsLoader;
import gtanks.gui.console.ConsoleWindow;
import gtanks.logger.Logger;
import gtanks.logger.remote.RemoteDatabaseLogger;
import gtanks.main.netty.NettyService;
import gtanks.rmi.payments.kit.loader.KitsLoader;
import gtanks.rmi.payments.sfx.loader.SFXLoader;
import gtanks.rmi.payments.shop.loader.SHOPLoader;
import gtanks.rmi.server.RMIServer;
import gtanks.services.AutoEntryServices;
import gtanks.services.hibernate.HibernateService;
import gtanks.system.BattlesSystemList;
import gtanks.system.SystemConsoleHandler;
import gtanks.system.dailybonus.DailyBonusService;
import gtanks.system.quartz.impl.QuartzServiceImpl;
import gtanks.test.server.configuration.ConfigurationsLoader;
import gtanks.users.garage.GarageItemsLoader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.json.simple.parser.ParseException;

public class Main {
   public static ConsoleWindow console;
   private static SystemConsoleHandler sch;

   public static void main(String[] args) {
      try {
         PropertyConfigurator.configure(Main.class.getResource("log4j.properties"));
         ConfigurationsLoader.load("");
         initFactorys();
         UserGroupsLoader.load("groups/");
         Logger.log("Connecting to DB...");
         Session session = HibernateService.getSessionFactory().getCurrentSession();
         session.beginTransaction();
         SQLQuery query = session.createSQLQuery("SET NAMES 'utf8' COLLATE 'utf8_general_ci';");
         System.out.println("Setting UTF-8 charset on DB: " + query.executeUpdate());
         session.getTransaction().commit();
         QuartzServiceImpl.inject();
         DailyBonusService.instance();
         AutoEntryServices.instance();
         NettyService.inject().init();
         BattlesSystemList.init();
         RMIServer.run();
      } catch (Exception var3) {
         var3.printStackTrace();
         RemoteDatabaseLogger.error(var3);
      }

   }

   private static void initFactorys() throws FileNotFoundException, IOException, ParseException {
      GarageItemsLoader.loadFromConfig("turrets.json", "hulls.json", "colormaps.json", "inventory.json", "effects.json", "kits.json");
      WeaponsFactory.init("weapons/");
      HullsFactory.init("hulls/");
      RankUtils.init();
      MapsLoader.initFactoryMaps();
      SFXLoader.load("sfx_data.json");
      SHOPLoader.load("shop_data.json");
      sch = SystemConsoleHandler.getInstance();
      sch.start();
   }
}
