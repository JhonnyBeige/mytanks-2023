package gtanks.services.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateService {
   private static final SessionFactory sessionFactory;

   static {
      try {
         sessionFactory = (new AnnotationConfiguration()).configure("hibernate.cfg.xml").buildSessionFactory();
      } catch (Throwable var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }

   public static SessionFactory getSessionFactory() {
      return sessionFactory;
   }
}
