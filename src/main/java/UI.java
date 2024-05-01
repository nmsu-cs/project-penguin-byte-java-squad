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
    private boolean importDataSearch = false;
    public UI() {
        init();
        database = new database();
    }

    private JPanel center;
    private PlaceholderTextField txtSearch;
    private JCheckBox jackStoleThis;

    private boolean nut;
    private String[] nutList = {"Almond", "Beechnut", "Brazil nut", "Butternut", "Cashew", "Chestnut", "Chinquapin nut", "Coconut",
                                "Filbert", "hazelnut", "Gianduja", "Ginkgo nut", "Hickory nut", " nut ", "nuts", "peanut",
                                "Litchi", "lichee", "lychee nut", "Macadamia nut", "Marzipan", "Nangai nut",
                                "Pecan", "Pesto", "Pili nut", "Pine nut", "Pistachio", "Praline", "Shea nut", "Walnut"};
    private boolean dairy;



    private void afterSearchFunctions(ResultSet rs,String adSearch) throws IOException, InterruptedException, SQLException {
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
                if(adSearch == null) {
                    searchFunction();
                }else{
                    searchFunctionV2(adSearch);
                }
            }
        });
        next.addActionListener(nextPage -> {
            paginationCurrent+=pagination_Increments;
            if(adSearch == null) {
                searchFunction();
            }else{
                searchFunctionV2(adSearch);
            }
        });

        center.add(pagination);

        center.revalidate();
        center.repaint();
    }
    private void searchFunction(){
        try {
            center.removeAll();
            Connection c = database.getConnection();
            Statement stmt = c.createStatement();
            ResultSet rs;
            String query = "SELECT * FROM dataset WHERE title LIKE \"%" + txtSearch.getText() + "%\"";
            if (dairy) {
                query += " and ingredients NOT LIKE \"%milk%\""+
                         " and ingredients NOT LIKE \"%butter%\""+
                         " and ingredients NOT LIKE \"%cheese%\""+
                         " and ingredients NOT LIKE \"%cream%\""+
                         " and ingredients NOT LIKE \"%yogurt%\"";
            }
            if (nut) {
                for (int i = 0; i < nutList.length; i++) {
                    query += " and ingredients NOT LIKE \"%" + nutList[i] + "%\"";
                }
            }
            query += " LIMIT " + pagination_Increments + " OFFSET " + paginationCurrent;
            // rs = stmt.executeQuery("SELECT * FROM dataset WHERE title LIKE \"%" + txtSearch.getText() + "%\" LIMIT " + pagination_Increments + " OFFSET " + paginationCurrent);
            rs = stmt.executeQuery(query);
//                ResultSetMetaData rsmd = rs.getMetaData();

            afterSearchFunctions(rs,null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void searchFunctionV2(String importedPantry) {
        try {
            //String importedPantry = "";
            center.removeAll();
            Connection c = database.getConnection();
            Statement stmt = c.createStatement();
            ResultSet rs;
            String SQL_Query = "select * from dataset D where D.id in " +
                    "(select T1.id" +
                    "from (select I.id, count(*) as totalIngredients " +
                    "from ingredients I " +
                    "group by I.id) as T1, " +
                    "(select id, count(*) as total " +
                    "from ingredients E " +
                    "where E.ingredient_name in (" + importedPantry + ") " +
                    "group by E.id) as T2 " +
                    "where T1.id = T2.id and T1.totalIngredients = T2.total)";

            if(dairy || nut){
                SQL_Query+="WHERE ";
            }
            if(jackStoleThis.isSelected()){
                if(!(dairy||nut)){
                    SQL_Query+="WHERE ";
                }
                SQL_Query+="title LIKE \"%"+txtSearch.getText()+"%\"";
            }
            if (dairy) {
                SQL_Query += " and ingredients NOT LIKE \"%milk%\""+
                        " and ingredients NOT LIKE \"%butter%\""+
                        " and ingredients NOT LIKE \"%cheese%\""+
                        " and ingredients NOT LIKE \"%cream%\""+
                        " and ingredients NOT LIKE \"%yogurt%\"";
            }
            if (nut) {
                for (int i = 0; i < nutList.length; i++) {
                    SQL_Query += " and ingredients NOT LIKE \"%" + nutList[i] + "%\"";
                }
            }
            rs = stmt.executeQuery(SQL_Query);
            afterSearchFunctions(rs,importedPantry);
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
        topBar.setLayout(new MigLayout("","[100%, grow]",""));
        topBar.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%)");
        JPanel topBar_Main = new JPanel();
        JPanel topBar_Bottom = new JPanel();
        topBar_Bottom.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%)");
        topBar_Main.setLayout(new MigLayout("","[90%, grow][10%, grow]",""));
        topBar_Main.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%)");
        main.add(topBar,"grow,wrap");
        topBar.add(topBar_Main,"grow, wrap");
        topBar.add(topBar_Bottom);
        JLabel tField = new JLabel("Cooking Companion, Title search");
        topBar_Main.add(tField,"wrap");


        txtSearch = new PlaceholderTextField();
        txtSearch.setPlaceholder("Title / Ingredients");
        txtSearch.putClientProperty(FlatClientProperties.STYLE,"" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%);" +

                "focusWidth:0;"+
                "innerFocusWidth:0");
        topBar_Main.add(txtSearch,"grow");
        JButton btnSearch = new JButton();
        btnSearch.setText("Search");
        topBar_Main.add(btnSearch,"grow");

        JCheckBox andrew1 = new JCheckBox("Dairy-Free");

        JCheckBox andrew2 = new JCheckBox("Nut-Free");

        jackStoleThis = new JCheckBox("Owned-Ingredients only");
        topBar_Bottom.add(andrew1);
        topBar_Bottom.add(andrew2);
        topBar_Bottom.add(jackStoleThis);

        center = new JPanel();
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        andrew2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (andrew2.isSelected()) {nut = true; System.out.println("Nut pressed on");}
                if (!andrew2.isSelected()) {nut = false; System.out.println("Nut pressed off");}
            }
        });
        andrew1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (andrew1.isSelected()) {dairy = true; System.out.println("Dairy pressed on");}
                if (!andrew1.isSelected()) {dairy = false; System.out.println("Dairy pressed off");}
            }
        });

        jackStoleThis.addActionListener(g -> {
            importDataSearch = jackStoleThis.isSelected();
            if (jackStoleThis.isSelected()){
                tField.setText("Cooking Companion, Ingredient Filtering");
            }else{
                tField.setText("Cooking Companion, Title Search");
            }
        });
        btnSearch.addActionListener(search -> {
            System.out.println("Search pressed.");
            searchFunction();
//            if(importDataSearch){
//                searchFunctionV2();
//            }else{
//                searchFunction();
//            }
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

        // IMPORT INGREDIENT FILE
        BottomButton importButton = new BottomButton();
        importButton.setText("<html><p>Import\nIngredients</p></html>");
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
                int returnVal = fileChooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        FileImporter fileImporter = new FileImporter(file);
                        String userPantry = fileImporter.pantryToString();
                        searchFunctionV2(userPantry);

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
//                    try {
//                        reader.readList(file);
//                        System.out.println(reader.toString());
//                    } catch (IOException ex) {
//                        throw new RuntimeException(ex);
//                    }
                }
            }
        });
        bottomPanel.add(importButton,"skip, grow");

        BottomButton searchByImport = new BottomButton();
        searchByImport.setText("<html><p>Can make\nrecipes</p></html>");
        bottomPanel.add(searchByImport,"grow");


        BottomButton settings = new BottomButton();
        settings.setText("<html><p>Settings</p></html>");
        bottomPanel.add(settings,"grow");
        settings.addActionListener(settingsBtn -> {
            final JFrame parent = new JFrame();
            JLabel lbl = new JLabel("Database Settings");
            JPanel pnl = new JPanel(new GridLayout(1,1));
            PlaceholderTextField hostAddr = new PlaceholderTextField();
            hostAddr.setPlaceholder("Host Address");
            hostAddr.setText(database.host);
            PlaceholderTextField port = new PlaceholderTextField();
            port.setPlaceholder("Port");
            port.setText(database.port);
            PlaceholderTextField dbtext = new PlaceholderTextField();
            dbtext.setPlaceholder("Database Name");
            dbtext.setText(database.data);
            PlaceholderTextField username = new PlaceholderTextField();
            username.setPlaceholder("User");
            username.setText(database.userN);
            PlaceholderTextField pass = new PlaceholderTextField();
            pass.setPlaceholder("Password");
            pass.setText(database.pass);

            JButton saveSettings = new JButton();
            saveSettings.setText("Save");

            saveSettings.addActionListener(svBttnDB -> {
                database.setConnectionSettings(hostAddr.getText(),port.getText(),username.getText(),pass.getText(),dbtext.getText());
                JOptionPane.showMessageDialog(this,"Updated database connection settings.");
                parent.dispose();
            });

            pnl.add(lbl);
            pnl.add(hostAddr);
            pnl.add(port);
            pnl.add(dbtext);
            pnl.add(username);
            pnl.add(pass);
            pnl.add(saveSettings);

            parent.add(pnl);

            parent.pack();
            parent.setVisible(true);
            parent.setLocationRelativeTo(this);
        });
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
