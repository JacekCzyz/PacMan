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



    public void ghost_set_on_level(int dx, int speed){

        for (int i = 0; i < N_GHOSTS; i++) {
            ghost_y[i] = 4 * TILE_SIZE; //start position
            ghost_x[i] = 4 * TILE_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            ghost_vel[i]=speed;
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
    }


//to jest najlepsze co mamy


    public void move_ghosts1(int ghostIndex, Graphics2D g2d, short[] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
        int ghost_x_copy1 = ghost_x[ghostIndex];
        int ghost_y_copy1 = ghost_y[ghostIndex];
        int ghost_dx_copy1 = ghost_dx[ghostIndex];
        int ghost_dy_copy1 = ghost_dy[ghostIndex];

        int[] dx1 = new int[4];
        int[] dy1 = new int[4];

        while (true) {
            if (ghost_x_copy1 % TILE_SIZE == 0 && ghost_y_copy1 % TILE_SIZE == 0) {
                int pos = ghost_x_copy1 / TILE_SIZE + N_TILES * (ghost_y_copy1 / TILE_SIZE);
                int count = 0;

                if ((screen_data[pos] & 1) == 0 && ghost_dx_copy1 != 1) {
                    dx1[count] = -1;
                    dy1[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 2) == 0 && ghost_dy_copy1 != 1) {
                    dx1[count] = 0;
                    dy1[count] = -1;
                    count++;
                }

                if ((screen_data[pos] & 4) == 0 && ghost_dx_copy1 != -1) {
                    dx1[count] = 1;
                    dy1[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 8) == 0 && ghost_dy_copy1 != -1) {
                    dx1[count] = 0;
                    dy1[count] = 1;
                    count++;
                }

                if (count == 0) {
                    if ((screen_data[pos] & 15) == 15) {
                        ghost_dx_copy1 = 0;
                        ghost_dy_copy1 = 0;
                    } else {
                        ghost_dx_copy1 = -ghost_dx_copy1;
                        ghost_dy_copy1 = -ghost_dy_copy1;
                    }
                } else {
                    count = (int) (Math.random() * count);
                    if (count > 3) {
                        count = 3;
                    }
                    ghost_dx_copy1 = dx1[count];
                    ghost_dy_copy1 = dy1[count];
                }
            }

            ghost_x_copy1 = ghost_x_copy1 + (ghost_dx_copy1 * ghost_vel[ghostIndex]);
            ghost_y_copy1 = ghost_y_copy1 + (ghost_dy_copy1 * ghost_vel[ghostIndex]);

            drawGhosts(g2d);

            int pacman_x = pacman.get_x();
            int pacman_y = pacman.get_y();
            boolean Pac_alive = pacman.Pac_alive;

            if (pacman_x > (ghost_x_copy1 - 12) && pacman_x < (ghost_x_copy1 + 12)
                    && pacman_y > (ghost_y_copy1 - 12) && pacman_y < (ghost_y_copy1 + 12)
                    && Running) {
                pacman.set_alive(false);
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            synchronized (this) {
                // Przypisanie wartości z kopii zmiennych do zmiennych głównych
                ghost_x[ghostIndex] = ghost_x_copy1;
                ghost_y[ghostIndex] = ghost_y_copy1;
                ghost_dx[ghostIndex] = ghost_dx_copy1;
                ghost_dy[ghostIndex] = ghost_dy_copy1;
            }
        }
    }


    public void move_ghosts2(int ghostIndex, Graphics2D g2d, short[] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
        int ghost_x_copy2 = ghost_x[ghostIndex];
        int ghost_y_copy2 = ghost_y[ghostIndex];
        int ghost_dx_copy2 = ghost_dx[ghostIndex];
        int ghost_dy_copy2 = ghost_dy[ghostIndex];

        int[] dx2 = new int[4];
        int[] dy2 = new int[4];

        while (true) {
            if (ghost_x_copy2 % TILE_SIZE == 0 && ghost_y_copy2 % TILE_SIZE == 0) {
                int pos = ghost_x_copy2 / TILE_SIZE + N_TILES * (ghost_y_copy2 / TILE_SIZE);
                int count = 0;

                if ((screen_data[pos] & 1) == 0 && ghost_dx_copy2 != 1) {
                    dx2[count] = -1;
                    dy2[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 2) == 0 && ghost_dy_copy2 != 1) {
                    dx2[count] = 0;
                    dy2[count] = -1;
                    count++;
                }

                if ((screen_data[pos] & 4) == 0 && ghost_dx_copy2 != -1) {
                    dx2[count] = 1;
                    dy2[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 8) == 0 && ghost_dy_copy2 != -1) {
                    dx2[count] = 0;
                    dy2[count] = 1;
                    count++;
                }

                if (count == 0) {
                    if ((screen_data[pos] & 15) == 15) {
                        ghost_dx_copy2 = 0;
                        ghost_dy_copy2 = 0;
                    } else {
                        ghost_dx_copy2 = -ghost_dx_copy2;
                        ghost_dy_copy2 = -ghost_dy_copy2;
                    }
                } else {
                    count = (int) (Math.random() * count);
                    if (count > 3) {
                        count = 3;
                    }
                    ghost_dx_copy2 = dx2[count];
                    ghost_dy_copy2 = dy2[count];
                }
            }

            ghost_x_copy2 = ghost_x_copy2 + (ghost_dx_copy2 * ghost_vel[ghostIndex]);
            ghost_y_copy2 = ghost_y_copy2 + (ghost_dy_copy2 * ghost_vel[ghostIndex]);

            drawGhosts(g2d);

            int pacman_x = pacman.get_x();
            int pacman_y = pacman.get_y();
            boolean Pac_alive = pacman.Pac_alive;

            if (pacman_x > (ghost_x_copy2 - 12) && pacman_x < (ghost_x_copy2 + 12)
                    && pacman_y > (ghost_y_copy2 - 12) && pacman_y < (ghost_y_copy2 + 12)
                    && Running) {
                pacman.set_alive(false);
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (this) {
                // Przypisanie wartości z kopii zmiennych do zmiennych głównych
                ghost_x[ghostIndex] = ghost_x_copy2;
                ghost_y[ghostIndex] = ghost_y_copy2;
                ghost_dx[ghostIndex] = ghost_dx_copy2;
                ghost_dy[ghostIndex] = ghost_dy_copy2;
            }
        }
    }


    public void move_ghosts3(int ghostIndex, Graphics2D g2d, short[] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
        int ghost_x_copy3 = ghost_x[ghostIndex];
        int ghost_y_copy3 = ghost_y[ghostIndex];
        int ghost_dx_copy3 = ghost_dx[ghostIndex];
        int ghost_dy_copy3 = ghost_dy[ghostIndex];

        int[] dx3 = new int[4];
        int[] dy3 = new int[4];

        while (true) {
            if (ghost_x_copy3 % TILE_SIZE == 0 && ghost_y_copy3 % TILE_SIZE == 0) {
//                int pos = ghost_x_copy3 / TILE_SIZE + N_TILES * (ghost_y_copy3 / TILE_SIZE);
//                int count = 0;
//
//                if ((screen_data[pos] & 1) == 0 && ghost_dx_copy3 != 1) {
//                    dx3[count] = -1;
//                    dy3[count] = 0;
//                    count++;
//                }
//
//                if ((screen_data[pos] & 2) == 0 && ghost_dy_copy3 != 1) {
//                    dx3[count] = 0;
//                    dy3[count] = -1;
//                    count++;
//                }
//
//                if ((screen_data[pos] & 4) == 0 && ghost_dx_copy3 != -1) {
//                    dx3[count] = 1;
//                    dy3[count] = 0;
//                    count++;
//                }
//
//                if ((screen_data[pos] & 8) == 0 && ghost_dy_copy3 != -1) {
//                    dx3[count] = 0;
//                    dy3[count] = 1;
//                    count++;
//                }
//
//                if (count == 0) {
//                    if ((screen_data[pos] & 15) == 15) {
//                        ghost_dx_copy3 = 0;
//                        ghost_dy_copy3 = 0;
//                    } else {
//                        ghost_dx_copy3 = -ghost_dx_copy3;
//                        ghost_dy_copy3 = -ghost_dy_copy3;
//                    }
//                } else {
                    int pacman_x = pacman.get_x();
                    int pacman_y = pacman.get_y();

                    // Oblicz różnicę pomiędzy położeniem duszka a Pacmana
                    int dx = pacman_x - ghost_x_copy3;
                    int dy = pacman_y - ghost_y_copy3;

                    // Znajdź dominujący kierunek (poziomy lub pionowy) i ustaw odpowiednie wartości dx3 i dy3
                    if (Math.abs(dx) > Math.abs(dy)) {
                        dx3[0] = (dx > 0) ? 1 : -1;
                        dy3[0] = 0;
                        dx3[1] = 0;
                        dy3[1] = (dy > 0) ? 1 : -1;
                    } else {
                        dx3[0] = 0;
                        dy3[0] = (dy > 0) ? 1 : -1;
                        dx3[1] = (dx > 0) ? 1 : -1;
                        dy3[1] = 0;
                    }

                    ghost_dx_copy3 = dx3[0];
                    ghost_dy_copy3 = dy3[0];
//                }
            }

            int future_x = ghost_x_copy3 + (ghost_dx_copy3 * ghost_vel[ghostIndex]);
            int future_y = ghost_y_copy3 + (ghost_dy_copy3 * ghost_vel[ghostIndex]);
            int future_pos = future_x / TILE_SIZE + N_TILES * (future_y / TILE_SIZE);
            boolean future_collides=true;

            while(future_collides==true) {
                future_collides = false;
                if ((screen_data[future_pos] & 8) == 1 || dy3[0] == 1) {
                    dy3[0] = 0;
                    dx3[0] = -1;
                    future_collides = true;
                } else if ((screen_data[future_pos] & 1) == 1 || dx3[0] == -1) {
                    dy3[0] = 1;
                    dx3[0] = 0;
                    future_collides = true;
                } else if ((screen_data[future_pos] & 2) == 1 || dy[0] == -1) {
                    dy3[0] = 0;
                    dx3[0] = 1;
                    future_collides = true;
                } else if ((screen_data[future_pos] & 4) == 1 || dx3[0] == 1) {
                    dy3[0] = -1;
                    dx3[0] = 0;
                    future_collides = true;
                }
            }

            ghost_dx_copy3 = dx3[0];
            ghost_dy_copy3 = dy3[0];


            ghost_x_copy3 = ghost_x_copy3 + (ghost_dx_copy3 * ghost_vel[ghostIndex]);
            ghost_y_copy3 = ghost_y_copy3 + (ghost_dy_copy3 * ghost_vel[ghostIndex]);

            drawGhosts(g2d);

            int pacman_x = pacman.get_x();
            int pacman_y = pacman.get_y();
            boolean Pac_alive = pacman.Pac_alive;

            if (pacman_x > (ghost_x_copy3 - 12) && pacman_x < (ghost_x_copy3 + 12)
                    && pacman_y > (ghost_y_copy3 - 12) && pacman_y < (ghost_y_copy3 + 12)
                    && Running) {
                pacman.set_alive(false);
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (this) {
                // Przypisanie wartości z kopii zmiennych do zmiennych głównych
                ghost_x[ghostIndex] = ghost_x_copy3;
                ghost_y[ghostIndex] = ghost_y_copy3;
                ghost_dx[ghostIndex] = ghost_dx_copy3;
                ghost_dy[ghostIndex] = ghost_dy_copy3;
            }
        }
    }


//    public void move_ghosts3(int ghostIndex, Graphics2D g2d, short [] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
//        int ghost_x_copy3 = ghost_x[ghostIndex];
//        int ghost_y_copy3 = ghost_y[ghostIndex];
//        int ghost_dx_copy3 = ghost_dx[ghostIndex];
//        int ghost_dy_copy3 = ghost_dy[ghostIndex];
//
//        int[] dx3 = new int[4];
//        int[] dy3 = new int[4];
//
//        while (true) {
//            if (ghost_x_copy3 % TILE_SIZE == 0 && ghost_y_copy3 % TILE_SIZE == 0) {
//                int pos = ghost_x_copy3 / TILE_SIZE + N_TILES * (ghost_y_copy3 / TILE_SIZE);
//                int count = 0;
//
//                if ((screen_data[pos] & 1) == 0 && ghost_dx_copy3 != 1) {
//                    dx3[count] = -1;
//                    dy3[count] = 0;
//                    count++;
//                }
//
//                if ((screen_data[pos] & 2) == 0 && ghost_dy_copy3 != 1) {
//                    dx3[count] = 0;
//                    dy3[count] = -1;
//                    count++;
//                }
//
//                if ((screen_data[pos] & 4) == 0 && ghost_dx_copy3 != -1) {
//                    dx3[count] = 1;
//                    dy3[count] = 0;
//                    count++;
//                }
//
//                if ((screen_data[pos] & 8) == 0 && ghost_dy_copy3 != -1) {
//                    dx3[count] = 0;
//                    dy3[count] = 1;
//                    count++;
//                }
//
//                if (count == 0) {
//                    if ((screen_data[pos] & 15) == 15) {
//                        ghost_dx_copy3 = 0;
//                        ghost_dy_copy3 = 0;
//                    } else {
//                        ghost_dx_copy3 = -ghost_dx_copy3;
//                        ghost_dy_copy3 = -ghost_dy_copy3;
//                    }
//                } else {
//                    count = (int) (Math.random() * count);
//                    if (count > 3) {
//                        count = 3;
//                    }
//                    ghost_dx_copy3 = dx3[count];
//                    ghost_dy_copy3 = dy3[count];
//                }
//            }
//
//            ghost_x_copy3 = ghost_x_copy3 + (ghost_dx_copy3 * ghost_vel[ghostIndex]);
//            ghost_y_copy3 = ghost_y_copy3 + (ghost_dy_copy3 * ghost_vel[ghostIndex]);
//
//            drawGhosts(g2d);
//
//            int pacman_x = pacman.get_x();
//            int pacman_y = pacman.get_y();
//            boolean Pac_alive = pacman.Pac_alive;
//
//            if (pacman_x > (ghost_x_copy3 - 12) && pacman_x < (ghost_x_copy3 + 12)
//                    && pacman_y > (ghost_y_copy3 - 12) && pacman_y < (ghost_y_copy3 + 12)
//                    && Running) {
//                pacman.set_alive(false);
//            }
//
//            try {
//                Thread.sleep(40);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//
//            synchronized (this) {
//                // Przypisanie wartości z kopii zmiennych do zmiennych głównych
//                ghost_x[ghostIndex] = ghost_x_copy3;
//                ghost_y[ghostIndex] = ghost_y_copy3;
//                ghost_dx[ghostIndex] = ghost_dx_copy3;
//                ghost_dy[ghostIndex] = ghost_dy_copy3;
//            }
//        }
//    }

    public void move_ghosts4(int ghostIndex, Graphics2D g2d, short [] screen_data, int TILE_SIZE, int N_TILES, PacGuy pacman, boolean Running) {
        int ghost_x_copy4 = ghost_x[ghostIndex];
        int ghost_y_copy4 = ghost_y[ghostIndex];
        int ghost_dx_copy4 = ghost_dx[ghostIndex];
        int ghost_dy_copy4 = ghost_dy[ghostIndex];

        int[] dx4 = new int[4];
        int[] dy4 = new int[4];

        while (true) {
            if (ghost_x_copy4 % TILE_SIZE == 0 && ghost_y_copy4 % TILE_SIZE == 0) {
                int pos = ghost_x_copy4 / TILE_SIZE + N_TILES * (ghost_y_copy4 / TILE_SIZE);
                int count = 0;

                if ((screen_data[pos] & 1) == 0 && ghost_dx_copy4 != 1) {
                    dx4[count] = -1;
                    dy4[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 2) == 0 && ghost_dy_copy4 != 1) {
                    dx4[count] = 0;
                    dy4[count] = -1;
                    count++;
                }

                if ((screen_data[pos] & 4) == 0 && ghost_dx_copy4 != -1) {
                    dx4[count] = 1;
                    dy4[count] = 0;
                    count++;
                }

                if ((screen_data[pos] & 8) == 0 && ghost_dy_copy4 != -1) {
                    dx4[count] = 0;
                    dy4[count] = 1;
                    count++;
                }

                if (count == 0) {
                    if ((screen_data[pos] & 15) == 15) {
                        ghost_dx_copy4 = 0;
                        ghost_dy_copy4 = 0;
                    } else {
                        ghost_dx_copy4 = -ghost_dx_copy4;
                        ghost_dy_copy4 = -ghost_dy_copy4;
                    }
                } else {
                    count = (int) (Math.random() * count);
                    if (count > 3) {
                        count = 3;
                    }
                    ghost_dx_copy4 = dx4[count];
                    ghost_dy_copy4 = dy4[count];
                }
            }

            ghost_x_copy4 = ghost_x_copy4 + (ghost_dx_copy4 * ghost_vel[ghostIndex]);
            ghost_y_copy4 = ghost_y_copy4 + (ghost_dy_copy4 * ghost_vel[ghostIndex]);

            drawGhosts(g2d);

            int pacman_x = pacman.get_x();
            int pacman_y = pacman.get_y();
            boolean Pac_alive = pacman.Pac_alive;

            if (pacman_x > (ghost_x_copy4 - 12) && pacman_x < (ghost_x_copy4 + 12)
                    && pacman_y > (ghost_y_copy4 - 12) && pacman_y < (ghost_y_copy4 + 12)
                    && Running) {
                pacman.set_alive(false);
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            synchronized (this) {
                // Przypisanie wartości z kopii zmiennych do zmiennych głównych
                ghost_x[ghostIndex] = ghost_x_copy4;
                ghost_y[ghostIndex] = ghost_y_copy4;
                ghost_dx[ghostIndex] = ghost_dx_copy4;
                ghost_dy[ghostIndex] = ghost_dy_copy4;
            }
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
