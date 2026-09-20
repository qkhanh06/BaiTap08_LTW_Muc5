package vn.iotstar.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.service.IProductService;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;

    @Override public boolean existsByCategoryId(Long categoryId) { return productRepository.existsByCategoryCategoryId(categoryId); }

    @Override
    public Product save(Product entity) {
        if (entity.getProductId() != null) {
            productRepository.findById(entity.getProductId()).ifPresent(old -> {
                if (!StringUtils.hasText(entity.getImages())) {
                    entity.setImages(old.getImages());
                }
                if (entity.getCreateDate() == null) {
                    entity.setCreateDate(old.getCreateDate());
                }
            });
        }
        return productRepository.save(entity);
    }

    @Override public List<Product> findAll() { return productRepository.findAll(); }
    @Override public Page<Product> findAll(Pageable pageable) { return productRepository.findAll(pageable); }
    @Override public Optional<Product> findById(Long id) { return productRepository.findById(id); }
    @Override public Optional<Product> findByProductName(String name) { return productRepository.findByProductName(name); }
    @Override public Optional<Product> findByCreateDate(Date createAt) { return productRepository.findByCreateDate(createAt); }
    @Override public void delete(Product entity) { productRepository.delete(entity); }
    @Override public void deleteById(Long id) { productRepository.deleteById(id); }
    @Override public List<Product> findByProductNameContaining(String name) { return productRepository.findByProductNameContaining(name); }
    @Override public Page<Product> findByProductNameContaining(String name, Pageable pageable) { return productRepository.findByProductNameContaining(name, pageable); }
}
