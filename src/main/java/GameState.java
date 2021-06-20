import java.io.Serializable;

/**
 * Klasa odpowiedzialna za reprezentowanie aktualnego stanu gry. Zawiera informacje
 * na temat znakow na planszy i aktywnego gracza w aktualnej turze.
 * Uzywana do aktualizacji stanu gry pomiedzy klientem a serwerem
 */
public class GameState implements Serializable {

    /**
     * Plansza aktualnej gry
     */
    private Board board;
    /**
     * Aktywny gracz (moze zrobic teraz ruch na planszy)
     */
    private Player activePlayer;
    /**
     * SerialVersionUID do serializacji danych.
     */
    private static final long serialVersionUID = 3002053260033745936L;

    /**
     * Zmienna do przechowywania ostatniego ruchu w formacie
     * nick: wiersz,kolumna,znak
     * np. Marek: 0, 0, X
     */
    private String lastMove;
    /**
     * Tworzymy stan gry przez przypisania aktualnej planszy i gracza
     */
    public GameState(Board board, Player activePlayer, String lastMove) {
        this.board = board;
        this.activePlayer = activePlayer;
        this.lastMove = lastMove;
    }

    /**
     * Sprawdzanie czy gra jest skonczona
     */
    public boolean hasEnded() {
        return board.hasEnded();
    }

    /**
     * Sprawdzenie ktory czy gracz wygral
     */
    public boolean hasWon() {
        return board.hasWon();
    }


    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    public String getActiveName() {
        return activePlayer.getName();
    }

    public String getActiveMark() {
        return activePlayer.getMark() + "";
    }

    public String getLastMove() {
        return lastMove;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }
}