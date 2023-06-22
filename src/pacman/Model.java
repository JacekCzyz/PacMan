package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Model extends JPanel implements ActionListener {
    private Dimension dim;  //swing class
    private final Font font = new Font("Arial", Font.BOLD, 14);
    private boolean Running = false;
    private boolean ghost_on_their_way = false;
    public final int TILE_SIZE = 24;
    public final int N_TILES = 15;
    private final int SCREEN_SIZE = N_TILES * TILE_SIZE;
    private int lives, score;
    //private Image heart_img, Linky_img, Pinky_img, Inky_img, Clyde_img;
    private Image heart_img;
    private int req_dx, req_dy;
    private final int max_speed = 3;
    public int speed = 3;
    private short [] screen_data;
    private Timer timer; //swing class
    private Ghosts ghosts = new Ghosts();
    private PacGuy pac_person = new PacGuy();
    private final short level_data[] = {    //0-sciana, 1-lewa granica, 2-gorna, 4-prawa, 8-dolna, 16-kropka do zjedzenia, granice przy scianach tez sie licza
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 28, 0,  17, 16, 16, 16, 16, 16, 16, 16, 20,
            0,  0,  0,  0,  0,  0,  17, 16, 16, 16, 16, 16, 16, 16, 20,
            19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,  0,  21,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,  0,  21,
            17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,  0,  21,
            17, 16, 16, 20, 0,  17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
            17, 24, 24, 28, 0,  25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
            21, 0,  0,  0,  0,  0,  0,  0,  17, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 22, 0,  19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0,  17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0,  17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28,

    };

    public Model(){
        load_imgs();
        var_init();
        addKeyListener(new TAdapter());
        setFocusable(true);
        init_game();
    }

    private  void load_imgs(){
        pac_person.load_pac_images();
        heart_img = new ImageIcon("src/imgs/heart.png").getImage();
        ghosts.load_ghost_imgs();
    }

    private void var_init(){
        screen_data = new short[N_TILES * N_TILES];
        dim = new Dimension(400,400);
        timer = new Timer(40, this);
        timer.start();
    }


    private void playGame(Graphics2D g2d){
        boolean start=false;
        if(pac_person.Pac_alive==false){
            death();
        }
        else{
            if(ghost_on_their_way==false) {
                ghosts.startGhostThreads(g2d, screen_data, pac_person, Running);
                ghost_on_their_way=true;
            }
            ghosts.drawGhosts(g2d);
            score = pac_person.move_pac(req_dx, req_dy, screen_data, score);
            pac_person.draw_pac(g2d, req_dx, req_dy);
            check_maze();

        }
    }

    public void showIntroScreen(Graphics2D g2d){
        String start = "press space to start";
        g2d.setColor(Color.YELLOW);
        g2d.drawString(start, SCREEN_SIZE/4, 150);
    }

    public void drawScore(Graphics2D g2d){
        g2d.setFont(font);
        g2d.setColor(new Color(5,151,79));
        String s = "Score:" + score;
        g2d.drawString(s, SCREEN_SIZE/2+96, SCREEN_SIZE+16);

        for(int i=0; i<lives; i++){
            g2d.drawImage(heart_img, i*28+8, SCREEN_SIZE+1, this);
        }
    }

    public void check_maze(){
        int i=0;
        boolean finished = true;
        while(i<N_TILES*N_TILES && finished){
            if((screen_data[i]) !=0) {
                finished = false;
            }
            i++;
        }
        if (finished) {
            score += 50;
            if (speed < max_speed) {
                speed++;
            }
            init_level();
        }
    }


    private void death(){
        lives--;
        if (lives==0){
            Running=false;
        }
        continue_level();
    }




    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += TILE_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += TILE_SIZE) {

                g2d.setColor(new Color(0,72,251));
                g2d.setStroke(new BasicStroke(5));

                if ((level_data[i] == 0)) {
                    g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                }

                if ((screen_data[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + TILE_SIZE - 1);
                }

                if ((screen_data[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + TILE_SIZE - 1, y);
                }

                if ((screen_data[i] & 4) != 0) {
                    g2d.drawLine(x + TILE_SIZE - 1, y, x + TILE_SIZE - 1,
                            y + TILE_SIZE - 1);
                }

                if ((screen_data[i] & 8) != 0) {
                    g2d.drawLine(x, y + TILE_SIZE - 1, x + TILE_SIZE - 1,
                            y + TILE_SIZE - 1);
                }

                if ((screen_data[i] & 16) != 0) {
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }

                i++;
            }
        }
    }


    private void init_game(){
        lives = 3;
        score = 0;
        init_level();
        //N_GHOSTS=6;
        speed=3;
    }

    private void init_level(){
        for(int i=0; i<N_TILES*N_TILES; i++){
            screen_data[i]=level_data[i];
        }
        continue_level();
    }


    private void continue_level() {

        int dx = 1;
        ghosts.ghost_set_on_level(dx, speed);
        pac_person.pacguy_set_on_level();

        req_dx = 0;		// reset direction controls
        req_dy = 0;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, dim.width, dim.height);

        drawMaze(g2d);
        drawScore(g2d);

        if (Running) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }


    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (Running) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    Running = false;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    Running = true;
                    init_game();
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
