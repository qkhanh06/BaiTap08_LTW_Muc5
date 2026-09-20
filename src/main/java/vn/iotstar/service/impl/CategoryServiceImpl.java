package vn.iotstar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import vn.iotstar.entity.Category;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.service.ICategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category save(Category entity) {
        if (entity.getCategoryId() != null) {
            categoryRepository.findById(entity.getCategoryId()).ifPresent(old -> {
                if (!StringUtils.hasText(entity.getIcon())) {
                    entity.setIcon(old.getIcon());
                }
            });
        }
        return categoryRepository.save(entity);
    }

    @Override public List<Category> findAll() { return categoryRepository.findAll(); }
    @Override public Page<Category> findAll(Pageable pageable) { return categoryRepository.findAll(pageable); }
    @Override public Optional<Category> findById(Long id) { return categoryRepository.findById(id); }
    @Override public Optional<Category> findByCategoryName(String name) { return categoryRepository.findByCategoryName(name); }
    @Override public void delete(Category entity) { categoryRepository.delete(entity); }
    @Override public void deleteById(Long id) { categoryRepository.deleteById(id); }
    @Override public List<Category> findByCategoryNameContaining(String name) { return categoryRepository.findByCategoryNameContaining(name); }
    @Override public Page<Category> findByCategoryNameContaining(String name, Pageable pageable) { return categoryRepository.findByCategoryNameContaining(name, pageable); }
}
