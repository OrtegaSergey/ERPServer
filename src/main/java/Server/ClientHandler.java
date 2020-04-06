package Server;


import Server.Secondary.Person;
import Server.Services.DBService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientHandler {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Server server;
    private String name;
    Gson gson = new GsonBuilder().setDateFormat("dd.MM.yyyy HH:mm").create();


    ClientHandler(Server server, Socket socket) {
        try {
            this.socket = socket;
            this.server = server;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/")) {
                                if (str.startsWith("/auth")) {
                                    String[] token = str.split(" ");
                                    String newName = DBService.Auth(token[1], token[2]);
                                    if (newName == null) {
                                        sendMsg("Неверный логин/пароль!");
                                    } else {
                                        sendMsg("/authok " + newName);
                                        sendMsg(gson.toJson(DBService.getBranches()));
                                        name = newName;
                                        server.clientOnline(ClientHandler.this);
                                        break;
                                    }
                                }
                                if (str.startsWith("/reg")) {
                                    String[] tokens = str.split(" ");
                                    String dataBaseRegistrationAnswer = DBService.registration(tokens[1], tokens[2], tokens[3]);
                                    sendMsg(dataBaseRegistrationAnswer);
                                }
                            }
                        }
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/")) {
                                if (str.equals("/end")) {
                                    sendMsg("/ServerClosing");
                                    break;
                                }
                                if (str.equals("/getActiveClients")){
                                    ArrayList<Person> activeClients = DBService.getActiveClients();
                                    if (!activeClients.isEmpty()) {
                                        sendMsg(gson.toJson(activeClients));
                                    }else sendMsg("Таблица пуста!");
                                }
                                if (str.equals("/getArchivedClients")){
                                    ArrayList<Person> archivedClients = DBService.getArchivedClients();
                                    if (!archivedClients.isEmpty()) {
                                        sendMsg(gson.toJson(archivedClients));
                                    }else sendMsg("Таблица пуста!");
                                }
                                if (str.startsWith("/getClientCard")){
                                    String[] tokens = str.split(" ");
                                    int id = Integer.parseInt(tokens[1]);
                                    Person person = DBService.getClientCard(id);
                                    if (person!=null){
                                        sendMsg(gson.toJson(person));
                                    } else sendMsg("Клиента не существует!");
                                }
                                if (str.startsWith("/archiveClient")){
                                    String[] tokens = str.split(" ");
                                    String response = DBService.archiveClient(Integer.parseInt(tokens[1]));
                                    if (response.equals("Добавлен в архив!")) sendMsg("/archived");
                                    sendMsg(response);
                                }
                                if (str.startsWith("/unzipClient")){
                                    String[] tokens = str.split(" ");
                                    String response = DBService.unzip(Integer.parseInt(tokens[1]));
                                    if (response.equals("Клиент разархивирован")) sendMsg("/unzipped");
                                    sendMsg(response);
                                }
                            }
                            if (str.contains("saveClientCard")){
                                Person clientCard = gson.fromJson(str, Person.class);
                                sendMsg(DBService.saveClientCard(clientCard));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
