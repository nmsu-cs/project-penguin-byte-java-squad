import java.sql.*;
import java.util.ArrayList;
public class Recipe{
    private int id;
    private String title;
    private String[] ingredients;
    private String[] instructions;
    private String link;
    private String[] NER;
    public Recipe (int myID, String name, String[] myIng, String[] myIns, String url, String[] myNER) {
        this.id = myID;
        this.title = name;
        this.ingredients = myIng;
        this.instructions = myIns;
        this.link = url;
        this.NER = myNER;
    }

    public int getID() {
        return id;
    }

    public String getTitle(){
        return title;
    }
    public String[] getIngredients(){
        return ingredients;
    }
    public String[] getInstructions() {
        return instructions;
    }
    public String getLink() {
        return link;
    }
    public String[] getNER() {
        return NER;
    }

    public static ArrayList<Recipe> getList(ResultSet rs) throws SQLException{
        ArrayList<Recipe> table = new ArrayList<Recipe>();
        String[] ingredients = null;
        String[] instructions = null;
        String[] NER = null;
        while(rs.next()){
            ingredients = rs.getString("ingredients").split(",");
            instructions = rs.getString("directions").split(",");
            NER = rs.getString("NER").split(",");
            Recipe curr = new Recipe(rs.getInt("id"), rs.getString("title"), ingredients, instructions, rs.getString("link"), NER);
            table.add(curr);
        }
        return table;
    }

    public static void printRecipe(Recipe r){
        System.out.println(r.title);
        System.out.println("  id: " + r.id);
        System.out.println("  ingredients: ");
        for(int i = 0; i < r.ingredients.length; i++) {
            System.out.println("    " + r.ingredients[i]);
        }
        System.out.println("  instructions: ");
        for(int j = 0; j < r.instructions.length; j++) {
            System.out.println("    " + r.instructions[j]);
        }
        System.out.println("  link: " + r.link);
        System.out.println("  NER: ");
        for(int i = 0; i < r.NER.length; i++) {
            System.out.println("    " + r.NER[i]);
        }
    }
}
