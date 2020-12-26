package server;

import java.sql.*;

public class SQLiteHandler {
    public static Connection connect;
    public static PreparedStatement psGetNickname;
    public static PreparedStatement psRegistration;
    public static PreparedStatement psChangeNick;

    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connect = DriverManager.getConnection("jdbc:sqlite:main.db");
            psGetNickname = connect.prepareStatement("SELECT nickname FROM Users WHERE password = ? AND login = ?;");
            psRegistration = connect.prepareStatement("INSERT INTO Users(login, password, nickname) VALUES(?, ?, ?);");
            psChangeNick = connect.prepareStatement("UPDATE Users SET nickname = ? WHERE nickname = ?;");
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getNicknameByLoginAndPassword(String login, String password) {
        String nick = null;
        try {
            psGetNickname.setString(2, login);
            psGetNickname.setString(1, password);
            ResultSet rs = psGetNickname.executeQuery();
            if (rs.next()) {
                nick = rs.getString(1);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return nick;

    }

    public static boolean registration(String login, String password, String nickname) {
        try {
            psRegistration.setString(1, login.toLowerCase());
            psRegistration.setString(2, password);
            psRegistration.setString(3, nickname);
            psRegistration.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean changeNickname(String oldNick, String newNick) {
        try {
            psChangeNick.setString(1, newNick);
            psChangeNick.setString(2, oldNick);
            psChangeNick.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static void disconnect() {
        try {
            psChangeNick.close();
            psRegistration.close();
            psGetNickname.close();
            connect.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
