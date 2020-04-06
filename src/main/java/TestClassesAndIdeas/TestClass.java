package TestClassesAndIdeas;

import Server.Secondary.Person;
import Server.Services.DBService;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestClass {
    public static void main(String[] args) {
//        java.util.Date utilDate = new java.util.Date();
//        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
//        System.out.println(sqlDate);

        System.out.println("pass1".hashCode());
        System.out.println("pass2".hashCode());

//        System.out.println("test".getClass().getSimpleName());
//        System.out.println(new Command("test").getClass().getSimpleName());

//        try {
//            DBService.connect();
//            DBService.saveClientCard(new Person());
//            DBService.disconnect();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}
