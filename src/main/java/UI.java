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
    private int paginationCurrent = 0;
    private int pagination_Increments = 10; // Changes the limit by.
    public UI() {
        init();
        database = new database();
    }

    private JPanel center;
    private PlaceholderTextField txtSearch;


    private void searchFunction(){
        try {
            center.removeAll();
            Connection c = database.getConnection();
            Statement stmt = c.createStatement();
            ResultSet rs;
            rs = stmt.executeQuery("SELECT * FROM dataset WHERE title LIKE \"%"+txtSearch.getText()+"%\" LIMIT "+pagination_Increments+" OFFSET "+paginationCurrent);
//                ResultSetMetaData rsmd = rs.getMetaData();
            ArrayList<Recipe> recipes = Recipe.getList(rs);

            for (Recipe rp : recipes){
                center.add(new RecipeButton(rp));
            }

            JPanel pagination = new JPanel();
            pagination.setLayout(new MigLayout("","[25%][25%][]",""));
            JButton prev = new JButton("Prev.");
            JButton next = new JButton("Next");
            pagination.add(prev,"skip, grow");
            pagination.add(next,"grow");
            JLabel pageNumber = new JLabel(paginationCurrent+"/"+pagination_Increments);
            pagination.add(pageNumber);

            prev.addActionListener(previous -> {
                if (paginationCurrent >= pagination_Increments){
                    paginationCurrent-=pagination_Increments;
                    searchFunction();
                }
            });
            next.addActionListener(nextPage -> {
                paginationCurrent+=pagination_Increments;
                searchFunction();
            });

            center.add(pagination);

            center.revalidate();
            center.repaint();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private JPanel mainPane(){
        JPanel main = new JPanel();
        main.setLayout(new MigLayout("wrap 1","[100%]","[7%, grow][83%, grow][10%, grow]"));

        JPanel topBar = new JPanel();
        topBar.setLayout(new MigLayout("","[80%, grow][20%, grow]",""));
        topBar.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%)");
        main.add(topBar,"grow,wrap");

        JLabel tField = new JLabel("Cooking Companion, Title search");
        topBar.add(tField,"wrap");


        txtSearch = new PlaceholderTextField();
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
        topBar.add(btnSearch,"grow, wrap");

        JCheckBox andrew1 = new JCheckBox("Diary");
        JCheckBox andrew2 = new JCheckBox("Nuts");
        JCheckBox andrew3 = new JCheckBox("?");
        topBar.add(andrew1,"grow");
        topBar.add(andrew2,"grow");
        topBar.add(andrew3,"grow");

        center = new JPanel();
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        btnSearch.addActionListener(search -> {
            System.out.println("Search pressed.");
            searchFunction();
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
