package pacman;

import javax.swing.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main extends JFrame {
    public Main(){
        add(new Model());
    }
    public static void main(String[] args) {
        Main pacman = new Main();
        pacman.setVisible(true);
        pacman.setTitle("Pacman");
        pacman.setSize(380,420);
        pacman.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pacman.setLocationRelativeTo(null);
    }
}