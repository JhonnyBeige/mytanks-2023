package gtanks.main.database.impl;

import gtanks.lobby.top.HallOfFame;
import gtanks.logger.Logger;
import gtanks.logger.Type;
import gtanks.logger.remote.LogObject;
import gtanks.logger.remote.RemoteDatabaseLogger;
import gtanks.main.database.DatabaseManager;
import gtanks.main.netty.blackip.BlackIP;
import gtanks.rmi.payments.mapping.Payment;
import gtanks.services.hibernate.HibernateService;
import gtanks.users.TypeUser;
import gtanks.users.User;
import gtanks.users.garage.Garage;
import gtanks.users.karma.Karma;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DatabaseManagerImpl extends Thread implements DatabaseManager {
   private static final DatabaseManagerImpl instance = new DatabaseManagerImpl();
   private Map<String, User> cache;

   private DatabaseManagerImpl() {
      super("DatabaseManagerImpl THREAD");
      this.cache = new TreeMap(String.CASE_INSENSITIVE_ORDER);
   }

   public void register(User user) {
      this.configurateNewAccount(user);
      Garage garage = new Garage();
      garage.parseJSONData();
      garage.setUserId(user.getNickname());
      Karma emptyKarma = new Karma();
      emptyKarma.setUserId(user.getNickname());
      Session session = null;
      Transaction tx = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         tx = session.beginTransaction();
         session.save(user);
         session.save(garage);
         session.save(emptyKarma);
         tx.commit();
      } catch (Exception var7) {
         if (tx.wasRolledBack()) {
            tx.rollback();
         }

         var7.printStackTrace();
         RemoteDatabaseLogger.error(var7);
      }

   }

   public void update(Karma karma) {
      Session session = null;
      Transaction tx = null;
      User user = null;
      if ((user = (User)this.cache.get(karma.getUserId())) != null) {
         user.setKarma(karma);
      }

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         tx = session.beginTransaction();
         session.update(karma);
         tx.commit();
      } catch (Exception var6) {
         if (tx.wasRolledBack()) {
            tx.rollback();
         }

         var6.printStackTrace();
         RemoteDatabaseLogger.error(var6);
      }

   }

   public void update(Garage garage) {
      Session session = null;
      Transaction tx = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         tx = session.beginTransaction();
         session.update(garage);
         tx.commit();
      } catch (Exception var5) {
         if (tx.wasRolledBack()) {
            tx.rollback();
         }

         var5.printStackTrace();
         RemoteDatabaseLogger.error(var5);
      }

   }

   public void update(User user) {
      Session session = null;
      Transaction tx = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         tx = session.beginTransaction();
         session.update(user);
         tx.commit();
      } catch (Exception var5) {
         if (tx.wasRolledBack()) {
            tx.rollback();
         }

         var5.printStackTrace();
         RemoteDatabaseLogger.error(var5);
      }

   }

   public User getUserById(String nickname) {
      Session session = null;
      Transaction tx = null;
      User user = null;
      if ((user = this.getUserByIdFromCache(nickname)) != null) {
         return user;
      } else {
         try {
            session = HibernateService.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM User U WHERE U.nickname = :nickname");
            query.setString("nickname", nickname);
            user = (User)query.uniqueResult();
            tx.commit();
         } catch (Exception var6) {
            if (tx.wasRolledBack()) {
               tx.rollback();
            }

            var6.printStackTrace();
            RemoteDatabaseLogger.error(var6);
         }

         return user;
      }
   }

   public static DatabaseManager instance() {
      return instance;
   }

   public void cache(User user) {
      if (user == null) {
         Logger.log(Type.ERROR, "DatabaseManagerImpl::cache user is null!");
      } else {
         this.cache.put(user.getNickname(), user);
      }
   }

   public void uncache(String id) {
      this.cache.remove(id);
   }

   public User getUserByIdFromCache(String nickname) {
      return (User)this.cache.get(nickname);
   }

   public boolean contains(String nickname) {
      return this.getUserById(nickname) != null;
   }

   public void configurateNewAccount(User user) {
      user.setCrystall(5);
      user.setNextScore(100);
      user.setType(TypeUser.DEFAULT);
      user.setEmail((String)null);
   }

   public int getCacheSize() {
      return this.cache.size();
   }

   public void initHallOfFame() {
      Session session = null;
      new ArrayList();

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         List<User> users = session.createCriteria(User.class).list();
         HallOfFame.getInstance().initHallFromCollection(users);
      } catch (Exception var4) {
         RemoteDatabaseLogger.error(var4);
      }

   }

   public Garage getGarageByUser(User user) {
      Session session = null;
      Transaction tx = null;
      Garage garage = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         tx = session.beginTransaction();
         Query query = session.createQuery("FROM Garage G WHERE G.userId = :nickname");
         query.setString("nickname", user.getNickname());
         garage = (Garage)query.uniqueResult();
         tx.commit();
      } catch (Exception var6) {
         if (tx.wasRolledBack()) {
            tx.rollback();
         }

         var6.printStackTrace();
         RemoteDatabaseLogger.error(var6);
      }

      return garage;
   }

   public Karma getKarmaByUser(User user) {
      Session session = null;
      Transaction tx = null;
      Karma karma = null;
      if (user.getKarma() != null) {
         return user.getKarma();
      } else {
         try {
            session = HibernateService.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM Karma K WHERE K.userId = :nickname");
            query.setString("nickname", user.getNickname());
            karma = (Karma)query.uniqueResult();
            tx.commit();
         } catch (Exception var6) {
            if (tx.wasRolledBack()) {
               tx.rollback();
            }

            var6.printStackTrace();
            RemoteDatabaseLogger.error(var6);
         }

         return karma;
      }
   }

   public BlackIP getBlackIPbyAddress(String address) {
      Session session = null;
      BlackIP ip = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         session.beginTransaction();
         Query query = session.createQuery("FROM BlackIP B WHERE B.ip = :ip");
         query.setString("ip", address);
         ip = (BlackIP)query.uniqueResult();
         session.getTransaction().commit();
      } catch (Exception var5) {
         var5.printStackTrace();
         RemoteDatabaseLogger.error(var5);
      }

      return ip;
   }

   public void register(BlackIP blackIP) {
      Session session = null;
      Transaction tx = null;
      if (this.getBlackIPbyAddress(blackIP.getIp()) == null) {
         try {
            session = HibernateService.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            session.saveOrUpdate(blackIP);
            tx.commit();
         } catch (Exception var5) {
            if (tx.wasRolledBack()) {
               tx.rollback();
            }

            var5.printStackTrace();
            RemoteDatabaseLogger.error(var5);
         }

      }
   }

   public void unregister(BlackIP blackIP) {
      Session session = null;
      Transaction tx = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         tx = session.beginTransaction();
         SQLQuery query = session.createSQLQuery("delete from tanks.black_ips where ip = :ip");
         query.setString("ip", blackIP.getIp());
         query.executeUpdate();
         tx.commit();
      } catch (Exception var5) {
         if (tx.wasRolledBack()) {
            tx.rollback();
         }

         var5.printStackTrace();
         RemoteDatabaseLogger.error(var5);
      }

   }

   public void register(LogObject log) {
      Session session = null;
      Transaction tx = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         tx = session.beginTransaction();
         session.save(log);
         tx.commit();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public List<LogObject> collectLogs() {
      Session session = null;
      List logs = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         Transaction tx = session.beginTransaction();
         logs = session.createCriteria(LogObject.class).list();
         tx.commit();
         return logs;
      } catch (Exception var5) {
         var5.printStackTrace();
         RemoteDatabaseLogger.error(var5);
         return null;
      }
   }

   public Payment getPaymentById(long paymentId) {
      Session session = null;
      Payment payment = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         session.beginTransaction();
         Query query = session.createQuery("FROM Payment p WHERE p.idPayment = :pid");
         query.setLong("pid", paymentId);
         payment = (Payment)query.uniqueResult();
         session.getTransaction().commit();
      } catch (Exception var6) {
         var6.printStackTrace();
         RemoteDatabaseLogger.error(var6);
      }

      return payment;
   }

   public void update(Payment payment) {
      Session session = null;
      Transaction tx = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         tx = session.beginTransaction();
         session.update(payment);
         tx.commit();
      } catch (Exception var5) {
         if (tx.wasRolledBack()) {
            tx.rollback();
         }

         var5.printStackTrace();
         RemoteDatabaseLogger.error(var5);
      }

   }

   public List<Garage> collectGarages() {
      Session session = null;
      List garages = null;

      try {
         session = HibernateService.getSessionFactory().getCurrentSession();
         Transaction tx = session.beginTransaction();
         garages = session.createCriteria(Garage.class).list();
         tx.commit();
         return garages;
      } catch (Exception var5) {
         var5.printStackTrace();
         RemoteDatabaseLogger.error(var5);
         return null;
      }
   }
}
