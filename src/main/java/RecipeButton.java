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
        this.setLayout(new MigLayout("ins 0","[]","[]"));
        JLabel left = new JLabel();
        int widHei = 60;
        left.setIcon(new ImageIcon(new ImageIcon("recipe-init.png").getImage().getScaledInstance(widHei, widHei, Image.SCALE_SMOOTH)));

        JPanel right = new JPanel();
        right.setLayout(new MigLayout("wrap","[50%, grow][]","[][]"));
        JLabel title = new JLabel("Creamy Corn");


        title.setText("<html><h3 style='max-width: 200px;'>"+recipe.getTitle()+"</h3></html>");

        System.out.println(recipe);

        right.add(title);
        right.add(new JLabel("UID: "+recipe.getID()));
        right.add(new JLabel("Difficulty: "+recipe.getIngredients().length));
        right.add(new JLabel(recipe.getLink().split("/")[0]));

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
            recipeDetails();
        });
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    private JButton row(Recipe rp) {


        return this;

    }

    private void recipeDetails() {
        Recipe rp = this.recipe;
        JFrame nFrame = new JFrame("Recipe Details");
        nFrame.setSize(325,612);
        //nFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        nFrame.pack();
        nFrame.setLocationRelativeTo(null);

        JTextArea recipeDetails = new JTextArea();
        String [] instructions = rp.getInstructions();
        String [] ingredients = rp.getIngredients();
        StringBuilder detailsBuilder = new StringBuilder();
        StringBuilder ingredBuilder = new StringBuilder();

        for(String instruction : instructions) {
            detailsBuilder.append(instruction.trim()).append("\n");
        }
        for(String ingredient : ingredients) {
            ingredBuilder.append(ingredient.trim()).append("\n");
        }
        String ing = ingredBuilder.toString();
        ing = ing.replaceAll("u00b0", "\u00b0");
        ing = ing.replaceAll("\"", "");
        ing = ing.replaceAll("\\]", "");
        ing = ing.replaceAll("\\[", "");
        String ins = detailsBuilder.toString();
        ins = ins.replaceAll("u00b0", "\u00b0");
        ins = ins.replaceAll("\"", "");
        ins = ins.replaceAll("\\]", "");
        ins = ins.replaceAll("\\[", "");
        recipeDetails.setText("Title: " + rp.getTitle() + "\nIngredients: \n" + ing
                + "Instructions: \n" + ins +
                "Link: " + rp.getLink() + "\n");
        JScrollPane detailsScroll = new JScrollPane(recipeDetails);
        nFrame.add(detailsScroll);
        nFrame.setVisible(true);
    }
}
