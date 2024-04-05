package com.recipeapplication;
import java.io.*;
import java.util.ArrayList;

public class Reader {

  public static ArrayList<String> readList(String list) throws IOException {
    String filePath = new File("").getAbsolutePath();
    String modifiedPath = filePath.concat(list);
    ArrayList<String> shoppingList = new ArrayList<>();
    BufferedReader br = new BufferedReader(new FileReader(modifiedPath));

    String line;
    while ((line = br.readLine()) != null) {
      shoppingList.add(line);
    }
    return shoppingList;
  }
}
