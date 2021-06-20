import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

/**
 * Klasa reprezentujaca logike gry po stronie serwera
 */
public class ServerModel 
{
    /**
     * ID aktualnej gry do celow bazodanowych
     */
    private int ID;

    /**
     * Plansza na ktorej odbywa sie grra
     */
    private Board board;

    /**
     * Aktywny gracz - teraz on wykonuje ruch
     */
    private Player activePlayer;
    /**
     * Gracz oczekujacy na ruch przeciwnika
     */
    private Player idlePlayer;
    /**
     * Zmienna do przechowywania ostatniego ruchu w formacie
     * nick: wiersz,kolumna,znak
     * np. Marek: 0, 0, X
     */
    private String lastMove;

    /**
     * Konstruujemy obiekt zawierajacy plansze
     */
    public ServerModel() 
    {
        this.board = new Board();
    }

    /**
     * Podmiana graczy
     */
    public void toggleActivePlayer() 
    {
        Player temp = activePlayer;
        activePlayer = idlePlayer;
        idlePlayer = temp;
    }

    /**
     * Sprawdzenie czy gra sie zakonczyla
     * Gra zakonczona jesli ktorys z graczy wygral lub plansza jest pelna

     * @return True jesli zakonczona, false w przeciwnym razie
     */
    public boolean hasEnded() 
    {
        return board.hasEnded();
    }


    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }
    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }
    public Player getActivePlayer() { return activePlayer; }
    public void setActivePlayer(Player activePlayer) { this.activePlayer = activePlayer; }
    public Player getIdlePlayer() { return idlePlayer; }
    public void setIdlePlayer(Player idlePlayer) { this.idlePlayer = idlePlayer; }
    public PrintWriter getActiveSocketOut() { return activePlayer.getSocketOut(); }
    public PrintWriter getIdleSocketOut() { return idlePlayer.getSocketOut(); }
    public ObjectInputStream getActiveInputStream() { return activePlayer.getObjectIn(); }
    public ObjectOutputStream getActiveOutputStream() { return activePlayer.getObjectOut(); }
    public ObjectInputStream getIdleInputStream() { return idlePlayer.getObjectIn(); }
    public ObjectOutputStream getIdleOutputStream() { return idlePlayer.getObjectOut(); }
    public String getLastMove() {
        return lastMove;
    }
    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }
}