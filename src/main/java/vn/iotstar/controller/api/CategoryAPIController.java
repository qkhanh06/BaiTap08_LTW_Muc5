package vn.iotstar.controller.api;

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
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryAPIController {
    private final ICategoryService categoryService;
    private final IProductService productService;
    private final IStorageService storageService;

    @GetMapping
    public ResponseEntity<?> getAllCategory() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Category>> search(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<Category> result = StringUtils.hasText(keyword)
                ? categoryService.findByCategoryNameContaining(keyword.trim(), pageable)
                : categoryService.findAll(pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/getCategory")
    public ResponseEntity<?> getCategory(@RequestParam("id") Long id) {
        return categoryService.findById(id)
                .<ResponseEntity<?>>map(category -> ResponseEntity.ok(new Response(true, "Thành công", category)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "Không tìm thấy Category", null)));
    }

    @PostMapping(value = "/addCategory", consumes = "multipart/form-data")
    public ResponseEntity<?> addCategory(
            @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        if (!StringUtils.hasText(categoryName)) {
            return ResponseEntity.badRequest().body(new Response(false, "Tên Category không được để trống", null));
        }
        if (categoryService.findByCategoryName(categoryName.trim()).isPresent()) {
            return ResponseEntity.badRequest().body(new Response(false, "Category đã tồn tại trong hệ thống", null));
        }
        Category category = new Category();
        category.setCategoryName(categoryName.trim());
        if (icon != null && !icon.isEmpty()) {
            String filename = storageService.getSorageFilename(icon, UUID.randomUUID().toString());
            storageService.store(icon, filename);
            category.setIcon(filename);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(true, "Thêm thành công", categoryService.save(category)));
    }

    @PutMapping(value = "/updateCategory", consumes = "multipart/form-data")
    public ResponseEntity<?> updateCategory(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        Optional<Category> optional = categoryService.findById(categoryId);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy Category", null));
        }
        if (!StringUtils.hasText(categoryName)) {
            return ResponseEntity.badRequest().body(new Response(false, "Tên Category không được để trống", null));
        }
        Optional<Category> sameName = categoryService.findByCategoryName(categoryName.trim());
        if (sameName.isPresent() && !sameName.get().getCategoryId().equals(categoryId)) {
            return ResponseEntity.badRequest().body(new Response(false, "Category đã tồn tại trong hệ thống", null));
        }
        Category category = optional.get();
        category.setCategoryName(categoryName.trim());
        if (icon != null && !icon.isEmpty()) {
            String oldIcon = category.getIcon();
            String filename = storageService.getSorageFilename(icon, UUID.randomUUID().toString());
            storageService.store(icon, filename);
            category.setIcon(filename);
            try { storageService.delete(oldIcon); } catch (Exception ignored) { }
        }
        return ResponseEntity.ok(new Response(true, "Cập nhật thành công", categoryService.save(category)));
    }

    @DeleteMapping("/deleteCategory")
    public ResponseEntity<?> deleteCategory(@RequestParam("categoryId") Long categoryId) {
        Optional<Category> optional = categoryService.findById(categoryId);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy Category", null));
        }
        Category category = optional.get();
        if (productService.existsByCategoryId(categoryId)) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Không thể xóa Category đang có Product", null));
        }
        try {
            String icon = category.getIcon();
            categoryService.delete(category);
            try { storageService.delete(icon); } catch (Exception ignored) { }
            return ResponseEntity.ok(new Response(true, "Xóa thành công", category));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Không thể xóa Category đang có Product", null));
        }
    }
}
