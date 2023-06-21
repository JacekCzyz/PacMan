package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Ghosts extends JPanel {
    private final int MAXN_GHOSTS = 6;
    private int N_GHOSTS = 6;
    private int [] ghost_x, ghost_y, ghost_dx, ghost_dy, ghost_vel;
    private Image ghost_img;

    private int [] dx, dy;

    public final int TILE_SIZE = 24;
    public final int N_TILES = 15;

    public Ghosts(){
        ghost_x = new int[MAXN_GHOSTS];
        ghost_dx = new int [MAXN_GHOSTS];
        ghost_y = new int[MAXN_GHOSTS];
        ghost_dy = new int[MAXN_GHOSTS];
        ghost_vel = new int[MAXN_GHOSTS];
        dx=new int[4];
        dy=new int[4];



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
        ghost_img = new ImageIcon("C:/Users/jacek/IdeaProjects/Pac Man/src/imgs/ghost.gif").getImage();
    }


    public boolean move_ghosts(Graphics2D g2d, short [] screen_data, int TILE_SIZE, int N_TILES, int pacman_x, int pacman_y, boolean Running, boolean Pac_alive) {

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
        return Pac_alive;
    }


    public void drawGhost(Graphics2D g2d, int x, int y){
        g2d.drawImage(this.ghost_img, x, y, this);

    }
}
