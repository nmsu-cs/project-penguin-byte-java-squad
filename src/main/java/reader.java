import java.io.*;
import java.util.ArrayList;

public class reader {

    public static ArrayList<String> readList(File list) throws IOException {
        String filePath = list.getAbsolutePath();
        //String modifiedPath = filePath.concat(list);
        ArrayList<String> shoppingList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        String line;
        while ((line = br.readLine()) != null) {
            shoppingList.add(line);
        }
        return shoppingList;
    }

    public static String toString(ArrayList<String> list) {
        StringBuilder listString = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                listString.append("'").append(list.get(i)).append("'");
            } else {
                listString.append("'").append(list.get(i)).append("',");
            }
        }
        return listString.toString();
    }
}
