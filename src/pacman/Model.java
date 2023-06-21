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
    private boolean Pac_alive = true;
    public final int TILE_SIZE = 24;
    public final int N_TILES = 15;
    private final int SCREEN_SIZE = N_TILES * TILE_SIZE;

    private final int PACMAN_VEL = 6;

    private int lives, score;
    //private Image heart_img, Linky_img, Pinky_img, Inky_img, Clyde_img;
    private Image heart_img;
    private Image up, down, left, right; //obrazki do ruchu w kazda strone
    private int pacman_x, pacman_y, pacman_dx, pacman_dy;
    private int req_dx, req_dy;

    private final int valid_vels[] = {1,2,3,4,5,6,8};
    private final int max_speed = 6;
    private int cur_speed = 3;
    private short [] screen_data;
    private Timer timer; //swing class
    private Ghosts ghosts = new Ghosts();

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
        down = new ImageIcon("C:/Users/jacek/IdeaProjects/Pac Man/src/imgs/down.gif").getImage();
        left = new ImageIcon("C:/Users/jacek/IdeaProjects/Pac Man/src/imgs/left.gif").getImage();
        right = new ImageIcon("C:/Users/jacek/IdeaProjects/Pac Man/src/imgs/right.gif").getImage();
        up = new ImageIcon("C:/Users/jacek/IdeaProjects/Pac Man/src/imgs/up.gif").getImage();
        heart_img = new ImageIcon("C:/Users/jacek/IdeaProjects/Pac Man/src/imgs/heart.png").getImage();
        ghosts.load_ghost_imgs();
    }

    private void var_init(){
        screen_data = new short[N_TILES * N_TILES];
        dim = new Dimension(400,400);
        //ghosts = new Ghosts();

        timer = new Timer(40, this);
        timer.start();
    }


    private void playGame(Graphics2D g2d){
        if(Pac_alive==false){
            death();
        }
        else{
            move_pac();
            draw_pac(g2d);
            ghosts.move_ghosts(g2d, screen_data, TILE_SIZE, N_TILES, pacman_x, pacman_y, Running, Pac_alive);
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
            if (cur_speed < max_speed) {
                cur_speed++;
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



    public void move_pac(){
        int pos;
        short ch;
        if(pacman_x%TILE_SIZE==0 && pacman_y%TILE_SIZE==0){
            pos = pacman_x/TILE_SIZE + N_TILES * (int) (pacman_y/TILE_SIZE);
            ch = screen_data[pos];
            if((ch & 16) !=0){
                screen_data[pos]= (short) (ch & 15);
                score++;
            }

            if(req_dx != 0 || req_dy != 0){
                if(!((req_dx==-1 && req_dy==0 && (ch&1)!=0) || (req_dx==1 && req_dy==0 && (ch&4) !=0) || (req_dx==0 && req_dy==-1 && (ch&2) !=0) || (req_dx==0 && req_dy==1 && (ch&8) !=0))) {
                    pacman_dx=req_dx;
                    pacman_dy=req_dy;
                }
            }

            if((pacman_dx==-1 && pacman_dy==0 && (ch&1) != 0) || (pacman_dx==1 && pacman_dy == 0 && (ch&4)!=0) || (pacman_dx==0 && pacman_dy == -1 && (ch&2)!=0) || (pacman_dx==0 && pacman_dy == 1 && (ch&8)!=0)){
                pacman_dx=0;
                pacman_dy=0;
            }
        }
        pacman_x = pacman_x + PACMAN_VEL*pacman_dx;
        pacman_y = pacman_y + PACMAN_VEL*pacman_dy;
    }

    public void draw_pac(Graphics2D g2d){
        if (req_dx == -1) {
            g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
            g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
            g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
            g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
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
        cur_speed=3;
    }

    private void init_level(){
        for(int i=0; i<N_TILES*N_TILES; i++){
            screen_data[i]=level_data[i];
        }
    }


    private void continue_level() {

        int dx = 1;
        int random;

        pacman_x = 7 * TILE_SIZE;  //start position
        pacman_y = 11 * TILE_SIZE;
        pacman_dx = 0;	//reset direction move
        pacman_dy = 0;
        req_dx = 0;		// reset direction controls
        req_dy = 0;
        Pac_alive = true;
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
