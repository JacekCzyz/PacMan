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
    private final int TILE_SIZE = 24;
    private final int N_TILES = 15;
    private final int SCREEN_SIZE = N_TILES * TILE_SIZE;
    private final int MAXN_GHOSTS = 12;
    private final int PACMAN_VEL = 6;

    private int N_GHOSTS = 6;
    private int lives, score;
    private int [] dx, dy;
    private int [] ghost_x, ghost_y, ghost_dx, ghost_dy, ghost_vel;
    //private Image heart_img, Linky_img, Pinky_img, Inky_img, Clyde_img;
    private Image heart_img, ghost_img;
    private Image up, down, left, right; //obrazki do ruchu w kazda strone
    private int pacman_x, pacman_y, pacman_dx, pacman_dy;
    private int req_dx, req_dy;

    private final int valid_vels[] = {1,2,3,4,5,6,8};
    private final int max_speed = 6;
    private int cur_speed = 3;
    private short [] screen_data;
    private Timer timer; //swing class

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
        down = new ImageIcon("/imgs/down.gif").getImage();
        left = new ImageIcon("/imgs/left.gif").getImage();
        right = new ImageIcon("/imgs/right.gif").getImage();
        up = new ImageIcon("/imgs/up.gif").getImage();
        ghost_img = new ImageIcon("/imgs/ghost.gif").getImage();
        heart_img = new ImageIcon("/imgs/heart.png").getImage();
//        down = new ImageIcon("C:\\Users\\jacek\\IdeaProjects\\Pac Man\\imgs\\down.gif").getImage();
//        left = new ImageIcon("C:\\Users\\jacek\\IdeaProjects\\Pac Man\\imgs\\left.gif").getImage();
//        right = new ImageIcon("C:\\Users\\jacek\\IdeaProjects\\Pac Man\\imgs\\right.gif").getImage();
//        up = new ImageIcon("C:\\Users\\jacek\\IdeaProjects\\Pac Man\\imgs\\up.gif").getImage();
//        ghost_img = new ImageIcon("C:\\Users\\jacek\\IdeaProjects\\Pac Man\\imgs\\ghost.gif").getImage();
//        heart_img = new ImageIcon("C:\\Users\\jacek\\IdeaProjects\\Pac Man\\imgs\\heart.png").getImage();
    }

    private void var_init(){
        screen_data = new short[N_TILES * N_TILES];
        dim = new Dimension(400,400);
        ghost_x = new int[MAXN_GHOSTS];
        ghost_dx = new int [MAXN_GHOSTS];
        ghost_y = new int[MAXN_GHOSTS];
        ghost_dy = new int[MAXN_GHOSTS];
        ghost_vel = new int[MAXN_GHOSTS];
        dx=new int[4];
        dy=new int[4];

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
            move_ghosts(g2d);
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

            if (N_GHOSTS < MAXN_GHOSTS) {
                N_GHOSTS++;
            }
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


    private void move_ghosts(Graphics2D g2d) {

        int pos;
        int count;

        for (int i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % TILE_SIZE == 0 && ghost_y[i] % TILE_SIZE == 0) {
                pos = ghost_x[i] / TILE_SIZE + N_TILES * (int) (ghost_y[i] / TILE_SIZE);

                count = 0;

                if ((screen_data[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screen_data[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screen_data[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghost_vel[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghost_vel[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && Running) {

                Pac_alive = false;
            }
        }
    }

    public void drawGhost(Graphics2D g2d, int x, int y){
        g2d.drawImage(ghost_img, x, y, this);
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
        N_GHOSTS=6;
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

        for (int i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * TILE_SIZE; //start position
            ghost_x[i] = 4 * TILE_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (cur_speed + 1));

            if (random > cur_speed) {
                random = cur_speed;
            }

            ghost_vel[i] = valid_vels[random];
        }

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
