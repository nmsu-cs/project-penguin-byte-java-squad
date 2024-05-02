import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class UI extends JFrame {
    private static database database;
    private int paginationCurrent = 0;
    private int pagination_Increments = 10; // Changes the limit by.
    private boolean importDataSearch = false;
    public String userPantry;
    public UI() {
        init();
        database = new database();
    }

    private JPanel center;
    private PlaceholderTextField txtSearch;
    private JCheckBox jackStoleThis;

    public static boolean debug = false;
    private boolean nut;
    private String[] nutList = {"Almond", "Beechnut", "Brazil nut", "Butternut", "Cashew", "Chestnut", "Chinquapin nut", "Coconut",
                                "Filbert", "hazelnut", "Gianduja", "Ginkgo nut", "Hickory nut", " nut ", "nuts", "peanut",
                                "Litchi", "lichee", "lychee nut", "Macadamia nut", "Marzipan", "Nangai nut",
                                "Pecan", "Pesto", "Pili nut", "Pine nut", "Pistachio", "Praline", "Shea nut", "Walnut"};
    private boolean dairy;
    private String[] dairyList = {"milk", "cheese", "sour cream", "heavy cream", "whipping cream", "butter", "yogurt", "yoghurt",
                                  "cheddar", "mozzarella", "brie", "feta", "gouda", "camembert", "blue cheese", "bleu cheese",
                                  "parmesean", "swiss cheese"};



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
                for (int d = 0; d < dairyList.length; d++) {
                    query += " and ingredients NOT LIKE \"%" + dairyList[d] + "%\"";
                }
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
        if(importedPantry == null){
            if (debug) {System.out.println("No Pantry Imported");}
            JOptionPane.showMessageDialog(this,
                    "You must import a pantry before we can use this option.");

            return;
        }
        try {
            //String importedPantry = "";
            center.removeAll();
            Connection c = database.getConnection();
            Statement stmt = c.createStatement();
            ResultSet rs;
            String SQL_Query = "select * from dataset D where D.id in " +
                    "(select T1.id " +
                    "from (select I.id, count(*) as totalIngredients " +
                    "from ingredients I " +
                    "group by I.id) as T1, " +
                    "(select E.id, count(*) as total " +
                    "from ingredients E " +
                    "where E.ingredient_name in (" + importedPantry + ") " +
                    "group by E.id) as T2 " +
                    "where T1.id = T2.id and T1.totalIngredients = T2.total)";

            if(jackStoleThis.isSelected()){
                SQL_Query+=" AND title LIKE \"%"+txtSearch.getText()+"%\"";
            }
            if (dairy) {
                for (int d = 0; d < dairyList.length; d++) {
                    SQL_Query += " and ingredients NOT LIKE \"%" + dairyList[d] + "%\"";
                }
            }
            if (nut) {
                for (int i = 0; i < nutList.length; i++) {
                    SQL_Query += " and ingredients NOT LIKE \"%" + nutList[i] + "%\"";
                }
            }
            SQL_Query += " LIMIT " + pagination_Increments + " OFFSET " + paginationCurrent;
            if (debug) {System.out.println(SQL_Query);}
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

    private JPanel mainPane() {
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
                if (andrew2.isSelected()) {nut = true; if (debug) {System.out.println("Nut pressed on");}}
                if (!andrew2.isSelected()) {nut = false; if (debug) {System.out.println("Nut pressed off");}}
            }
        });
        andrew1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (andrew1.isSelected()) {dairy = true; if (debug) {System.out.println("Dairy pressed on");}}
                if (!andrew1.isSelected()) {dairy = false; if (debug) {System.out.println("Dairy pressed off");}}
            }
        });

        jackStoleThis.addActionListener(g -> {
            importDataSearch = jackStoleThis.isSelected();
            if (jackStoleThis.isSelected()){
                tField.setText("Cooking Companion, Pantry Search");
            }else{
                tField.setText("Cooking Companion, Title Search");
            }
        });
        btnSearch.addActionListener(search -> {
            if (debug) {System.out.println("Search pressed.");}
            paginationCurrent = 0;
            searchFunction();
            if(jackStoleThis.isSelected()){
                searchFunctionV2(userPantry);
            }else{
                searchFunction();
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

        // IMPORT INGREDIENT FILE
        BottomButton importButton = new BottomButton();
        importButton.setText("<html><p>Import\nIngredients</p></html>");
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        } else {
                            String fileName = f.getName().toLowerCase();
                            return fileName.endsWith(".txt");
                        }
                    }
                    @Override
                    public String getDescription() {
                        return "Text Files (*.txt)";
                    }
                });
                fileChooser.setAcceptAllFileFilterUsed(false);
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        FileImporter fileImporter = new FileImporter(file);
                        userPantry = fileImporter.pantryToString();
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
        searchByImport.setText("Help");
        bottomPanel.add(searchByImport,"grow");
        searchByImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane helpPop = new JOptionPane();
                String helpText = "INSTRUCTIONS:\n" +
                                  "For basic searches, simply enter a keyword\n" +
                                  "or name of a recipe you would like to search for!\n\n" +
                                  "Scroll through the recipes, and if you see one you\n" +
                                  "like, simply click on it to find out more details!\n\n" +
                                  "If you want to find recipes that accomodate to \n" +
                                  "food allergies like dairy or tree nuts, mark the\n" +
                                  "checkbox below the search bar to enable said\n" +
                                  "filter.\n\n" +
                                  "To find recipes that you can make with your\n" +
                                  "current ingredients, you must do the following:\n" +
                                  " - Create a .txt file\n" +
                                  " - In said file, write out ingredients you own,\n" +
                                  "   one ingredient per line\n" +
                                  " - Once you have this file on your device, press\n" +
                                  "   the import ingredients button and select your\n" +
                                  "   file.\n" +
                                  " - Once the file is uploaded, a default search\n" +
                                  "   will occur that finds recipes using your list.\n" +
                                  " - If you would then like to search for specific\n" +
                                  "   recipes based on your list, type your search\n" +
                                  "   and check the Owned Ingredients Only checkbox.";
                helpPop.showInternalMessageDialog(null,helpText, "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        BottomButton settings = new BottomButton();
        settings.setText("<html><p>Settings</p></html>");
        bottomPanel.add(settings,"grow");
        AtomicBoolean bounce = new AtomicBoolean(false);

        settings.addActionListener(settingsBtn -> {
            final JFrame parent = new JFrame();
            JLabel lbl = new JLabel("Database Settings");
            JPanel pnl = new JPanel(new GridLayout(8,1));
            pnl.putClientProperty(FlatClientProperties.STYLE,"" +
                    "arc:10;" +
                    "[light]background:darken(@background,5%);" +
                    "[dark]background:lighten(@background,5%)");
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
            saveSettings.putClientProperty(FlatClientProperties.STYLE,"" +
                    "arc:10;" +
                    "[light]background:darken(@background,5%);" +
                    "[dark]background:lighten(@background,5%);" +
                    "borderWidth:0;"+
                    "focusPainted:false;"+
                    "innerFocusWidth:0");
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
            pnl.add(saveSettings, "wrap");
            JButton ldTog = new JButton("Light Mode");
            if (bounce.get()) {ldTog.setText("Dark Mode");}
            ldTog.putClientProperty(FlatClientProperties.STYLE,"" +
                    "arc:10;" +
                    "[light]background:darken(@background,5%);" +
                    "[dark]background:lighten(@background,5%);" +
                    "borderWidth:0;"+
                    "focusPainted:false;"+
                    "innerFocusWidth:0");
            ldTog.addActionListener(p -> {
                try {
                    if (bounce.get()) {
                        ldTog.setText("Light Mode");
                        bounce.set(false);
                        UIManager.setLookAndFeel(new FlatMacDarkLaf());
                    }
                    else {
                        ldTog.setText("Dark Mode");
                        bounce.set(true);
                        UIManager.setLookAndFeel(new FlatMacLightLaf());
                    }
                } catch (UnsupportedLookAndFeelException e) {
                    throw new RuntimeException(e);
                }
                SwingUtilities.updateComponentTreeUI(this);
                SwingUtilities.updateComponentTreeUI(parent);
                pnl.repaint();
                pnl.revalidate();
            });

            pnl.add(ldTog);

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

    private static void setupIngredientsTable() throws SQLException {
        Statement stmt = database.getConnection().createStatement();
        ResultSet data = stmt.executeQuery("SELECT id,NER FROM dataset LIMIT 1000 OFFSET 15252"); // FOR OFFSET, Double check column ids, this offset doesn't directly reference correctly.
        while(data.next()){
            int id = data.getInt("id");
            if (debug) {System.out.println("Processing Ingredients for: "+id);}
            String ner = data.getString("NER");
            ner = ner.replace("[","");
            ner = ner.replace("]","");
            String[] nersplit = ner.split("\", ");

            PreparedStatement prep = database.getConnection().prepareStatement("INSERT INTO new_ingredients VALUES (?,?)");
            for(String str : nersplit){
                //System.out.println(str);
                str = str.replace("\"","");
                str = str.replace(", ","");
                if (debug) System.out.print(str+",");
                prep.setString(2,str);
                prep.setInt(1,id);

                prep.addBatch();

            }
            if (debug) System.out.println(prep);
            prep.executeBatch();
            if (debug) System.out.println();
        }
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
