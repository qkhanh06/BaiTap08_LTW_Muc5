package vn.iotstar.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Product;

public interface IProductService {
    boolean existsByCategoryId(Long categoryId);
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    Optional<Product> findById(Long id);
    Optional<Product> findByProductName(String name);
    Optional<Product> findByCreateDate(Date createAt);
    Product save(Product entity);
    void delete(Product entity);
    void deleteById(Long id);
    List<Product> findByProductNameContaining(String name);
    Page<Product> findByProductNameContaining(String name, Pageable pageable);
}
