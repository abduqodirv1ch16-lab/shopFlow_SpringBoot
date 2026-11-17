package uz.pdp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.dto.request.ProductRequest;
import uz.pdp.dto.response.ProductResponse;
import uz.pdp.entity.Category;
import uz.pdp.entity.Product;
import uz.pdp.exception.ResourceNotFoundException;
import uz.pdp.mapper.ProductMapper;
import uz.pdp.repo.CategoryRepo;
import uz.pdp.repo.ProductRepo;
import uz.pdp.service.ProductService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponse> getAllProduct(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepo.findFilteredProducts(categoryId, minPrice, maxPrice, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productMapper.toResponse(
                productRepo.findByIdAndActiveTrue(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Mahsulot topilmadi, ID: " + id))
        );
    }

    @Override
    public Page<ProductResponse> searchProduct(String keyword, Pageable pageable) {
        return productRepo.findByNameContainingIgnoreCaseAndActiveTrue(keyword, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategoriya topilmadi, ID: " + request.getCategoryId()));
        Product product = productMapper.toEntity(request, category);
        Product saved = productRepo.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mahsulot topilmadi, ID: " + id));
        Category category = categoryRepo.findById(request.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Kategoriya topilmadi, ID: " + request.getCategoryId())
        );
        productMapper.updateEntity(product, request, category);
        return productMapper.toResponse(productRepo.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mahsulot topilmadi, ID: " + id));
        product.setActive(false);
        productRepo.save(product);
    }
}
