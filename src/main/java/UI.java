import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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
        row.setLayout(new MigLayout("ins 0","[]","[]"));
        JLabel left = new JLabel();
        int widHei = 60;
        left.setIcon(new ImageIcon(new ImageIcon("recipe-init.png").getImage().getScaledInstance(widHei, widHei, Image.SCALE_SMOOTH)));

        JPanel right = new JPanel();
        right.setLayout(new MigLayout("wrap","[60%, grow][]","[][]"));
        JLabel title = new JLabel("Creamy Corn");

        title.setText("<html><h3 style='max-width: 200px;'>"+rp.getTitle()+"</h3></html>");
//        title.setMaximumSize(new Dimension((row.getWidth()-60)/2,Integer.MIN_VALUE));


        right.add(title);
        right.add(new JLabel("Have"),"wrap");
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

    private JPanel mainPane(){
        JPanel main = new JPanel();
        main.setLayout(new MigLayout("wrap 1","[100%]","[5%, grow][85%, grow][10%, grow]"));

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
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        btnSearch.addActionListener(search -> {
            System.out.println("Search pressed.");
            try {
                center.removeAll();
                Connection c = database.getConnection();
                Statement stmt = c.createStatement();
                ResultSet rs;
                rs = stmt.executeQuery("SELECT * FROM dataset WHERE title LIKE \"%"+txtSearch.getText()+"%\" LIMIT 50");
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

        JScrollPane scrollPane = new JScrollPane(center,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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


        BottomButton importButton = new BottomButton();
        importButton.setText("Import ingredient file");
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        System.out.println(reader.readList(file));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        bottomPanel.add(importButton,"skip, grow");
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
        setContentPane(mainPane());

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
