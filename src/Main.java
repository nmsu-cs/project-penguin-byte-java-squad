import javax.swing.*;
import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;
import java.sql.*;
public class Main {
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
            System.out.println("");
        }
    }
    public static void main(String[] args) throws SQLException {
        try{
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        database = new database();
    }



}