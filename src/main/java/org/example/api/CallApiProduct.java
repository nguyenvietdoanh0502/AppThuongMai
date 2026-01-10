package org.example.api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.example.model.Product;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CallApiProduct {
    private static final String BASE_URL = "https://fakestoreapi.com/products";
    private final HttpClient httpClient;
    public CallApiProduct(){
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }
    private List<Product> parseJsonToProducts(String json){
        List<Product> products = new ArrayList<>();
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        Random random = new Random();
        for(JsonElement element: jsonArray){
            JsonObject jsonObject = element.getAsJsonObject();
            Product p = new Product();
            p.setProductId(jsonObject.get("id").getAsInt());
            p.setTitle(jsonObject.get("title").getAsString());
            p.setPrice(jsonObject.get("price").getAsDouble());
            p.setDescription(jsonObject.get("description").getAsString());
            p.setCategory(jsonObject.get("category").getAsString());
            p.setImage(jsonObject.get("image").getAsString());
            if(jsonObject.has("rating")){
                JsonObject ratingObject = jsonObject.get("rating").getAsJsonObject();
                p.setRatingRate(ratingObject.get("rate").getAsDouble());
                p.setRatingCount(ratingObject.get("count").getAsInt());
            }
            p.setQuantity(random.nextInt(100)+1);
            products.add(p);
        }
        return products;
    }
    public List<Product> getAllProducts() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().header("Content-Type", "application/json").build();
        HttpResponse<String> response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseJsonToProducts(response.body());
        } else {
            return new ArrayList<>();
        }
    }
}
