import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * GUI dla gracza
 */
public class ClientView extends JFrame {
    /**
     * SerialVersionUID do serializacji danych.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Panel z nazwa gry
     */
    private JPanel northPanel;
    /**
     * Panel zawierajacy nick aktywnego gracza oraz jego znak
     */
    private JPanel southPanel;
    /**
     * Pole tekstowe na aktywny znak
     */
    private JTextField activeMark;
    /**
     * Pole tekstowe na nick aktywnego gracza
     */
    private JTextField activePlayerName;
    /**
     * Panel centralny na przyciski uzyte jako plansza do gry
     */
    private JPanel centerPanel;

    //Przyciski w pierwszym rzedzie
    private JButton Button1;
    private JButton Button2;
    private JButton Button3;

    //Przyciski w drugim rzedzie
    private JButton Button4;
    private JButton Button5;
    private JButton Button6;

    //przyciski w trzecim rzedzie
    private JButton Button7;
    private JButton Button8;
    private JButton Button9;

    /**
     * Panel po lewej z polem tekstowym przeznaczonym na komunikaty
     */
    private JPanel westPanel;
    /**
     * Pole tekstowe wyswietlajace stan gry
     */
    private JTextArea journal;
    /**
     * Scroll dla pola tekstowego, potrzebny aby wyswietlic pole,
     * pomoze gdy tekst bedzie za dlugi.
     */
    private JScrollPane scrollPane;

    /**
     * Inicjalizacja zmiennych, kontenera i paneli
     */
    public ClientView(String nickName) {
        super(nickName + " w grze =TicTacToe=");
        initializePanels();
        initializeContainer();
    }

    /**
     * Zmiana stanu przyciskow,
     * umozliwia zagranie tury
     *
     * @param state true jesli wlaczone, false jesli wylaczone.
     */
    public void enableButtons(boolean state) {
        Button2.setEnabled(state);
        Button1.setEnabled(state);
        Button3.setEnabled(state);
        Button4.setEnabled(state);
        Button5.setEnabled(state);
        Button6.setEnabled(state);
        Button7.setEnabled(state);
        Button8.setEnabled(state);
        Button9.setEnabled(state);
    }

    /**
     * Aktualizacja przyciskow na planszy
     *
     * @param board przechowuje model gry w postacji 3x3 tablicy.
     */
    public void updateButtonText(char[][] board) {
        Button1.setText(board[0][0] + "");
        Button2.setText(board[0][1] + "");
        Button3.setText(board[0][2] + "");
        Button4.setText(board[1][0] + "");
        Button5.setText(board[1][1] + "");
        Button6.setText(board[1][2] + "");
        Button7.setText(board[2][0] + "");
        Button8.setText(board[2][1] + "");
        Button9.setText(board[2][2] + "");
    }

    /**
     * Inicjalizacja zmiennych
     */
    private void initializePanels() {
        //Gora
        northPanel = new JPanel();
        northPanel.add(new JLabel("TicTacToe"));

        // Lewa strona
        westPanel = new JPanel();
        journal = new JTextArea(20, 25);
        scrollPane = new JScrollPane(journal);
        westPanel.add(scrollPane);

        // Prawa i srodek
        centerPanel = new JPanel();
        Button1 = new JButton("");
        Button2 = new JButton("");
        Button3 = new JButton("");
        Button4 = new JButton("");
        Button5 = new JButton("");
        Button6 = new JButton("");
        Button7 = new JButton("");
        Button8 = new JButton("");
        Button9 = new JButton("");

        centerPanel.setLayout(new GridLayout(3, 3, 5, 5));
        centerPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        centerPanel.add(Button1);
        centerPanel.add(Button2);
        centerPanel.add(Button3);
        centerPanel.add(Button4);
        centerPanel.add(Button5);
        centerPanel.add(Button6);
        centerPanel.add(Button7);
        centerPanel.add(Button8);
        centerPanel.add(Button9);

        // Dol
        southPanel = new JPanel();
        activeMark = new JTextField(5);
        activePlayerName = new JTextField(10);

        southPanel.add(new JLabel("Teraz ruch:"));
        southPanel.add(activePlayerName);
        southPanel.add(new JLabel("Aktywny znak:"));
        southPanel.add(activeMark);

        // Na start wylaczona mozliwosc gry
        enableButtons(false);
        // Wylaczamy mozliwosc edycji pol tekstowych
        setUneditable();

    }

    /**
     * Ustawianie pol na nieedytowalne
     */
    private void setUneditable() {
        activeMark.setEditable(false);
        activePlayerName.setEditable(false);
        journal.setEditable(false);
    }

    /**
     * Inicjalizacja kontenera.
     */
    private void initializeContainer() {

       Container c = getContentPane();

        // Zatrzymaj program po zamknieciu okna
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Ustawienie rozmiaru okna
        setPreferredSize(new Dimension(600, 400));

        // Nie mozna zmieniac rozmiaru okna
        setResizable(false);

        // Set layout for main window frame
        c.setLayout(new BorderLayout());

        // Umieszczenie paneli w kontenerze
        c.add("North", northPanel);
        c.add("West", westPanel);
        c.add("Center", centerPanel);
        c.add("South", southPanel);

        // Make the frame visible
        setVisible(true);
        pack();
    }

    public void Button1Listener(ActionListener listener) {
        Button1.addActionListener(listener);
    }

    public void Button2Listener(ActionListener listener) {
        Button2.addActionListener(listener);
    }

    public void Button3Listener(ActionListener listener) {
        Button3.addActionListener(listener);
    }

    public void Button4Listener(ActionListener listener) {
        Button4.addActionListener(listener);
    }

    public void Button5Listener(ActionListener listener) {
        Button5.addActionListener(listener);
    }

    public void Button6Listener(ActionListener listener) {
        Button6.addActionListener(listener);
    }

    public void Button7Listener(ActionListener listener) {
        Button7.addActionListener(listener);
    }

    public void Button8Listener(ActionListener listener) {
        Button8.addActionListener(listener);
    }

    public void Button9Listener(ActionListener listener) {
        Button9.addActionListener(listener);
    }

    public void setActiveMark(String text) {
        this.activeMark.setText(text);
    }

    public void setActivePlayerName(String text) {
        this.activePlayerName.setText(text);
    }

    public void setJournal(String text) {
        String gameLog = this.journal.getText();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String currentTime = formatter.format(date);

        if (gameLog.isEmpty())
            this.journal.setText("[" + currentTime + "] " + text);
        else
            this.journal.setText(gameLog + "\n" + "[" + currentTime + "] " + text);
    }
}
