import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class UI extends JFrame {
    private static database database;
    public UI() {
        init();
        database = new database();
    }

    private void recipeDetails(Recipe rp){
        JFrame nFrame = new JFrame("Recipe Details");
        nFrame.setSize(300,200);
        nFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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



    private JButton row(Recipe rp){
        JButton row = new JButton();
        row.setLayout(new MigLayout("ins 0","[10%, grow][grow]","grow"));
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

        row.add(left);
        row.add(right);

        row.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%);" +
                "borderWidth:0;"+
                "focusPainted:false;"+
                "innerFocusWidth:0");
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        row.addActionListener(rowClicked -> {
            this.setContentPane(new JPanel());
            recipeDetails(rp);
        });
        return row;
    }

    private JPanel mainPain(){
        JPanel main = new JPanel();
        main.setLayout(new MigLayout("wrap 1","[grow]","[5%, grow][85%, grow][10%, grow]"));

        JPanel topBar = new JPanel();
        topBar.setLayout(new MigLayout("","[80%, grow][20%, grow]",""));
        topBar.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%)");
        main.add(topBar,"grow,wrap");

        JLabel tField = new JLabel("Cooking Companion, Title search");
        topBar.add(tField,"wrap");

        PlaceholderTextField txtSearch = new PlaceholderTextField();
        txtSearch.setPlaceholder("Title / Ingredients");
        txtSearch.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%);" +

                "focusWidth:0;"+
                "innerFocusWidth:0");
        topBar.add(txtSearch,"grow");
        JButton btnSearch = new JButton();
        btnSearch.setText("Search");
        topBar.add(btnSearch,"grow");

        JPanel center = new JPanel();
        center.setLayout(new GridLayout(0,1));
        btnSearch.addActionListener(search -> {
            System.out.println("Search pressed.");
            try {
                Connection c = database.getConnection();
                Statement stmt = c.createStatement();
                ResultSet rs;
                rs = stmt.executeQuery("SELECT * FROM dataset WHERE title LIKE \"%"+txtSearch.getText()+"%\" LIMIT 10");
//                ResultSetMetaData rsmd = rs.getMetaData();
                ArrayList<Recipe> recipes = Recipe.getList(rs);

                for (Recipe rp : recipes){
                    center.add(row(rp));
                }

//                while (rs.next()) {
//                    //center.add(row(rs.getString("title")));
//                    Recipe rp = new Recipe(rs.getInt("id"),rs.get);
//                    center.add(row(rp));
//                }

                center.revalidate();
                center.repaint();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setBorder(null);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setBlockIncrement(150);
        verticalScrollBar.setUnitIncrement(10);


        main.add(scrollPane, "grow, wrap");

        JPanel bottomPanel = new JPanel();
        bottomPanel.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%)");
        main.add(bottomPanel,"grow");


        MigLayout layout = new MigLayout("","[20%][20%, grow][20%, grow][20%, grow][20%]","grow");
        bottomPanel.setLayout(layout);

        bottomPanel.add(new BottomButton(),"skip, grow");
        bottomPanel.add(new BottomButton(),"grow");
        bottomPanel.add(new BottomButton(),"grow");
        return main;
    }

    private void init(){
        setTitle("Cooking Companion");
        setIconImage(new ImageIcon("logo.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(375, 812));
        setResizable(false);
        setLocationRelativeTo(null);
        setContentPane(mainPain());

    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("doug.themes");
        UIManager.put("defaultFont",new Font(FlatRobotoFont.FAMILY, Font.PLAIN,13));
        UIManager.put("defaultBoldFont",new Font(FlatRobotoFont.FAMILY, Font.BOLD,16));
        FlatMacDarkLaf.setup();
//        UIManager.put("Table.alternateRowColor", new Color(128, 128, 128,30));
        EventQueue.invokeLater(() -> new UI().setVisible(true));
    }
}
