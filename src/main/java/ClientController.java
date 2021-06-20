import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Klasa dba o komunikacje pomiedzy logika z ClientModel oraz GUI z ClientView
 * Jest rowniez odpowiedzialna za komunikacje z serwerem
 */
public class ClientController {
    /**
     * Socket do komunikacji z serwerem.
     */
    private Socket socket;
    /**
     * Wysylanie do serwera.
     */
    private PrintWriter out;
    /**
     * Czytanie z serwera.
     */
    private BufferedReader in;
    /**
     * Czytanie z serwera serializowanych obiektow.
     */
    private ObjectInputStream objectIn;
    /**
     * Wysylanie do serwera serializowanych obiektow.
     */
    private ObjectOutputStream objectOut;
    /**
     * GUI.
     */
    private ClientView view;
    /**
     * Kontroler gry po stronie klienta.
     */
    private ClientModel model;
    /**
     * Do sprawdzania czy znaleziono przeciwnika do pary aby rozpoczac gre
     * true - znaleziono, rozpoczyna sie gra
     * false - komunikacja wstrzymana, czekamy na przeciwnika
     */
    private boolean isLive;
    /**
     * Nazwa klienta.
     */
    private String name = null;

    /**
     * Konstruktor z podlaczeniem do serwera, inicjalizacja zmiennych
     * po stronie klienta oraz wyswietlenie GUI
     *
     * @param hostName nazwa hosta.
     * @param port numer portu.
     */
    public ClientController(String hostName, int port) {
        connectToServer(hostName, port);
        initClientVariables();
        addButtonFunctionality();

    }

    /**
     * Przygotowanie, przebieg gry oraz obsluzenie rozlaczenia z serwerem
     */
    public void communicate() {
        settingGameUp();
        runGame();
        disconnectFromServer();
    }

    /**
     * Inicjowanie komunikacji pomiedzy serwerem a klientem.
     *
     * @param serverName nazwa hosta.
     * @param portNumber numer portu.
     */
    private void connectToServer(String serverName, int portNumber) {
        try {
            socket = new Socket(serverName, portNumber);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //jesli blad polaczenia, wyswietl komunikat
            String error = e.getMessage();
            JOptionPane.showMessageDialog(null, error);
            System.exit(0);
        }
    }

    /**
     * Inicjalizacja widoku, modelu oraz pobranie nicku
     */
    private void initClientVariables() {
        assignNickname();
        view = new ClientView(name);
        model = new ClientModel();
        //czekamy na drugiego gracza
        isLive = false;
    }

    /**
     * Dodanie listernerów do widoku
     * po ich kliknieciu nastepuje proba wykonania ruchu
     */
    private void addButtonFunctionality() {

        view.Button1Listener((ActionEvent e) ->
        {
            int[] move = {0, 0};
            handleMove(move);
        });
        view.Button2Listener((ActionEvent e) ->
        {
            int[] move = {0, 1};
            handleMove(move);
        });
        view.Button3Listener((ActionEvent e) ->
        {
            int[] move = {0, 2};
            handleMove(move);
        });
        view.Button4Listener((ActionEvent e) ->
        {
            int[] move = {1, 0};
            handleMove(move);
        });
        view.Button5Listener((ActionEvent e) ->
        {
            int[] move = {1, 1};
            handleMove(move);
        });
        view.Button6Listener((ActionEvent e) ->
        {
            int[] move = {1, 2};
            handleMove(move);
        });
        view.Button7Listener((ActionEvent e) ->
        {
            int[] move = {2, 0};
            handleMove(move);
        });
        view.Button8Listener((ActionEvent e) ->
        {
            int[] move = {2, 1};
            handleMove(move);
        });
        view.Button9Listener((ActionEvent e) ->
        {
            int[] move = {2, 2};
            handleMove(move);
        });
    }

