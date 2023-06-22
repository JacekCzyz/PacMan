package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;

public class Ghosts extends JPanel {
    private final int MAXN_GHOSTS = 6;
    private int N_GHOSTS = 6;
    private int [] ghost_x, ghost_y, ghost_dx, ghost_dy, ghost_vel;
    private Image ghost_img;

    private int [] dx, dy;

    public final int TILE_SIZE = 24;
    public final int N_TILES = 15;
    private Thread[] ghostThreads;
    private PacGuy pac_person = new PacGuy();



    public Ghosts(){
        ghost_x = new int[MAXN_GHOSTS];
        ghost_dx = new int [MAXN_GHOSTS];
        ghost_y = new int[MAXN_GHOSTS];
        ghost_dy = new int[MAXN_GHOSTS];
        ghost_vel = new int[MAXN_GHOSTS];
        dx=new int[4];
        dy=new int[4];
        ghostThreads = new Thread[N_GHOSTS];
    }

    private Lock lock = new ReentrantLock();


    public void ghost_set_on_level(int dx, int random, int cur_speed, int[] valid_vels){

        for (int i = 0; i < N_GHOSTS; i++) {
            ghost_y[i] = 4 * TILE_SIZE; //start position
            ghost_x[i] = 4 * TILE_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;

            if (random > cur_speed) {
                random = cur_speed;
            }

            ghost_vel[i] = valid_vels[random];
        }
    }

    public  void load_ghost_imgs(){
        ghost_img = new ImageIcon("src/imgs/ghost.gif").getImage();
    }

    public void startGhostThreads(Graphics2D g2d, short [] screen_data, PacGuy pacman, boolean Running){
        /*final int[] ghostIndex = {0, 1, 2, 3, 4, 5};
        //for (int i = 0; i < N_GHOSTS; i++) {
        for(int current: ghostIndex){
            //int current = ghostIndex[i];
            ghostThreads[current] = new Thread(() -> move_ghosts(current, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running));
            ghostThreads[current].start();
        }*/
        ghostThreads[0] = new Thread(() -> move_ghosts(0, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running));
        ghostThreads[0].start();
        ghostThreads[1] = new Thread(() -> move_ghosts(1, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running));
        ghostThreads[1].start();
        ghostThreads[2] = new Thread(() -> move_ghosts(2, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running));
        ghostThreads[2].start();
        ghostThreads[3] = new Thread(() -> move_ghosts(3, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running));
        ghostThreads[3].start();
        ghostThreads[4] = new Thread(() -> move_ghosts(4, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running));
        ghostThreads[4].start();
        ghostThreads[5] = new Thread(() -> move_ghosts(5, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running));
        ghostThreads[5].start();
    }

//    public void move_ghosts(int ghostIndex, Graphics2D g2d, short[] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
//        int pacman_x = pacman.get_x();
//        int pacman_y = pacman.get_y();
//        boolean Pac_alive = pacman.Pac_alive;
//        boolean hitWall = false; // Flaga oznaczająca zderzenie z ścianą
//        move_ghosts(g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running);
//    }



