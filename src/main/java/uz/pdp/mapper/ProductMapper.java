package uz.pdp.mapper;

import org.springframework.stereotype.Component;
import uz.pdp.dto.request.ProductRequest;
import uz.pdp.dto.response.ProductResponse;
import uz.pdp.entity.Category;
import uz.pdp.entity.Product;

import java.util.ArrayList;

@Component
public class ProductMapper {
    public Product toEntity(ProductRequest request, Category category) {
        if (request == null) return null;
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .imageUrls(request.getImageUrls() != null ? new ArrayList<>(request.getImageUrls()) : new ArrayList<>())
                .category(category)
                .active(true)
                .build();
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setImageUrls(product.getImageUrls() != null ? new ArrayList<>(product.getImageUrls()) : new ArrayList<>());
        response.setActive(product.isActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }
        return response;
    }

    public void updateEntity(Product product, ProductRequest request, Category category) {
        if (request == null || product == null) return;
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setImageUrls(request.getImageUrls() != null ? new ArrayList<>(request.getImageUrls()) : new ArrayList<>());
        if (category != null) {
            product.setCategory(category);
        }
    }
}
