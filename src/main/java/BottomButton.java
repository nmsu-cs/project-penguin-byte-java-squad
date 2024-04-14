import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class BottomButton extends JButton {
    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        this.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +

                "focusWidth:0;"+
                "innerFocusWidth:0");

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void setIcon(){

    }
}
