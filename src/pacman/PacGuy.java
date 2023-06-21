package pacman;

import javax.swing.*;
import java.awt.*;

public class PacGuy extends JFrame {
    private final int PACMAN_VEL = 6;
    public boolean Pac_alive = true;
    private Image up, down, left, right; //obrazki do ruchu w kazda strone
    private int pacman_x, pacman_y, pacman_dx, pacman_dy;
    public final int TILE_SIZE = 24;
    public final int N_TILES = 15;

    public void load_pac_images(){
        down = new ImageIcon("C:/Users/jacek/IdeaProjects/Pac Man/src/imgs/down.gif").getImage();
        left = new ImageIcon("C:/Users/jacek/IdeaProjects/Pac Man/src/imgs/left.gif").getImage();
        right = new ImageIcon("C:/Users/jacek/IdeaProjects/Pac Man/src/imgs/right.gif").getImage();
        up = new ImageIcon("C:/Users/jacek/IdeaProjects/Pac Man/src/imgs/up.gif").getImage();
    }

    public int get_x(){
        return pacman_x;
    }
    public int get_y(){
        return pacman_y;
    }
    public boolean get_alive(){
        return Pac_alive;
    }

    public void move_pac(int req_dx, int req_dy, short[] screen_data, int score){
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

    public void draw_pac(Graphics2D g2d, int req_dx, int req_dy){
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

    public void pacguy_set_on_level(){
        pacman_x = 7 * TILE_SIZE;  //start position
        pacman_y = 11 * TILE_SIZE;
        pacman_dx = 0;	//reset direction move
        pacman_dy = 0;
        Pac_alive = true;
    }
}
