package Server.Services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateDBConnectionSingleton {
    private HibernateDBConnectionSingleton(){}

    private static class ConnectionHolder{
        public static final SessionFactory SF = new Configuration()
                .configure()
                .buildSessionFactory();
            }

    public static SessionFactory getSessionFactory(){
        return ConnectionHolder.SF;
    }

    public static void closeFactory(){
        ConnectionHolder.SF.close();
    }
}
