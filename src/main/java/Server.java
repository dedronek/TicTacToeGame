import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Klasa serwera odpowiedzialnego za komunikace pomiedzy klientem
 * a watkiem serwera gry obslugiwanym przez ServerController
 */
public class Server {

    /**
     * Socket do polaczen dla klientow
     */
    private ServerSocket serverSocket;
    /**
     * Pisanie do klienta.
     */
    private PrintWriter out;
    /**
     * Czytanie od klienta.
     */
    private BufferedReader in;
    /**
     * Pula watkow w celu uruchomienia wielu instancji gry dla klientow
     */
    private ExecutorService pool;
    /**
     * Losowanie znaku dla gracza, pierwszy gracz otrzymuje X
     */
    private char randomMark;

    /**
     * Tworzenie serwera obslugujacego 6 rownoleglych instancji gry
     */
    public Server() {
        try {
            randomMark = 'X';
            serverSocket = new ServerSocket(1122);
            pool = Executors.newFixedThreadPool(6);
            System.out.println("Serwer nasluchuje na porcie " + serverSocket.getLocalPort() + "...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda starujaca serwer, w nieskonczonej petli dobiera graczy w dwojki
     * aby mogli rozegrac gre.
     */
    public void runServer() {
        try {
            while (true) {
                findPlayers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeServer();
        }
    }

    /**
     * Rozpoczecie nowej instancji gry.
     * Po podlaczeniu dwoch klientow i wyslaniu ich nickow na serwer
     * gra jest inicjalizowana i startowana w puli watkow
     */
    private void findPlayers() throws IOException {
        Player p1 = getPlayer();
        Player p2 = getPlayer();

        pool.execute(new ServerController(p1, p2));
    }

    /**
     * Obsluga podlaczenia klientow i utworzenia obiektu Player
     *
     * @return Player gotowy obiekt do podlaczenia do gry
     */
    private Player getPlayer() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter((socket.getOutputStream()), true);
                Player player = new Player(socket, in.readLine(), getPlayerMark());
                toggleMark();
                System.out.println(player.getName() + " polaczony.");
                return player;
            } catch (IOException e) {
                System.out.println("Problem z polaczeniem do serwera...");
            }
        }
    }

    /**
     * Zmiana znaku dla drugiego gracza
     */
    private void toggleMark() {
        if (randomMark == 'X')
            randomMark = 'O';
        else
            randomMark = 'X';
    }

    /**
     * Zamykanie polaczenia z klientem
     */
    private void closeServer() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Startowanie serwera
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.runServer();
    }

    private char getPlayerMark() {
        return randomMark;
    }
}