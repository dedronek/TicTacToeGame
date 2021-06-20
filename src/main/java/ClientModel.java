/**
 * Klasa reprezentujac klienta gry i jego logike
 */
public class ClientModel 
{
    /**
     * Plansza, na ktorej toczy sie rozgrywka.
     */
    private Board board;
    /**
     * Gracz, ktory wykonuje ruch w tej turze.
     */
    private Player activePlayer;

    /**
     * Konstruktor z utworzeniem planszy
     */
    public ClientModel() 
    {
        this.board = new Board();
    }

    /**
     * Pauzowanie rozgrywaki dopoki wazny ruch nie zostanie wykonany
     * (klikniecie w odpowiedni przycisk)
     */
    public void performOneMove() 
    {
        activePlayer.makeMove();
    }

    /**
     * Sprawdzanie czy pozycja na planszy nie jest juz zajeta
     * przez innego gracza
     * @param position Int[] - wiersz i kolumna
     * @return true jesli miejsce jest puste, w przeciwnym razie falsz
     */
    public boolean isBlank(int[] position) 
    {
        return board.isBlank(position);
    }

    /**
     * Dodanie znaku gracza na odpowiednie miejsce w planszy
     * @param row - wiersz planszy
     * @param col - kolumna planszy
     * @param mark - znak gracza (X lub O)
     */
    public void addMark(int row, int col, char mark) 
    {
        board.addMark(row, col, mark);
    }

    /**
     * Proba wlasciwego wykonania ruchu.
     * Jesli pole jest puste, nadpisujemy jego wartosc znakiem aktywnego gracza,
     * Zmieniamy flage aby zaznaczy, ze gracz wykonal swoj ruch
     * @param move Int[] wiersz i kolumna.
     * @return True jesli udalo sie wykonac ruch, w przeciwnym razie falsz
     */
    public boolean attemptMove(int[] move) 
    {
        if (isBlank(move)) 
        {
            addMark(move[0], move[1], activePlayer.getMark());
            activePlayer.setHasPlayed(true);
            return true;
        } 
        return false;
    }

    /**
     * Zwraca tablice 2d char aktualnej planszy
     */
    public char[][] getBoardArr() 
    { 
        return board.getBoard(); 
    }
    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }
    public Player getActivePlayer() { return activePlayer; }
    public void setActivePlayer(Player activePlayer) { this.activePlayer = activePlayer; }
}