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

public class UI extends JFrame {
    private static database database;
    public UI() {
        init();
        database = new database();
    }

    private void recipeDetails(){
        JFrame nFrame = new JFrame();
        nFrame.pack();
        nFrame.setVisible(true);
        nFrame.setLocationRelativeTo(this);
    }

    private JButton row(Recipe rp){
        JButton row = new JButton();
        row.setLayout(new MigLayout("ins 0","[]","[]"));
        JLabel left = new JLabel();
        int widHei = 60;
        left.setIcon(new ImageIcon(new ImageIcon("NMSU_NoU-Crimson.png").getImage().getScaledInstance(widHei, widHei, Image.SCALE_DEFAULT)));

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
            recipeDetails();
        });
        return row;
    }

    private JPanel mainPain(){
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
        setIconImage(new ImageIcon("logo.png").getImage());
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
