package Server;
import Server.Services.DBService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;

    public Server() throws SQLException {
        ServerSocket server = null;
        Socket socket = null;
        clients = new Vector<>();


        try {
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен!");
            DBService.connect();
            System.out.println("Сервер подключён к БД!");

            while (true){
                socket = server.accept();
                System.out.println("Клиент подключился!");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DBService.disconnect();
        }
    }

    public void clientOnline(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void clientOffline(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}
