import java.io.Serializable;

/**
 * Klasa do reprezentacji planszy gry.
 */
public class Board implements Serializable {

    /**
     * SerialVersionUID do serializacji.
     */
    private static final long serialVersionUID = -4309518207161622889L;
    /**
     * Tablica 3x3 do reprezentacji planszy.
     */
    private char[][] board;
    /**
     * Liczba wykonanych dotychczas ruchow.
     */
    private int movesNumber;

    /**
     * Uzupelnienie tablicy pustymi znakami, movesNUmber na 0
     */
    public Board() {
        movesNumber = 0;
        board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    /**
     * Sprawdzenie czy pole jest puste
     *
     * @param move Int[] - wiersz, kolumna.
     * @return True jesli puste, w przeciwnym razie false
     */
    public boolean isBlank(int[] move) {
        return board[move[0]][move[1]] == ' ';
    }

    /**
     * Metoda do wyswietlania gry w formie
     * --- -> XOX
     * --- -> OXO
     * --- -> X--
     */
    public void display() {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ')
                    System.out.print("-");
                else {
                    System.out.print(board[i][j]);
                }

            }
            System.out.println("");
        }
        System.out.println("Liczba ruchow: " + movesNumber);

    }

    /**
     * Sprawdzenie czy plansza jest pelna movesNumber >= 9.
     *
     * @return True jesli pelna
     */
    public boolean isFull() {
        return movesNumber >= 9;
    }

    /**
     * Sprawdzenie czy nastapila wygrana
     *
     * @return True jesli wygrana, w przeciwnym razie falsz
     */
    public boolean hasWon() {
        return isHorizontalWin() || isVerticalWin() || isDiagonalWin();
    }

    /**
     * Sprawdzenie czy gra jest skonczona (wygrana albo plansza pelna)
     *
     * @return True jesli skonczona, w przeciwnym razie falsz
     */
    public boolean hasEnded() {
        return hasWon() || isFull();
    }

    /**
     * Stawianie znaku w wybranym miejscu + zwiekszanie liczby ruchow
     *
     * @param row  wiersz.
     * @param col  kolumna.
     * @param mark znak gracza (X lub O)
     */
    public void addMark(int row, int col, char mark) {
        board[row][col] = mark;
        movesNumber++;
    }


    /**
     * Walidacja wygranej horyzontalnej
     *
     * 0O-
     * XXX
     * ---
     *
     * @return True jesli tak, w przeciwnym razie false
     */
    private boolean isHorizontalWin() {
        for (int row = 0; row < 3; row++) {

            //poruszamy sie po wierszach gora - dol
            // trzecie pole nie jest puste aby wyeliminowac sytuacje (XX-)
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2] && board[row][2] != ' ') {
                return true;
            }
        }
        return false;
    }

    /**
     * Walidacja wygranej wertykalnej
     *
     * @return True jesli tak, w przeciwnym razie false
     */
    private boolean isVerticalWin() {
        for (int col = 0; col < 3; col++) {

            //poruszamy sie po kolumnach lewa - prawa
            // trzecie pole nie jest puste aby wyeliminowac sytuacje
            // -X-
            // -X-
            // ---
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col] && board[2][col] != ' ') {
                return true;
            }
        }
        return false;
    }

    /**
     * Walidacja wygranej po przekatnej.
     *
     * @return True jesli tak, w przeciwnym razie false
     */
    private boolean isDiagonalWin() {
        // X--
        // -X-
        // --X
        if (board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] != ' ')
            return true;

        // --X
        // -X-
        // X--
        else if (board[2][0] == board[1][1] && board[2][0] == board[0][2] && board[2][0] != ' ')
            return true;
        //niespelniony warunek
        else
            return false;
    }

    public char[][] getBoard() {
        return board;
    }
}