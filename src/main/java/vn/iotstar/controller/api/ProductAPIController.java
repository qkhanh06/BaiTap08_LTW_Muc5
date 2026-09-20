package vn.iotstar.controller.api;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductAPIController {
    private final IProductService productService;
    private final ICategoryService categoryService;
    private final IStorageService storageService;

    @GetMapping
    public ResponseEntity<?> getAllProduct() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Product>> search(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<Product> result = StringUtils.hasText(keyword)
                ? productService.findByProductNameContaining(keyword.trim(), pageable)
                : productService.findAll(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getProduct")
    public ResponseEntity<?> getProduct(@RequestParam("id") Long id) {
        return productService.findById(id)
                .<ResponseEntity<?>>map(product -> ResponseEntity.ok(new Response(true, "Thành công", product)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "Không tìm thấy Product", null)));
    }

    @PostMapping(value = "/addProduct", consumes = "multipart/form-data")
    public ResponseEntity<?> addProduct(
            @RequestParam String productName,
            @RequestParam Integer quantity,
            @RequestParam Double unitPrice,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam String description,
            @RequestParam Double discount,
            @RequestParam Short status,
            @RequestParam Long categoryId) {
        String error = validateProduct(productName, quantity, unitPrice, description, discount, status);
        if (error != null) {
            return ResponseEntity.badRequest().body(new Response(false, error, null));
        }
        if (productService.findByProductName(productName.trim()).isPresent()) {
            return ResponseEntity.badRequest().body(new Response(false, "Sản phẩm này đã tồn tại trong hệ thống", null));
        }
        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(false, "Category không tồn tại", null));
        }
        Product product = new Product();
        applyFields(product, productName, quantity, unitPrice, description, discount, status, categoryOpt.get());
        product.setCreateDate(new Date());
        storeImageIfPresent(product, imageFile, false);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(true, "Thêm thành công", productService.save(product)));
    }

    @PutMapping(value = "/updateProduct", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProduct(
            @RequestParam Long productId,
            @RequestParam String productName,
            @RequestParam Integer quantity,
            @RequestParam Double unitPrice,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam String description,
            @RequestParam Double discount,
            @RequestParam Short status,
            @RequestParam Long categoryId) {
        String error = validateProduct(productName, quantity, unitPrice, description, discount, status);
        if (error != null) {
            return ResponseEntity.badRequest().body(new Response(false, error, null));
        }
        Optional<Product> productOpt = productService.findById(productId);
        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy Product", null));
        }
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(false, "Category không tồn tại", null));
        }
        Optional<Product> sameName = productService.findByProductName(productName.trim());
        if (sameName.isPresent() && !sameName.get().getProductId().equals(productId)) {
            return ResponseEntity.badRequest().body(new Response(false, "Sản phẩm này đã tồn tại trong hệ thống", null));
        }
        Product product = productOpt.get();
        applyFields(product, productName, quantity, unitPrice, description, discount, status, categoryOpt.get());
        storeImageIfPresent(product, imageFile, true);
        return ResponseEntity.ok(new Response(true, "Cập nhật thành công", productService.save(product)));
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<?> deleteProduct(@RequestParam Long productId) {
        Optional<Product> productOpt = productService.findById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy Product", null));
        }
        Product product = productOpt.get();
        String image = product.getImages();
        productService.delete(product);
        try { storageService.delete(image); } catch (Exception ignored) { }
        return ResponseEntity.ok(new Response(true, "Xóa thành công", product));
    }

    private void applyFields(Product product, String productName, Integer quantity, Double unitPrice,
                             String description, Double discount, Short status, Category category) {
        product.setProductName(productName.trim());
        product.setQuantity(quantity);
        product.setUnitPrice(unitPrice);
        product.setDescription(description);
        product.setDiscount(discount);
        product.setStatus(status);
        product.setCategory(category);
    }

    private String validateProduct(String name, Integer quantity, Double price, String description,
                                   Double discount, Short status) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(description)) {
            return "Tên và mô tả Product không được để trống";
        }
        if (quantity == null || quantity < 0 || price == null || !Double.isFinite(price) || price < 0
                || discount == null || !Double.isFinite(discount) || discount < 0) {
            return "Số lượng, giá và giảm giá phải là số không âm";
        }
        if (status == null || (status != 0 && status != 1)) {
            return "Status phải là 0 hoặc 1";
        }
        return null;
    }

    private void storeImageIfPresent(Product product, MultipartFile imageFile, boolean deleteOld) {
        if (imageFile == null || imageFile.isEmpty()) return;
        String oldImage = product.getImages();
        String filename = storageService.getSorageFilename(imageFile, UUID.randomUUID().toString());
        storageService.store(imageFile, filename);
        product.setImages(filename);
        if (deleteOld) {
            try { storageService.delete(oldImage); } catch (Exception ignored) { }
        }
    }
}
