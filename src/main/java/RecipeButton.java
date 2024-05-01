import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class RecipeButton extends JButton {
    private Recipe recipe = null;

    public boolean AIGeneratedImages = false;


    public RecipeButton(Recipe recipe) throws IOException, InterruptedException {
        this.recipe = recipe;
        this.setLayout(new MigLayout("ins 0","[]","[]"));
        JLabel left = new JLabel();
        int widHei = 60;

        String filePath = "images/"+recipe.getTitle().replace(",", "").replace("\"","")+".png";
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
        nFrame.setSize(325,612);
        //nFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        nFrame.pack();
        nFrame.setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new MigLayout("ins 0","[]","[]3[]"));

        JPanel titleLabel = new JPanel();
        JLabel titleArea = new JLabel("what the heck");
        titleLabel.setLayout(new MigLayout("wrap","[50%, grow][]","[][]"));
        titleArea.setText("<html><h2 style='max-width: 300px;'>"+rp.getTitle()+"</h2></html>");

        titleLabel.add(titleArea);
        headerPanel.add(titleLabel);
        titleLabel.setVisible(true);

        //creating icon for recipe details
        ImageIcon icon = new ImageIcon("images/"+rp.getTitle()+".png");
        Image image = icon.getImage();
        Image image1 = image.getScaledInstance(150, 125, Image.SCALE_SMOOTH);
        ImageIcon icon2 = new ImageIcon(image1);
        JLabel recipeLabel = new JLabel(icon2);
        headerPanel.add(recipeLabel, BorderLayout.CENTER);


        //add button for recipe url
        JButton linkButton = new JButton("Recipe Website");
        linkButton.setFont(new Font(linkButton.getFont().getName(), Font.BOLD, 24));
        linkButton.setSize(new Dimension(20,20));


        linkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    Desktop.getDesktop().browse(new URI(rp.getLink()));
                }   catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

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
        recipeDetails.setText("Ingredients:" + "\n\n"
                + ing + "\n" + "Instructions:" + " \n\n" + ins);
        recipeDetails.setLineWrap(true);
        recipeDetails.setWrapStyleWord(true);
        recipeDetails.setFont(new Font(recipeDetails.getFont().getName(), Font.BOLD, 16));
        recipeDetails.setEditable(false);
        JScrollPane detailsScroll = new JScrollPane(recipeDetails);
        nFrame.add(detailsScroll);
        nFrame.add(linkButton, BorderLayout.SOUTH);
        nFrame.add(headerPanel, BorderLayout.NORTH);
        nFrame.setIconImage(image1);
        nFrame.setVisible(true);
    }
}
