import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import net.miginfocom.layout.Grid;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

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

    private JButton row(String ttl){
        JButton row = new JButton();
        row.setLayout(new MigLayout("ins 0","[10%, grow][grow]","grow"));
        JLabel left = new JLabel();
        int widHei = 60;
        left.setIcon(new ImageIcon(new ImageIcon("NMSU_NoU-Crimson.png").getImage().getScaledInstance(widHei, widHei, Image.SCALE_DEFAULT)));

        JPanel right = new JPanel();
        right.setLayout(new MigLayout("wrap","[][]","[][]"));
        JLabel title = new JLabel("Creamy Corn");
        title.setText("<html><h3>"+ttl+"</h3></html>");
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
            recipeDetails();
        });
        return row;
    }

    private JPanel mainPain(){
        JPanel main = new JPanel();
        main.setLayout(new MigLayout("wrap 1","[grow]","[10%, grow][80%, grow][10%, grow]"));

        JPanel topBar = new JPanel();
        topBar.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%)");
        main.add(topBar,"grow,wrap");

        JTextArea txtSearch = new JTextArea();
        topBar.add(txtSearch);
        JButton btnSearch = new JButton();
        topBar.add(btnSearch);

        JPanel center = new JPanel();
        center.setLayout(new GridLayout(0,1));
        btnSearch.addActionListener(search -> {
            System.out.println("Search pressed.");
            try {
                Connection c = database.getConnection();
                Statement stmt = c.createStatement();
                ResultSet rs;
                rs = stmt.executeQuery("SELECT * FROM dataset WHERE title LIKE \"%"+txtSearch.getText()+"%\" LIMIT 10");
                System.out.println(rs);
                ResultSetMetaData rsmd = rs.getMetaData();

                System.out.println(rs.getFetchSize());
                while (rs.next()) {
                    center.add(row(rs.getString("title")));
                }

                center.revalidate();
                center.repaint();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setBorder(null);



//        for(int i = 0; i < 150; i++){
//            center.add(row(""));
//        }

        main.add(scrollPane, "grow, wrap");

        JPanel bottomPanel = new JPanel();
        bottomPanel.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%)");
        main.add(bottomPanel,"grow");
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
