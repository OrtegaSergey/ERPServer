package Server.Services;

import Server.Secondary.Person;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DBService {
    private static Connection connection;
    private static Statement stmt;
    private static final String URL = "jdbc:mysql://localhost:3306/wmp?useSSL=false&autoReconnect=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1a7ye8hn10c4";

    public static void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() throws SQLException {
        connection.close();
    }


    public static String Auth(String login, String password) throws SQLException{
        checkConnection();
        String qry = String.format("SELECT name FROM wmp.users WHERE login='%s' AND password='%d';", login, password.hashCode());
        ResultSet rs = stmt.executeQuery(qry);
        if (rs.next()){
            return rs.getString("name");
        }
        return null;
    }

    public static String registration (String name, String login, String password) throws SQLException{
        checkConnection();
        String qry = String.format("SELECT id FROM wmp.users WHERE login='%s'", login);
        ResultSet rs = stmt.executeQuery(qry);
        if (rs.next()){
            if (rs.getString(1)!=null){
                return "Данный логин уже зарегистрирован!";
            }
        }

        String successQry = String.format("INSERT INTO wmp.users (login, password, name) " +
                "VALUES ('%s', '%d', '%s');", login, password.hashCode(), name);
        stmt.executeUpdate(successQry);
        return "/registered";
    }


    public static ArrayList<Person> getActiveClients() throws SQLException{
        checkConnection();
        ArrayList<Person> al= new ArrayList<Person>();
        String qry = String.format("SELECT * FROM wmp.clients WHERE archived IS NULL AND deleted='0';");
        ResultSet rs = stmt.executeQuery(qry);
        while (rs.next()) {
            al.add(new Person("activeClient", rs));
        }
        return al;
    }

    public static ArrayList<Person> getArchivedClients() throws SQLException{
        checkConnection();
        ArrayList<Person> al= new ArrayList<Person>();
        String qry = String.format("SELECT * FROM wmp.clients WHERE archived IS NOT NULL AND deleted='0';");
        ResultSet rs = stmt.executeQuery(qry);
        while (rs.next()) {
            al.add(new Person("archivedClient", rs));
        }
        return al;
    }

    public static Person getClientCard(int id) throws SQLException {
        checkConnection();
        String qry = String.format("SELECT * FROM wmp.clients WHERE id='%d';", id);
        ResultSet rs = stmt.executeQuery(qry);
        if (rs.next()){
            return new Person("clientCard", rs);
        }
        return null;
    }

    public static ArrayList<String> getBranches() throws SQLException{
        checkConnection();
        ArrayList<String> branchesList = new ArrayList<>();
        String qry = String.format("SELECT * FROM wmp.branches WHERE enable='1'");
        ResultSet rs = stmt.executeQuery(qry);
        while (rs.next()){
            branchesList.add(rs.getString("branch"));
        }
        return branchesList;
    }

    private static void checkConnection() throws SQLException{
            if (!connection.isValid(1000)){
                connect();
            }
    }

    public static String saveClientCard(Person clientCard){
        String qry = "UPDATE wmp.clients SET name=?, phone=?, address=?, mail=?, agreed=?, alert=? WHERE id=?;";
        try {
            PreparedStatement ps = connection.prepareStatement(qry);
            if (clientCard.getName()!=null){
                ps.setString(1, clientCard.getName());
            } else ps.setString(1, null);
            if (clientCard.getPhone()!=null){
                ps.setString(2, clientCard.getPhone());
            } else ps.setString(2, null);
            if (clientCard.getAddress()!=null){
                ps.setString(3, clientCard.getAddress());
            } else ps.setString(3, null);
            if (clientCard.getEmail()!=null){
                ps.setString(4, clientCard.getEmail());
            } else ps.setString(4, null);
            if (clientCard.getAgreed()!=null){
                ps.setTimestamp(5, clientCard.getAgreed());
            } else ps.setString(5, null);
            ps.setInt(6, clientCard.isAlert() ? 1 : 0);
            ps.setInt(7, clientCard.getId());

            int rows = ps.executeUpdate();
            return "Успешно!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Ошибка базы данных!";
        }
    }

    public static String archiveClient(int id) {
        LocalDate date = LocalDate.now();
        try {
        String checkQry = String.format("SELECT archived FROM wmp.clients WHERE id=%d", id);
        ResultSet rs = stmt.executeQuery(checkQry);
        if (rs.next()) {
            if (rs.getDate(1) != null) return "Клиент уже в архиве!";
            String qry = String.format("UPDATE wmp.clients SET archived='" + date + "' WHERE id=%d", id);
            stmt.executeUpdate(qry);
            return "Добавлен в архив!";
        }
        } catch (SQLException e){
            e.printStackTrace();
            return "Ошибка базы данных!";
        }
        return "Ошибка поиска!";
    }

    public static String unzip (int id){
        try {
            String checkQry = String.format("SELECT archived FROM wmp.clients WHERE id=%d", id);
            ResultSet rs = stmt.executeQuery(checkQry);
            if (rs.next()) {
                if (rs.getDate(1) == null) return "Клиент не в архиве!";
                String qry = String.format("UPDATE wmp.clients SET archived=null WHERE id=%d", id);
                stmt.executeUpdate(qry);
                return "Клиент разархивирован";
            }
        } catch (SQLException e){
            e.printStackTrace();
            return "Ошибка базы данных!";
        }
        return "Ошибка поиска!";
    }
}