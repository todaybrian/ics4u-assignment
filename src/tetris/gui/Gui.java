package tetris.gui;

import tetris.GamePanel;
import tetris.gui.widget.AnimatedRectangle;
import tetris.gui.widget.AnimationType;
import tetris.util.Assets;
import tetris.gui.widget.Button;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Gui {
    protected ImageIcon topBar;
    protected ImageIcon bottomBar;

    protected ArrayList<AnimatedRectangle> componentList;
    protected ArrayList<Button> buttonList;
    protected GamePanel instance;

    public Gui(){
        buttonList = new ArrayList<>();
        componentList = new ArrayList<>();
        instance = GamePanel.getGamePanel();
    }

    public void draw(Graphics2D g){
        instance.getGameBackground().draw(g);
        //Top bar
        if(topBar != null) {
            g.drawImage(topBar.getImage(), 0, 0, topBar.getIconWidth(), topBar.getIconHeight(), null);
        }
        //Bottom bar
        if(bottomBar != null) {
            g.drawImage(bottomBar.getImage(), 0, GamePanel.INTERNAL_HEIGHT - bottomBar.getIconHeight(), bottomBar.getIconWidth(), bottomBar.getIconHeight(), null);
        }

        for(AnimatedRectangle component : componentList){
            component.draw(g);
        }
        boolean mouseOverButton = false;
        for(Button button : buttonList){
            if(button.isMouseOver()){
                mouseOverButton = true;
            }
            button.draw(g);
        }

        if(mouseOverButton){
            instance.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else{
            instance.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void mouseClicked(){
        //TODO: Check if mouse is above each button and run button interface if it is clicked
        for(Button button: buttonList){
            if(button.isMouseOver())
                button.setClicked(true);
        }
    }

    public void mouseReleased(){
        for(Button button: buttonList){
            if(button.isClicked() && button.isMouseOver())
                button.clicked();
            button.setClicked(false);
        }
    }

    public ArrayList<Button> getButtonList(){
        return buttonList;
    }

    public ArrayList<AnimatedRectangle> getComponentList(){
        return componentList;
    }

    public void update() {

    }

    public void keyPressed(KeyEvent e){

    }

    public void keyReleased(KeyEvent e){

    }


}
