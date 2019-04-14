package waterfall.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static Session currentSession;
    private static Transaction currentTransaction;

    public static void openSession() {
        currentSession = getSessionFactory().openSession();
    }

    public static void closeSession() {
        currentSession.close();
    }

    public static Session getCurrentSession() {
        return currentSession;
    }


    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            // Make sure the SessionFactory is created
            System.err.println("Error occured during the SessiongFactory creation");
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void openSessionWithTransaction() {
        openSession();
        currentTransaction = currentSession.beginTransaction();
    }

    public static void closeSessionWithTransaction() {
        currentTransaction.commit();
        closeSession();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
