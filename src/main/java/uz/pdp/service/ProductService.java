package uz.pdp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.pdp.dto.request.ProductRequest;
import uz.pdp.dto.response.ProductResponse;

import java.math.BigDecimal;

public interface ProductService {
    Page<ProductResponse> getAllProduct(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    ProductResponse getProductById(Long id);
    Page<ProductResponse> searchProduct(String keyword, Pageable pageable);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
