package uz.pdp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import uz.pdp.dto.request.CategoryRequest;
import uz.pdp.dto.response.CategoryResponse;
import uz.pdp.entity.Category;
import uz.pdp.exception.ResourceNotFoundException;
import uz.pdp.mapper.CategoryMapper;
import uz.pdp.repo.CategoryRepo;
import uz.pdp.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepo.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategoriya topilmadi, ID: " + id));
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepo.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DataIntegrityViolationException("Bunday nomdagi kategoriya allaqachon mavjud");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepo.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Kategoriya topilmadi, ID: " + id));
        String newName = request.getName().trim();
        if (!category.getName().equalsIgnoreCase(newName) && categoryRepo.existsByNameIgnoreCase(newName)) {
            throw new DataIntegrityViolationException("Bunday nomdagi kategoriya allaqachon mavjud");
        }
        categoryMapper.updateEntity(category, request);
        return categoryMapper.toResponse(categoryRepo.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Kategoriya topilmadi, ID: " + id));
        categoryRepo.delete(category);
    }
}