    /**
     * Obsluzenie ruchu na planszy. Jesli slot jest zajety,
     * wyswietlenie stosownego komunikatu
     *
     * @param move Int[] wiersz i kolumna.
     */
    private void handleMove(int[] move) {
        if (!model.attemptMove(move)) {
            view.setJournal("Wybierz inne miejsce z planszy.");
        }
    }

    /**
     * Dodanie gracza do gry i oczekiwanie na przeciwnika
     */
    private void settingGameUp() {
        addPlayerToGame();
        waitForGameStart();
    }

    /**
     * Wyswietlenie pola na nick
     * dopoki gracz nie wpisze nicku
     */
    private void assignNickname() {
        while (name == null || name.isEmpty() || name == "") {
            name = JOptionPane.showInputDialog("Wpisz nick:");
        }
    }

    /**
     * Dodanie gracza do gry
     * Wyslanie na serwer nicku gracza
     * Inicjalizacja zmiennych odpowiedzialnych za wymiane obiektow
     */
    private void addPlayerToGame() {
        try {
            view.setActivePlayerName(name);
            //wyslanie nicku na serwer
            out.println(name);
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Oczekiwanie na przeciwnika
     * Jesli serwer wysle informacje ze znalazl drugiego gracza
     * Rozpoczynamy rozgrywke
     */
    private void waitForGameStart() {
        try {
            view.setJournal("Oczekiwanie na przeciwnika...");
            while (!isLive) {
                //oczekiwanie na odpowiedz serwera
                String response = in.readLine();
                if (response.equals("Znaleziono przeciwnika. Start gry...")) {
                    view.setJournal(response);
                    //gra sie rozpoczyna
                    isLive = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uruchomienie gry.
     * 1. Czekamy na stan gry z serwera
     * 2. Po otrzymaniu stanu, aktualizujemy lokalny stan gry (widok klienta)
     * 3. Czekamy na ruch aktywnego gracza, wysylamy stan gry na serwer
     * Jesli stan otrzymanej gry null -> blad komunikacji/przeciwnik opuscil gre
     */
    private void runGame() {
        while (isLive) {
            GameState gameState = recieveGameStateUpdate();
            if (gameState == null) {
                break;
            }
            updateClientGameState(gameState);
            continueGameState(gameState);
        }
    }

    /**
     * Otrzymywanie najswiezszego stanu gry z serwera
     *
     * @return najnowszy stan gry, jesli komunikacja jest prawidlowa
     * w przeciwnym razie null
     */
    private GameState recieveGameStateUpdate() {
        try {
            //oczekiwanie na stan gry
            return (GameState) objectIn.readObject();
        } catch (StreamCorruptedException e) {
            view.setJournal("Twoj przeciwnik wyszedl, koniec gry.");
            //gra wstrzymana
            isLive = false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Aktualizacja stanu gry u klienta aby odzwierciedlal najnowszy
     * stan gry z serwera
     *
     * @param gameState najnowszy stan gry z serwera.
     */
    private void updateClientGameState(GameState gameState) {
        //aktualizacja modelu o nowy ruch
        model.setBoard(gameState.getBoard());
        //aktualizacja modelu o aktywnego gracza
        model.setActivePlayer(gameState.getActivePlayer());
        //aktualizacja widoku planszy o nowy ruch
        view.updateButtonText(model.getBoardArr());
        //zmiana nicku aktywnego gracza
        view.setActivePlayerName(gameState.getActiveName());
        //zmiana znaku, ktory teraz robi ruch
        view.setActiveMark(gameState.getActiveMark());
        //wypisanie ostatniego ruchu
        if (gameState.getLastMove() != null && gameState.getActivePlayer().getName().equals(name))
            view.setJournal(gameState.getLastMove());
    }

    /**
     * Kontynuowanie gry wedlug najnowszego stanu.
     * Jesli gra sie nie skonczyla, procesowanie ruchu
     * Jesli gracz wygral, wyswietlenie gratulacji
     * Jesli remis, wyswietlenie komunikatu
     *
     * @param gameState najnowszy stan gry.
     */
    private void continueGameState(GameState gameState) {
        //dopoki gra nie jest skonczona, procesuj ruchy
        if (!gameState.hasEnded()) {
            processTurn(gameState);
        //jesli gra jest wygrana, obsluz scenariusz wygranej
        } else if (gameState.hasWon()) {
            handleWinEnding(gameState);
       //w przeciwnym razie, obsluz scenariusz remisu
        } else {
            handleTieEnding();
        }
    }

    /**
     * Jesli to ruch klienta, zezwol na klikanie po planszy
     * aby klient mogl zrobic swoj ruch.
     * Jesli to nie ruch klienta, zablokuj plansze i czekaj na ruch przeciwnika.
     *
     * @param gameState najnowszy stan gry z serwera.
     */
    private void processTurn(GameState gameState) {
        //jesli gracz to aktywny gracz
        if (gameState.getActiveName().equals(name)) {
            playTurn(gameState);
        }
        //jesli gracz to nieaktywny gracz
        else {
            //wyswietl stosowny komunikat
            view.setJournal(gameState.getActiveName() + " wykonuje swoj ruch...");
        }
    }

    /**
     * Rozegranie jednej tury.
     * Zezwolenie klientowi na wykonanie ruchu
     * Aktualizacja stanu gry po wykonaniu ruchu
     * Wyslanie zaktualizownae stanu gry na serwer
     *
     * @param gameState najnowszy stan gry z serwer.
     */
    private void playTurn(GameState gameState) {
        setUpTurn();
        model.performOneMove();
        endTurn(gameState);
    }

    /**
     * Informacja o ruchu dla aktywnego klienta, odblokowanie planszy
     */
    private void setUpTurn() {
        view.setJournal("Twój ruch!");
        view.enableButtons(true);
    }

    /**
     * Zakonczenie ruchu przez zablokowanie planszy.
     * Aktualizacja lokalnego stanu gry aby odzwierciedlal wykonany ruch
     * Wyslanie zaktualizowanego stanu gry na serwer
     *
     * @param gameState najnowszy stan gry.
     */
    private void endTurn(GameState gameState) {
        //zablokuj plansze
        view.enableButtons(false);
        //zaktualizuj znaki na planszy
        view.updateButtonText(model.getBoardArr());
        //zaktualizuj stan gry po wykonaniu ruchu
        gameState.setBoard(model.getBoard());
        //ustaw aktualnego gracza
        gameState.setActivePlayer(model.getActivePlayer());
        //wyslij zaktualizowany stan gry na serwer
        sendGameStateUpdate(gameState);
    }

    /**
     * Wyslanie zaktualizowanego stanu gry na serwer
     *
     * @param gameState najswiezszy stan gry.
     */
    private void sendGameStateUpdate(GameState gameState) {
        try {
            objectOut.writeObject(gameState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obsluga wygranej
     * Wyswietlenie wygranego gracza i zakonczenie gry.
     *
     * @param gameState najswiezszy stan gry.
     */
    private void handleWinEnding(GameState gameState) {
        //wyswietlenie informacji o tym ze aktualny gracz wygral
        view.setJournal(gameState.getActiveName() + " wygral!");
        //zakonczenie gry
        isLive = false;
    }

    /**
     * Obsluga remisu
     * Wyswietlenie informacji o remisie.
     */
    private void handleTieEnding() {
        view.setJournal("Mamy remis!");
        isLive = false;
    }

    /**
     * Zakonczenie komunikacji pomiedzy klientem a serwerem.
     * Zamkniecie polaczen.
     */
    private void disconnectFromServer() {
        try {
            in.close();
            out.close();
            objectIn.close();
            objectOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)  {
        ClientController clientController = new ClientController("localhost", 1122);
        clientController.communicate();
    }
}