    /*public void move_ghosts2(Graphics2D g2d, short[] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
        int pos;
        int count;

        int pacman_x = pacman.get_x();
        int pacman_y = pacman.get_y();
        boolean Pac_alive = pacman.Pac_alive;
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

                pacman.set_alive(false);
            }
        }

    }*/




//to jest najlepsze co mamy
public void move_ghosts(int ghostIndex, Graphics2D g2d, short [] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
    int pacman_x = pacman.get_x();
    int pacman_y = pacman.get_y();
    boolean Pac_alive = pacman.Pac_alive;
    while (true) {
        lock.lock();
        try {
            if (ghost_x[ghostIndex] % TILE_SIZE == 0 && ghost_y[ghostIndex] % TILE_SIZE == 0) {
                int pos = ghost_x[ghostIndex] / TILE_SIZE + N_TILES * (int) (ghost_y[ghostIndex] / TILE_SIZE);
                int count = 0;

                if ((screen_data[pos] & 1) == 0 && ghost_dx[ghostIndex] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 2) == 0 && ghost_dy[ghostIndex] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screen_data[pos] & 4) == 0 && ghost_dx[ghostIndex] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 8) == 0 && ghost_dy[ghostIndex] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {
                    if ((screen_data[pos] & 15) == 15) {
                        ghost_dx[ghostIndex] = 0;
                        ghost_dy[ghostIndex] = 0;
                    } else {
                        ghost_dx[ghostIndex] = -ghost_dx[ghostIndex];
                        ghost_dy[ghostIndex] = -ghost_dy[ghostIndex];
                    }
                } else {
                    count = (int) (Math.random() * count);
                    if (count > 3) {
                        count = 3;
                    }
                    ghost_dx[ghostIndex] = dx[count];
                    ghost_dy[ghostIndex] = dy[count];
                }
            }

            ghost_x[ghostIndex] = ghost_x[ghostIndex] + (ghost_dx[ghostIndex] * ghost_vel[ghostIndex]);
            ghost_y[ghostIndex] = ghost_y[ghostIndex] + (ghost_dy[ghostIndex] * ghost_vel[ghostIndex]);

            drawGhosts(g2d);

            //drawGhost(g2d, ghost_x[ghostIndex] + 1, ghost_y[ghostIndex] + 1);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (pacman_x > (ghost_x[ghostIndex] - 12) && pacman_x < (ghost_x[ghostIndex] + 12)
                    && pacman_y > (ghost_y[ghostIndex] - 12) && pacman_y < (ghost_y[ghostIndex] + 12)
                    && Running) {

                pacman.set_alive(false);
            }
        } finally {
            lock.unlock();
        }
    }
}


    /*public boolean move_ghosts(int ghostIndex, Graphics2D g2d, short [] screen_data, int TILE_SIZE, int N_TILES, int pacman_x, int pacman_y, boolean Running, boolean Pac_alive) {
        while (true) {
            if (ghost_x[ghostIndex] % TILE_SIZE == 0 && ghost_y[ghostIndex] % TILE_SIZE == 0) {
                int pos = ghost_x[ghostIndex] / TILE_SIZE + N_TILES * (int) (ghost_y[ghostIndex] / TILE_SIZE);
                int count = 0;

                if ((screen_data[pos] & 1) == 0 && ghost_dx[ghostIndex] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 2) == 0 && ghost_dy[ghostIndex] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screen_data[pos] & 4) == 0 && ghost_dx[ghostIndex] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 8) == 0 && ghost_dy[ghostIndex] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {
                    if ((screen_data[pos] & 15) == 15) {
                        ghost_dx[ghostIndex] = 0;
                        ghost_dy[ghostIndex] = 0;
                    } else {
                        ghost_dx[ghostIndex] = -ghost_dx[ghostIndex];
                        ghost_dy[ghostIndex] = -ghost_dy[ghostIndex];
                    }
                } else {
                    count = (int) (Math.random() * count);
                    if (count > 3) {
                        count = 3;
                    }
                    ghost_dx[ghostIndex] = dx[count];
                    ghost_dy[ghostIndex] = dy[count];
                }
            }

            ghost_x[ghostIndex] = ghost_x[ghostIndex] + (ghost_dx[ghostIndex] * ghost_vel[ghostIndex]);
            ghost_y[ghostIndex] = ghost_y[ghostIndex] + (ghost_dy[ghostIndex] * ghost_vel[ghostIndex]);

            drawGhost(g2d, ghost_x[ghostIndex] + 1, ghost_y[ghostIndex] + 1);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (pacman_x > (ghost_x[ghostIndex] - 12) && pacman_x < (ghost_x[ghostIndex] + 12)
                    && pacman_y > (ghost_y[ghostIndex] - 12) && pacman_y < (ghost_y[ghostIndex] + 12)
                    && Running) {

                Pac_alive = false;
            }
        }
        return Pac_alive;
    }*/


   // public void drawGhost(Graphics2D g2d, int x, int y){
   public void drawGhosts(Graphics2D g2d) {
       //lock.lock();
       try {
           for (int i = 0; i < ghost_x.length; i++)
               g2d.drawImage(this.ghost_img, ghost_x[i], ghost_y[i], this);
       } finally {
           //lock.unlock();
       }
   }
}
