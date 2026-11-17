package uz.pdp.mapper;

import org.springframework.stereotype.Component;
import uz.pdp.dto.request.CategoryRequest;
import uz.pdp.dto.response.CategoryResponse;
import uz.pdp.entity.Category;

@Component
public class CategoryMapper {
    public Category toEntity(CategoryRequest request) {
        if (request == null) return null;
        return Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();
    }

    public CategoryResponse toResponse(Category category) {
        if (category == null) return null;
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }

    public void updateEntity(Category category, CategoryRequest request) {
        if (category == null || request == null) return;
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
    }
}
