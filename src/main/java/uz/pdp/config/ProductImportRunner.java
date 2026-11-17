package uz.pdp.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Profile("import-products")
@RequiredArgsConstructor
public class ProductImportRunner implements ApplicationRunner {

    private static final int TOTAL_PRODUCTS = 500;

    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private final Path uploadDir = Path.of("uploads", "products");
    private final Map<String, Path> downloadedImages = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM product", Integer.class);
        if (count != null && count > 0) {
            log.info("Product jadvali bo'sh emas! Import to'xtatildi.");
            return;
        }

        Files.createDirectories(uploadDir);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dummyjson.com/products?limit=0"))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("API xatosi: " + response.statusCode());
        }

        JsonNode products = mapper.readTree(response.body()).path("products");
        if (!products.isArray() || products.isEmpty()) {
            throw new RuntimeException("API mahsulot qaytarmadi!");
        }

        int sourceCount = products.size();
        int imported = 0;

        for (int i = 0; i < TOTAL_PRODUCTS; i++) {
            JsonNode item = products.get(i % sourceCount);
            int round = i / sourceCount;

            String productName = item.path("title").asText("ShopFlow Product");
            if (round > 0) {
                productName = productName + " - Demo " + (i + 1);
            }
            if (productName.length() > 150) {
                productName = productName.substring(0, 150);
            }

            String description = item.path("description").asText("ShopFlow mahsuloti");
            BigDecimal price = BigDecimal.valueOf(item.path("price").asDouble(19.99));
            int quantity = Math.max(item.path("stock").asInt(10), 1);
            String categoryName = mapCategory(item.path("category").asText(""));

            String imageUrl = item.path("thumbnail").asText("");
            if (imageUrl.isBlank() && item.path("images").isArray() && !item.path("images").isEmpty()) {
                imageUrl = item.path("images").get(0).asText("");
            }
            if (imageUrl.isBlank()) {
                throw new RuntimeException("Mahsulot rasmi topilmadi: " + productName);
            }

            jdbc.update(
                    "INSERT INTO category (name, description, created_at) VALUES (?, ?, NOW()) ON CONFLICT (name) DO NOTHING",
                    categoryName,
                    categoryName + " kategoriyasi"
            );

            Long categoryId = jdbc.queryForObject(
                    "SELECT id FROM category WHERE name = ?",
                    Long.class,
                    categoryName
            );
            if (categoryId == null) {
                throw new RuntimeException("Kategoriya topilmadi: " + categoryName);
            }

            String localImage = downloadImage(imageUrl, i + 1);

            Long generatedId = jdbc.queryForObject("""
                    INSERT INTO product (
                        name,
                        description,
                        price,
                        quantity,
                        category_id,
                        active,
                        created_at,
                        updated_at
                    )
                    VALUES (?, ?, ?, ?, ?, TRUE, NOW(), NOW())
                    RETURNING id
                    """,
                    Long.class,
                    productName,
                    description,
                    price,
                    quantity,
                    categoryId
            );

            if (generatedId == null) {
                throw new RuntimeException("Product ID olinmadi: " + productName);
            }

            jdbc.update(
                    "INSERT INTO product_images (product_id, image_url) VALUES (?, ?)",
                    generatedId,
                    localImage
            );

            imported++;
        }

        log.info("Import muvaffaqiyatli tugadi! Jami mahsulot: {}", imported);
    }

    private String downloadImage(String imageUrl, int number) throws Exception {
        URI uri = URI.create(imageUrl);
        String lowerPath = uri.getPath().toLowerCase();

        String extension = ".jpg";
        if (lowerPath.endsWith(".png")) extension = ".png";
        else if (lowerPath.endsWith(".webp")) extension = ".webp";
        else if (lowerPath.endsWith(".jpeg")) extension = ".jpeg";

        String filename = String.format("product-%04d%s", number, extension);
        Path destination = uploadDir.resolve(filename);

        Path cached = downloadedImages.get(imageUrl);
        if (cached != null && Files.exists(cached)) {
            Files.copy(cached, destination, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/products/" + filename;
        }

        HttpRequest imageRequest = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(30))
                .header("User-Agent", "ShopFlow Product Importer")
                .GET()
                .build();

        HttpResponse<byte[]> imageResponse = client.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray());
        if (imageResponse.statusCode() != 200 || imageResponse.body().length == 0) {
            throw new RuntimeException("Rasm yuklanmadi: " + imageUrl);
        }

        String contentType = imageResponse.headers().firstValue("Content-Type").orElse("");
        if (!contentType.startsWith("image/")) {
            throw new RuntimeException("URL rasm qaytarmadi: " + imageUrl);
        }

        Files.write(destination, imageResponse.body());
        downloadedImages.put(imageUrl, destination);

        return "/uploads/products/" + filename;
    }

    private String mapCategory(String value) {
        String c = value.toLowerCase();
        if (c.contains("smartphone")) return "Smartfonlar";
        if (c.contains("laptop")) return "Noutbuklar";
        if (c.contains("mens-shirts") || c.contains("mens-shoes") || c.contains("mens-watches"))
            return "Erkaklar kiyimi";
        if (c.contains("womens-dresses") || c.contains("womens-shoes") || c.contains("womens-watches") || c.contains("tops"))
            return "Ayollar kiyimi";
        if (c.contains("shoes")) return "Oyoq kiyimlar";
        if (c.contains("furniture") || c.contains("home-decoration") || c.contains("lighting"))
            return "Uy jihozlari";
        if (c.contains("beauty") || c.contains("skin") || c.contains("fragrances"))
            return "Go'zallik va parvarish";
        if (c.contains("groceries")) return "Oziq-ovqat mahsulotlari";
        if (c.contains("vehicle") || c.contains("motorcycle")) return "Aksessuarlar";
        return "Audio qurilmalar";
    }
}
