package vn.iotstar.controller.graphql;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.entity.*;
import vn.iotstar.repository.*;

@Controller
@RequiredArgsConstructor
@Transactional
public class CatalogGraphqlController {
    private final ProductRepository products;
    private final CategoryRepository categories;

    public record CategoryInput(@NotBlank @Size(max = 255) String categoryName,
                                @Size(max = 255) String icon) {}
    public record ProductInput(@NotBlank @Size(max = 500) String productName,
            @Min(0) int quantity, @DecimalMin("0") double unitPrice,
            @Size(max = 200) String images, @NotNull @Size(max = 500) String description,
            @DecimalMin("0") @DecimalMax("100") double discount,
            @Min(0) @Max(1) int status, @NotNull Long categoryId) {}

    private PageRequest paging(int page, int size, String id) {
        if (page < 0 || size < 1 || size > 100)
            throw new IllegalArgumentException("Trang phải >= 0; kích thước trang từ 1 đến 100.");
        return PageRequest.of(page, size, Sort.by(id).descending());
    }

    @QueryMapping
    public List<Product> homeProducts(@Argument Long categoryId) {
        return categoryId == null ? products.findAllByOrderByUnitPriceAscProductIdAsc()
                : products.findByCategoryCategoryIdOrderByUnitPriceAscProductIdAsc(categoryId);
    }

    @QueryMapping
    public List<Category> allCategories() { return categories.findAll(Sort.by("categoryName", "categoryId")); }

    @QueryMapping
    public Page<Product> products(@Argument String keyword, @Argument int page, @Argument int size) {
        return products.findByProductNameContainingIgnoreCase(keyword == null ? "" : keyword.trim(),
                paging(page, size, "productId"));
    }

    @QueryMapping
    public Page<Category> categories(@Argument String keyword, @Argument int page, @Argument int size) {
        return categories.findByCategoryNameContainingIgnoreCase(keyword == null ? "" : keyword.trim(),
                paging(page, size, "categoryId"));
    }

    @QueryMapping
    public Product product(@Argument Long id) {
        return products.findById(id).orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm."));
    }

    @QueryMapping
    public Category category(@Argument Long id) {
        return categories.findById(id).orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục."));
    }

    @MutationMapping
    public Category createCategory(@Argument @Valid CategoryInput input) {
        return saveCategory(new Category(), input);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument @Valid CategoryInput input) {
        return saveCategory(category(id), input);
    }

    private Category saveCategory(Category entity, CategoryInput input) {
        entity.setCategoryName(input.categoryName().trim());
        entity.setIcon(input.icon());
        return categories.save(entity);
    }

    @MutationMapping
    public boolean deleteCategory(@Argument Long id) {
        Category entity = category(id);
        if (products.existsByCategoryCategoryId(id))
            throw new IllegalArgumentException("Danh mục còn sản phẩm. Hãy chuyển hoặc xóa sản phẩm trước.");
        categories.delete(entity);
        categories.flush();
        return true;
    }

    @MutationMapping
    public Product createProduct(@Argument @Valid ProductInput input) {
        Product entity = new Product();
        entity.setCreateDate(new Date());
        return saveProduct(entity, input);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument @Valid ProductInput input) {
        return saveProduct(product(id), input);
    }

    private Product saveProduct(Product entity, ProductInput input) {
        if (!Double.isFinite(input.unitPrice()) || !Double.isFinite(input.discount()))
            throw new IllegalArgumentException("Giá và giảm giá phải là số hữu hạn.");
        entity.setCategory(category(input.categoryId()));
        entity.setProductName(input.productName().trim());
        entity.setQuantity(input.quantity());
        entity.setUnitPrice(input.unitPrice());
        entity.setImages(input.images());
        entity.setDescription(input.description());
        entity.setDiscount(input.discount());
        entity.setStatus((short) input.status());
        return products.save(entity);
    }

    @MutationMapping
    public boolean deleteProduct(@Argument Long id) {
        products.delete(product(id));
        return true;
    }
}

