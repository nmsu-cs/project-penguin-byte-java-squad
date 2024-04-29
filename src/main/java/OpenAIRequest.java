import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class OpenAIRequest {
    public static void main(String[] args) throws IOException, InterruptedException {
//        getOpenAI_Image("A Recipe image, of Chicken Funny");

    }

    public static String getOpenAI_Image(String title) throws IOException, InterruptedException{
        //title = "A Recipe image of "+title;
        title = title.replace(",", "").replace("\"","");
        // Generate the image, respond with a json string in a 256x256 image.

        String apiKey = "sk-proj-";
        String endpoint = "https://api.openai.com/v1/images/generations";

        // JSON body data
        String jsonBody = "{" +
                "\"prompt\": \"" + "A recipe of "+title + "\"," + // Note the double quotes around title
                "\"size\": \"256x256\"," +
                "\"model\": \"dall-e-2\"," +
                "\"response_format\": \"b64_json\"" +
                "}";
        System.out.println(jsonBody);
        // Create headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");

        // Create HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Create HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Send request and get response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Print response
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());





        // Convert the response code to json, then grab the response of b64_json (returned from the OpenAI API)
        //String jsonContent = new String(Files.readAllBytes(Paths.get("response.json")));
        JSONObject js = new JSONObject(response.body());
        if (js.has("error")) {
            System.out.println("INAWDOIJAWOIDJAOIWDJOIAWJDOIAJWD");
            return "images/recipe-init.png";
        }
        JSONObject ns = new JSONObject(js.getJSONArray("data").get(0).toString());
        String b64_json = (String) ns.get("b64_json");

        System.out.println(b64_json);

        byte[] imageBytes = Base64.getDecoder().decode(b64_json);

        try {
            // Create a BufferedImage from the byte array
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);
            bis.close();

            // Write the BufferedImage to a file
            File output = new File("images/"+title+".png");
            ImageIO.write(image, "png", output);

            System.out.println("Image saved to: " + output.getAbsolutePath());
            return title+".png";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "images/recipe-init.png";
    }
}
