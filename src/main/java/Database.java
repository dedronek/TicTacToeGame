import java.sql.*;

public class Database {



    //parametry polaczeniowe
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/tictactoe";
    private static final String USERNAME = "java";
    private static final String PASSWORD = "";

    /**
     * handler polaczenia z baza
     */
    static Connection conn;

    /**
     * Proba polaczenia do bazy, conn = handler polaczenia z baza
     */
    public Database() {

        //polaczenie do bazy
        try {
            conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //do testow
    public static void main(String[] args) {
        Database d = new Database();
        d.pushMove(2,"0,1,X,");
    }

    /**
     * Wyszukuje gracza w bazie, jesli znajdzie, zwraca jego ID, jesli nie, tworzy nowego
     * @param nick
     * @return ID gracza
     */
    public int pushPlayer(String nick) {

        int id = searchPlayer(nick);
        if (id == 0) {
            String insertSql = "INSERT INTO `players` (`id`, `nick`) VALUES (NULL, '" + nick + "')";
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(insertSql);
                ps.executeUpdate();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return id=searchPlayer(nick);
        } else {
            return id;
        }

    }

    /**
     * Tworzy nowa gre na podstawie ID graczy i ich znakow
     * @param player1ID - id gracza 1
     * @param player1Mark znak gracza 1
     * @param player2ID id gracza 2
     * @param player2Mark znak gracza 2
     * @return id gry
     */
    public int pushGame(int player1ID,char player1Mark, int player2ID, char player2Mark)
    {

        int id = 0;
        String insertSql = "INSERT INTO `games` (`player1`, `player1Mark`,`player2`,`player2Mark`,`status`) VALUES ('"+player1ID+"','" + player1Mark+ "','"+player2ID+"','"+player2Mark+"','1')";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(insertSql,Statement.RETURN_GENERATED_KEYS);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try (ResultSet getId = ps.getGeneratedKeys()) {
            while (getId.next()) {
                id = getId.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    /**
     * Wyszukuje gracza po nicku
     * @param nick nick szukanego gracza
     * @return zwraca id gracza, jesli nie znajdzie zwraca 0
     */
    public int searchPlayer(String nick) {
        int id = 0;
        String searchSql = "Select `id` from `players` where nick='" + nick + "'";

        PreparedStatement search = null;
        try {
            search = conn.prepareStatement(searchSql);
            ResultSet searchResult = search.executeQuery();

            while (searchResult.next()) {
                id = searchResult.getInt("id");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    /**
     * Metoda pomocnicza. Wyciaga z bazy aktualnie zrobione ruchy w danej grze
     * @param gameID - id gry
     * @return zwraca string dotychcza zrobionymi ruchami, jesli brak zwraca pusty ciag znakow
     */
    public String getPreviousMoves(int gameID)
    {
        String moves = null;
        String searchSql = "Select `moves` from `games` where `gameId`="+gameID;

        PreparedStatement search = null;
        try {
            search = conn.prepareStatement(searchSql);
            ResultSet searchResult = search.executeQuery();

            while (searchResult.next()) {
                moves = searchResult.getString("moves");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (moves == null)
            return "";
        else
            return moves+System.lineSeparator();
    }

    /**
     * Metoda wrzuca aktualny ruch do gry o podanym ID
     * @param gameID id gry
     * @param move ruch w postaci wiersz,kolumna,znak
     */
    public void pushMove(int gameID, String move)
    {
        String previousMoves = getPreviousMoves(gameID);
        String updatedMoves = previousMoves + move;

        String updateSql = "UPDATE `games` SET `moves` = '"+updatedMoves+"' WHERE `gameId` = "+gameID;

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(updateSql);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Zmienia stan gry w zaleznosci od przebiegu rozgrywki
     *  status 1 - w trakcie, 0 - zakonczona, -1 - przerwana
     * @param gameID id gry
     * @param status status do zapisania w bazie
     */
    public void updateGameStatus(int gameID, int status)
    {
        String updateSql = "UPDATE `games` SET `status` = '"+status+"' WHERE `gameId` = "+gameID;

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(updateSql);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
