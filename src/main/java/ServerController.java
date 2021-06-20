import java.io.IOException;
import java.net.SocketException;

/**
 * Klasa do kontroli logiki gry z ServerModel oraz komunikacji pomiedzy graczami
 * Rozszerza runnable
 */
public class ServerController implements Runnable {

    /**
     * Zmienna dla modelu odpowiedzialna za logike gry po stronie serwera
     */
    private ServerModel model;

    /**
     * Inicjowanie bazy danych i polaczenia do niej
     */
    private Database database = new Database();


    /**
     * Tworzenie jednego watku gry odpowiedzialnego za zarzadzanie rozgrywka
     * pomiedzy dwoma graczami. Gracz 2 jest ustawiony jako aktywny -
     * ruch rozpoczyna sie od podmiany aktywnych graczy.
     *
     * Dodajemy gracza do bazy i jednoczesnie ustawiamy ID dla niego po stronie serwera
     *
     * Zapisujemy gre do bazy korzystajac z dostarczonych informacji z modelu jednoczesnie
     * ustawiajac jej ID po stronie serwera
     *
     * @param p1 Gracz 1
     * @param p2 Gracz 2
     */
    public ServerController(Player p1, Player p2) {
        this.model = new ServerModel();

        p1.setID(database.pushPlayer(p1.getName()));
        p2.setID(database.pushPlayer(p2.getName()));

        model.setID(database.pushGame(p1.getID(), p1.getMark(), p2.getID(), p2.getMark()));

        model.setActivePlayer(p2);
        model.setIdlePlayer(p1);

    }

    /**
     * Metoda przy rozszerzaniu Runnable, jest wykonywana w petli do zakonczenia watku
     */
    @Override
    public void run() {
        try {
            //znaleziono dwoch graczy, startujemy w grze
            notifyGameStart();
            //dopoki gra nie jest zakonczona, rozgrywaj ruch
            while (!model.hasEnded()) {
                playTurn();
            }
            //gra skonczona -> update stanu na 1
            database.updateGameStatus(model.getID(), 1);
            //obsluga zakonczenia gry
            handleGameEnd();
        } catch (IOException e) {
            //komunikacja zostala przerwana -> update stanu na -1
            database.updateGameStatus(model.getID(), -1);
            //obsluga zerwania polaczenia
            handleOpponentDisconnect();
        }
    }

    /**
     * Powiadomienie obu graczy, ze gra za moment sie rozpocznie.
     * Po komunikacie 2,5 sekundy przerwy aby dac sie przygotowac.
     */
    private void notifyGameStart() {
        System.out.println("Gra rozpoczeta: " + model.getIdlePlayer().getName() +
                " vs " + model.getActivePlayer().getName() + ".");
        model.getActiveSocketOut().println("Znaleziono przeciwnika. Start gry...");
        model.getIdleSocketOut().println("Znaleziono przeciwnika. Start gry...");
        sleep(2500);
    }

    /**
     * Pauzowanie watkow na okreslona liczbe milisekund
     *
     * @param milliseconds liczba milisekund na ile pauzowac
     */
    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Obsluga jednego ruchu gry
     *
     * @throws SocketException obsluga rozlaczenia gracza
     */
    private void playTurn() throws SocketException {
        //podmiana aktywnego gracza
        model.toggleActivePlayer();
        //wyslanie stanu gry do graczy
        broadcastGameState();
        //oczekiwanie na stan gry od aktywnego gracza
        waitingForGameStateUpdate();
    }

    /**
     * Wysylanie najswiezszej wersji gry do obu graczy
     *
     * @throws SocketException obsluga wyjscia gracza
     */
    private void broadcastGameState() throws SocketException {
        try {
            //utworzenie nowego stanu gry (aktualna plansza, aktualny gracz)
            GameState gameState = new GameState(model.getBoard(), model.getActivePlayer(), model.getLastMove());
            //wyslanie modelu do aktywnego gracza
            model.getActiveOutputStream().writeObject(gameState);
            //wyslanie modelu do oczekujacego gracza
            model.getIdleOutputStream().writeObject(gameState);
        } catch (IOException e) {
            throw new SocketException();
        }
    }

    /**
     * Oczekiwanie na stan gry po ruchu aktywnego gracza a nastepnie podmiana
     * stanu gry po stronie serwera
     *
     * @throws SocketException obsluga wyjscia gracza
     */
    private void waitingForGameStateUpdate() throws SocketException {
        try {
            //oczekiwanie na stan gry od aktywnego gracza - pauzowanie komunikacji
            GameState gameState = (GameState) model.getActiveInputStream().readObject();

            //roznica miedzy stanem gry po stronie serwera a aktualnym stanem gry
            //ustalenie jaki ruch zostal wykonany
            String lastMove = compare(gameState.getBoard(), model.getBoard());

            //zapisanie ruchu do bazy
            database.pushMove(model.getID(), lastMove);

            lastMove = model.getActivePlayer().getName()+": "+lastMove;
            //nadpisanie ostatniego ruchu w stanie gry
            model.setLastMove(lastMove);

            //podmiana stanu gry po stronie serwera
            model.setBoard(gameState.getBoard());

            //gameState.getBoard().display();
        } catch (IOException e) {
            throw new SocketException();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obsluga zakonczenia rozgrywki.
     * Wyslanie stanu gry do graczy i wyswietlenie informacji o wyniku rozgrywki po stronie serwera
     *
     * @throws SocketException obsluga rozlaczenia gracza
     */
    private void handleGameEnd() throws SocketException {
        try {
            broadcastGameState();
            System.out.println("Gra " + model.getIdlePlayer().getName() + " vs. " +
                    model.getActivePlayer().getName() + " zakonczona. " + model.getActivePlayer().getName() + " wygral!");
        } catch (IOException e) {
            throw new SocketException();
        }
    }

    /**
     * Wyswietlenie informacji o wyjsciu gracza po stronie graczy i serwera
     */
    private void handleOpponentDisconnect() {
        System.out.println("Jeden z graczy wyszedl. Koniec gry: " +
                model.getIdlePlayer().getName() + " vs. " + model.getActivePlayer().getName() + ".");
        model.getActiveSocketOut().println("Twoj przeciwnik wyszedl, koniec gry.");
        model.getIdleSocketOut().println("Twoj przeciwnik wyszedl, koniec gry.");
    }

    /**
     * Metoda do porownania stanu gry na serwerze do stanu gry otrzymanego od klienta.
     * Dzieki niej mozemy odczytac jaki ruch zostal wykonany przez gracza
     *
     * @param board1 - plansza gry u klienta
     * @param board2 - plansza gry na serwerze
     * @return ruch w postaci wiersz,kolumna,znak
     */
    public String compare(Board board1, Board board2) {
        char[][] clientBoard = board1.getBoard();
        char[][] serverBoard = board2.getBoard();
        String diff = null;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (clientBoard[i][j] != serverBoard[i][j])
                    diff = i + "," + j + "," + model.getActivePlayer().getMark() + ",";
            }
        }
        return diff;
    }
}