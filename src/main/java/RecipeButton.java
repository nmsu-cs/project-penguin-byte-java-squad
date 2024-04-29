import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class RecipeButton extends JButton {
    private Recipe recipe = null;


    public RecipeButton(Recipe recipe) throws IOException, InterruptedException {
        this.recipe = recipe;
        this.setLayout(new MigLayout("ins 0","[]","[]"));
        JLabel left = new JLabel();
        int widHei = 60;

        String filePath = "images/"+recipe.getTitle()+".png";
        File file = new File(filePath);
        if (file.exists()) {
            left.setIcon(new ImageIcon(new ImageIcon("images/"+recipe.getTitle().replace(",", "").replace("\"","")+".png").getImage().getScaledInstance(widHei, widHei, Image.SCALE_SMOOTH)));
        } else {
            // Generate one.
            left.setIcon(new ImageIcon(new ImageIcon("images/"+OpenAIRequest.getOpenAI_Image(recipe.getTitle())).getImage().getScaledInstance(widHei, widHei, Image.SCALE_SMOOTH)));
        }

        //left.setIcon(new ImageIcon(new ImageIcon("recipe-init.png").getImage().getScaledInstance(widHei, widHei, Image.SCALE_SMOOTH)));

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
        nFrame.setSize(300,200);
        //nFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        nFrame.pack();
        nFrame.setLocationRelativeTo(this);

        JTextArea recipeDetails = new JTextArea();
        String [] instructions = rp.getInstructions();
        StringBuilder detailsBuilder = new StringBuilder();

        for(String instruction : instructions) {
            detailsBuilder.append(instruction.trim()).append("\n");
        }
        recipeDetails.setText("Title: " + rp.getTitle() + "\n"
                + "Instructions: " + detailsBuilder.toString() +
                "Link: " + rp.getLink() + "\n");
        JScrollPane detailsScroll = new JScrollPane(recipeDetails);
        nFrame.add(detailsScroll);
        nFrame.setVisible(true);
    }



}
