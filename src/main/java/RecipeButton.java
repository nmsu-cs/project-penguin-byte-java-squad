import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import javax.swing.*;
import java.awt.*;

public class RecipeButton extends JButton {
    private Recipe recipe = null;

    public RecipeButton(Recipe recipe) {
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    private JButton row(Recipe rp) {
        this.setLayout(new MigLayout("ins 0","[10%, grow][grow]","grow"));
        JLabel left = new JLabel();
        int widHei = 60;
        left.setIcon(new ImageIcon(new ImageIcon("recipe-init.png").getImage().getScaledInstance(widHei, widHei, Image.SCALE_SMOOTH)));

        JPanel right = new JPanel();
        right.setLayout(new MigLayout("wrap","[][]","[][]"));
        JLabel title = new JLabel("Creamy Corn");
        title.setText("<html><h3>"+rp.getTitle()+"</h3></html>");
        right.add(title);
        right.add(new JLabel("Have"));
        right.add(new JLabel("Difficulty: 1-10/100"));
        right.add(new JLabel("cookbook.org"));

        right.setOpaque(false);

        this.add(left);
        this.add(right);

        this.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%);" +
                "borderWidth:0;"+
                "focusPainted:false;"+
                "innerFocusWidth:0");

        this.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel panel = new JPanel();
        this.addActionListener(rowClicked -> {
            recipeDetails(this.getRecipe());
        });

        return this;

    }

    private void recipeDetails(Recipe rp) {

    }



}
