package server;

public class SQLiteAuthService implements AuthService {
    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return SQLiteHandler.getNicknameByLoginAndPassword(login, password);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        return SQLiteHandler.registration(login, password, nickname);
    }

    @Override
    public boolean changeNickname(String oldNick, String newNick) {
        return SQLiteHandler.changeNickname(oldNick, newNick);
    }

}
