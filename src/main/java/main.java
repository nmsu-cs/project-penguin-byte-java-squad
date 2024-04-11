import javax.swing.*;
import com.formdev.flatlaf.FlatDarkLaf;
import com.mysql.cj.protocol.x.XProtocolRowInputStream;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.sql.*;


public class main {
    private static database database;

    private static void setupIngredientsTable() throws SQLException {
        Statement stmt = database.getConnection().createStatement();
        ResultSet data = stmt.executeQuery("SELECT id,NER FROM dataset_600k_csv");

        while(data.next()){
            int id = data.getInt("id");
            System.out.println("Processing Ingredients for: "+id);
            String ner = data.getString("NER");
            ner = ner.replace("[","");
            ner = ner.replace("]","");
            String[] nersplit = ner.split("\",");

            PreparedStatement prep = database.getConnection().prepareStatement("INSERT INTO ingredients VALUES (?,?)");
            for(String str : nersplit){
                str = str.replace("\"","");
                str = str.replace(",","");
                System.out.print(str+", ");
                prep.setString(1,str);
                prep.setInt(2,id);

                prep.addBatch();

            }
            prep.executeBatch();
            System.out.println();
        }
    }
    public static void main(String[] args) throws SQLException {
        // Set FlatLaf Dark as the application's look and feel
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf Dark");
            throw new RuntimeException(e);
        }
        SwingUtilities.invokeLater(main::createAndShowGUI);

        database = new database();

    }//of main

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create the top navigation bar
        JPanel navBar = new JPanel();
        navBar.add(new JButton("Settings"));
        navBar.add(new JButton("Ingredients"));
//        navBar.add(new JButton("Poop"));

        // Create the search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Cooking Companion", SwingConstants.CENTER);
        titleLabel.setFont(new Font("San Serif", Font.BOLD, 32)); // Set font size and style
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add some padding around the title

        JTextField searchField = new JTextField(20);
//        String recipe = searchField.getText();
        JPanel searchFieldPanel = new JPanel(); // A panel to hold the search field and butt// n
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recipe = searchField.getText();
                System.out.println(recipe);
                try {
                    Connection c = database.getConnection();
                    Statement stmt = c.createStatement();
                    ResultSet rs;
                    rs = stmt.executeQuery("select * from dataset D where D.title like" + "'%" + recipe + "%'");
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int numCols = rsmd.getColumnCount();
                    while (rs.next()) {
                        System.out.println(rs.getString("title"));

                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        searchFieldPanel.add(searchField);
        searchFieldPanel.add(searchButton);

        searchPanel.add(titleLabel, BorderLayout.NORTH); // Add the title label at the top of the search panel
        searchPanel.add(searchFieldPanel, BorderLayout.CENTER); // Add the search field and button below the title

        // Main panel with BorderLayout to position navBar at the top and search in the center
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(navBar, BorderLayout.NORTH);

        // A trick to center the searchPanel in the middle of the frame
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(searchPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }// of create method

    public static void searchAction(String recipe){

    }

}//of class