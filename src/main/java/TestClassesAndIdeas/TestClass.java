package TestClassesAndIdeas;


import Server.Secondary.HibernatePerson;
import Server.Services.HibernateDBConnectionSingleton;
import org.hibernate.Session;

import java.util.List;


public class TestClass {
    public static void main(String[] args) {
        Session session = HibernateDBConnectionSingleton.getSessionFactory().openSession();
//        String sql = "From " + HibernatePerson.class.getSimpleName();
//        List<HibernatePerson> clients = session.createQuery(sql).list();
//        System.out.println(clients.size());
        List<HibernatePerson> clients =
                session.createSQLQuery("SELECT * FROM wmp.clients WHERE archived IS NULL AND deleted='0';")
                        .list();
        System.out.println(clients.size());
        session.disconnect();
        HibernateDBConnectionSingleton.closeFactory();
    }
}
