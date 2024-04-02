package com.recipeapplication;
import javax.swing.*;
public class View {
    private JPanel mainPanel;

    public View(){
        mainPanel = new JPanel();
    }
    public void setController(Controller controller){
        //fix
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
