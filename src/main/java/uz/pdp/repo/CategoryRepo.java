package uz.pdp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.entity.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
}
