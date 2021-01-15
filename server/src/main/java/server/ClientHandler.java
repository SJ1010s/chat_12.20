package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class ClientHandler {
    private static final Logger cliHandLogger = Logger.getLogger(ClientHandler.class.getName());

    Server server = null;
    Socket socket = null;
    DataInputStream in;
    DataOutputStream out;
    private String nickname;
    private String login;
    ExecutorService service = Executors.newCachedThreadPool();

    public ClientHandler(Server server, Socket socket) {
        try {
            Handler consoleServHandler = new ConsoleHandler();
            Handler fileServHandler = new FileHandler(
                    "Log/ClientHandlerLog_%g.xml", 10*1024 ,40, true);
            cliHandLogger.addHandler(consoleServHandler);
            cliHandLogger.addHandler(fileServHandler);

            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

  //          new Thread(()-> {
            service.submit(()->{
                    try {
                        // цикл аутентификации
                        while (true){
                            socket.setSoTimeout(30000);
                            String str = in.readUTF();

                            if (str.startsWith("/auth")){
                                String[] token = str.split("\\s");
                                String newNick = server.getAuthService().getNicknameByLoginAndPassword(token[1], token[2]);
                                login = token[1];

                                if (newNick != null){
                                    if(!server.isLoginAuthenticated(token[1])) {
                                        nickname = newNick;
                                        sendMsg("/authok " + nickname);
                                        server.subscribe(this);
//                                        System.out.println("Клиент " + nickname + " подключился");
                                        cliHandLogger.log(Level.INFO, "Клиент " + nickname + " подключился");
                                        socket.setSoTimeout(0);
                                        break;
                                    }else{
                                        sendMsg("С данной учетной записью уже зашли");
                                    }
                                }else {
                                    sendMsg("Неверный логин / пароль");
                                }
                            }

                            if (str.startsWith("/reg")){
                                String[] token = str.split("\\s");
                                if(token.length < 4){
                                    continue;
                                }
                                boolean isRegistration = server.getAuthService()
                                        .registration(token[1], token[2], token[3]);
                                if(isRegistration){
                                    sendMsg("/regok");
                                } else {
                                    sendMsg("/regno");
                                }
                            }
                        }

                        // цикл работы
                        while (true) {
                            String str = in.readUTF();

                            if(str.startsWith("/")) {

                                if (str.equals("/end")) {
                                    out.writeUTF("/end");
                                    break;
                                }

                                if (str.startsWith("/w")) {
                                    String[] token = str.split("\\s+", 3);
                                    if (token.length <3){
                                        continue;
                                    }
                                    server.privateCastMsg(this, token[1], token[2]);
                                }

                                if (str.startsWith("/chnick")){
                                    String[] token = str.split("\\s+", 3);
                                    if (token.length > 2){
                                        sendMsg("Ник не может содержать пробелов");
                                        continue;
                                    }
                                    if (token.length == 2) {
                                        if(server.getAuthService().changeNickname(this.nickname, token[1])){
                                            this.nickname = token[1];
                                            server.broadClientList();
                                            sendMsg("Ник изменен");
                                        } else {
                                            sendMsg("Ошибка при изменении ника");
                                        }
                                    }
                                }

                            }else {
                                server.broadCastMsg(this, str);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
//                        System.out.println("Клиент отключился");
                        cliHandLogger.log(Level.INFO, "Клиент " + nickname + " отключился");
                        server.unsubscribe(this);
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            service.shutdown();
        }
    }

    public String getNickname(){
        return nickname;
    }

    public String getLogin() {
        return login;
    }

}
