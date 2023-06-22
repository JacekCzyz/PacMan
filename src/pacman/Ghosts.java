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
    private final int MAXN_GHOSTS = 4;
    private int N_GHOSTS = 4;
    private int [] ghost_x, ghost_y, ghost_dx, ghost_dy, ghost_vel;
    private Image ghost_img;

    private int [] dx, dy;

    public final int TILE_SIZE = 24;
    public final int N_TILES = 15;
    private Thread[] ghostThreads;
    private PacGuy pac_person = new PacGuy();
    private final Object[] ghostLocks;


    public Ghosts(){
        ghost_x = new int[MAXN_GHOSTS];
        ghost_dx = new int [MAXN_GHOSTS];
        ghost_y = new int[MAXN_GHOSTS];
        ghost_dy = new int[MAXN_GHOSTS];
        ghost_vel = new int[MAXN_GHOSTS];
        dx=new int[4];
        dy=new int[4];
        ghostThreads = new Thread[N_GHOSTS];
        ghostLocks = new Object[N_GHOSTS]; // Inicjalizacja tablicy blokad dla duchów
        for (int i = 0; i < N_GHOSTS; i++) {
            ghostLocks[i] = new Object(); // Inicjalizacja blokady dla konkretnego ducha
        }
    }



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
/*
        for (int i = 0; i < N_GHOSTS; i++) {
            final int ghostIndex = i;
            ghostThreads[ghostIndex] = new Thread(() -> move_ghosts(ghostIndex, g2d, screen_data, TILE_SIZE, N_TILES, pacman, Running));
            ghostThreads[ghostIndex].start();
        }*/

        ghostThreads[0] = new Thread(() -> {
            System.out.println("Thread ID: " + Thread.currentThread().getId());
            move_ghosts1(0, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running);
        });
        ghostThreads[0].start();
        ghostThreads[1] = new Thread(() -> {
            System.out.println("Thread ID: " + Thread.currentThread().getId());
            move_ghosts2(1, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running);
        });
        ghostThreads[1].start();
        ghostThreads[2] = new Thread(() -> {
            System.out.println("Thread ID: " + Thread.currentThread().getId());
            move_ghosts3(2, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running);
        });
        ghostThreads[2].start();
        ghostThreads[3] = new Thread(() -> {
            System.out.println("Thread ID: " + Thread.currentThread().getId());
            move_ghosts4(3, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running);
        });
        ghostThreads[3].start();



/*        ghostThreads[4] = new Thread(() -> {
            System.out.println("Thread ID: " + Thread.currentThread().getId());
            move_ghosts(4, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running);
        });
        ghostThreads[4].start();
        ghostThreads[5] = new Thread(() -> {
            System.out.println("Thread ID: " + Thread.currentThread().getId());
            move_ghosts(5, g2d, screen_data, TILE_SIZE, N_TILES, pacman ,Running);
        });
        ghostThreads[5].start();*/
    }


//to jest najlepsze co mamy
public void move_ghosts1(int ghostIndex, Graphics2D g2d, short [] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
    int pacman_x = pacman.get_x();
    int pacman_y = pacman.get_y();
    boolean Pac_alive = pacman.Pac_alive;

    while (true) {
        //synchronized (ghostLocks[ghostIndex]) {
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

                //synchronized (this) {
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
            if (pacman_x > (ghost_x[ghostIndex] - 12) && pacman_x < (ghost_x[ghostIndex] + 12)
                    && pacman_y > (ghost_y[ghostIndex] - 12) && pacman_y < (ghost_y[ghostIndex] + 12)
                    && Running) {
                pacman.set_alive(false);
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
       // }
    }
}

    public void move_ghosts2(int ghostIndex, Graphics2D g2d, short [] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
        int pacman_x = pacman.get_x();
        int pacman_y = pacman.get_y();
        boolean Pac_alive = pacman.Pac_alive;

        while (true) {
            //synchronized (ghostLocks[ghostIndex]) {
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
            if (pacman_x > (ghost_x[ghostIndex] - 12) && pacman_x < (ghost_x[ghostIndex] + 12)
                    && pacman_y > (ghost_y[ghostIndex] - 12) && pacman_y < (ghost_y[ghostIndex] + 12)
                    && Running) {
                pacman.set_alive(false);
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
             }
       //}
    }

        public void move_ghosts3(int ghostIndex, Graphics2D g2d, short [] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
        int pacman_x = pacman.get_x();
        int pacman_y = pacman.get_y();
        boolean Pac_alive = pacman.Pac_alive;

        while (true) {
            //synchronized (this) {
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
            if (pacman_x > (ghost_x[ghostIndex] - 12) && pacman_x < (ghost_x[ghostIndex] + 12)
                    && pacman_y > (ghost_y[ghostIndex] - 12) && pacman_y < (ghost_y[ghostIndex] + 12)
                    && Running) {
                pacman.set_alive(false);
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // }
        }
    }

    public void move_ghosts4(int ghostIndex, Graphics2D g2d, short [] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
        int pacman_x = pacman.get_x();
        int pacman_y = pacman.get_y();
        boolean Pac_alive = pacman.Pac_alive;

        while (true) {
            //synchronized (this) {
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
            if (pacman_x > (ghost_x[ghostIndex] - 12) && pacman_x < (ghost_x[ghostIndex] + 12)
                    && pacman_y > (ghost_y[ghostIndex] - 12) && pacman_y < (ghost_y[ghostIndex] + 12)
                    && Running) {
                pacman.set_alive(false);
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // }
        }
    }


   public void drawGhosts(Graphics2D g2d) {
       //lock.lock();
       try {
           for (int i = 0; i < ghost_x.length; i++)
               g2d.drawImage(this.ghost_img, ghost_x[i], ghost_y[i], this);
       } finally {
           //lock.unlock();
       }
   }

/*    public void drawGhost(Graphics2D g2d, int x, int y){
        g2d.drawImage(this.ghost_img, x, y, this);

    }*/
}
