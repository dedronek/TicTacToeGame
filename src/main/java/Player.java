import java.io.*;
import java.net.Socket;

/**
 * Reprezentacja gracza.
 * Zawiera informacje o graczu: ID, nick, znak, sockety
 */
public class Player implements Serializable {

    /**
     * ID do celow bazodanowych.
     */
    private int ID;

    /**
     * Nick gracza
     */
    private String name;
    /**
     * Znak gracza w rozgrywce
     */
    private char mark;
    /**
     * Flaga sprawdzajaca czy gracz wykonal juz swoj ruch
     * true jesil kliknal poprawny klawisz na planszy, serwer przelacza ruch na przeciwnika
     * false jesli nie kliknal, serwer i przeciwnik oczekuja na ruch
     */
    private boolean hasPlayed;
    /**
     * Pisanie do serwera.
     */
    private transient PrintWriter socketOut;
    /**
     * Czytanie z serwera.
     */
    private transient BufferedReader socketIn;
    /**
     * Wysylanie obiektow do serwera
     */
    private transient ObjectInputStream objectIn;
    /**
     * Czytanie obiektow z serwera
     */
    private transient ObjectOutputStream objectOut;
    /**
     * SerialVersionUID do serializacji danych.
     */
    private static final long serialVersionUID = 720785984605791249L;


    /**
     * Tworzymy gracza korzystajac z parametrow uzyskanych przez
     * polaczenie sie z serwerem
     */
    public Player(Socket socket, String name, char mark) {
        try {
            this.name = name;
            this.mark = mark;
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOut = new PrintWriter((socket.getOutputStream()), true);
            objectIn = new ObjectInputStream(socket.getInputStream());
            objectOut = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zatrzymanie gry dopoki poprawny ruch nie zostanie wykonany
     * (puste pole nie zostanie klikniete)
     */
    public void makeMove() {
        hasPlayed = false;
        while (!hasPlayed) {
            try {
                Thread.sleep(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public char getMark() {
        return mark;
    }

    public void setHasPlayed(boolean hasPlayed) {
        this.hasPlayed = hasPlayed;
    }

    public PrintWriter getSocketOut() {
        return socketOut;
    }

    public BufferedReader getSocketIn() {
        return socketIn;
    }

    public ObjectInputStream getObjectIn() {
        return objectIn;
    }

    public ObjectOutputStream getObjectOut() {
        return objectOut;
    }
}