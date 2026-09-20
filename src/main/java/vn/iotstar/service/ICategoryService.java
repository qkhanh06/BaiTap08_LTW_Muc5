package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Category;

public interface ICategoryService {
    List<Category> findAll();
    Page<Category> findAll(Pageable pageable);
    Optional<Category> findById(Long id);
    Optional<Category> findByCategoryName(String name);
    Category save(Category entity);
    void delete(Category entity);
    void deleteById(Long id);
    List<Category> findByCategoryNameContaining(String name);
    Page<Category> findByCategoryNameContaining(String name, Pageable pageable);
}
