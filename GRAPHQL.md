# Phần 5 — GraphQL + AJAX + Thymeleaf

## Chạy ứng dụng

Dùng **JDK 17 hoặc JDK 21** và Maven 3.6.3 trở lên (Spring Boot 3.1.5).
Project vẫn biên dịch với mức Java 17 trong pom.xml; không cần đổi thành 21.
Thiết lập MySQL như README chính, sau đó chạy:

```powershell
mvn test
mvn spring-boot:run
```

Kiểm thử dùng H2 riêng, không thay đổi cơ sở dữ liệu MySQL đang dùng.
Có thể dùng script Windows với JAVA_HOME trỏ tới JDK 17/21. Nếu JAVA_HOME
chưa được đặt, script chọn JDK 17 portable trong `.tools` (nếu có).
Maven portable trong `.tools` được ưu tiên khi có sẵn:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 test
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 test -Browser
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 start
```

Script chỉ thay đổi biến môi trường của tiến trình đang chạy. Thư mục `.tools`
được bỏ qua bởi Git. Khi chuyển dự án sang máy khác, cài JDK 17 và Maven,
đặt JAVA_HOME trỏ tới JDK 17 hoặc 21 rồi chạy các lệnh Maven ở trên.

## Các trang

| Đường dẫn | Chức năng |
| --- | --- |
| / hoặc /home | Tất cả sản phẩm theo unitPrice tăng dần; chọn một danh mục để lọc |
| /graphql/categories | CRUD danh mục, tìm kiếm tên không phân biệt hoa thường, phân trang |
| /graphql/products | CRUD sản phẩm, tìm kiếm tên không phân biệt hoa thường, phân trang |
| /graphiql | Chạy query/mutation GraphQL trực tiếp |
| /admin/categories và /admin/products | Giao diện REST/JSP phần 4 hiện có |

Thymeleaf render cấu trúc trang. JavaScript gọi POST /graphql bằng fetch,
đọc data/errors rồi cập nhật DOM mà không tải lại trang. Các trang GraphQL
không phụ thuộc CDN. Swagger vẫn dùng cho REST; thử GraphQL bằng GraphiQL.

Trường giá trong dữ liệu có tên **unitPrice**. Home sắp xếp tăng dần theo
unitPrice, sau đó productId để ổn định thứ tự khi trùng giá.
Home trả tất cả sản phẩm theo yêu cầu; phân trang áp dụng cho trang quản lý.

## Query mẫu

Mở http://localhost:8080/graphiql:

```graphql
query {
  homeProducts {
    productId
    productName
    unitPrice
    category { categoryId categoryName }
  }
  categories(keyword: "", page: 0, size: 5) {
    content { categoryId categoryName icon }
    totalElements
    totalPages
    number
  }
  products(keyword: "", page: 0, size: 5) {
    content { productId productName unitPrice quantity }
    totalElements
    totalPages
    number
  }
}
```

Lọc theo ID danh mục có thật trong dữ liệu:

```graphql
query {
  homeProducts(categoryId: "1") {
    productId productName unitPrice
  }
}
```

## Mutation mẫu

Thực hiện lần lượt. Thay ID mẫu bằng ID trả về từ mutation tạo.

```graphql
mutation {
  createCategory(input: {categoryName: "Điện thoại", icon: ""}) {
    categoryId categoryName
  }
}
```

```graphql
mutation {
  createProduct(input: {
    productName: "Điện thoại A"
    quantity: 10
    unitPrice: 5000000
    images: ""
    description: "Sản phẩm thử nghiệm"
    discount: 0
    status: 1
    categoryId: "1"
  }) { productId productName unitPrice }
}
```

- updateCategory(id: ID!, input: CategoryInput!): cập nhật tên và icon.
- updateProduct(id: ID!, input: ProductInput!): cập nhật đầy đủ các trường, giữ ngày tạo.
- deleteProduct(id: ID!): xóa sản phẩm.
- deleteCategory(id: ID!): xóa danh mục nếu không còn sản phẩm.
- product(id: ID!), category(id: ID!): đọc một bản ghi.

Input cập nhật là dữ liệu đầy đủ, không phải bản cập nhật từng trường.
Icon và images nhận chuỗi đường dẫn/URL; upload file vẫn có trong REST phần 4.
Trang home hiển thị ảnh, tên, danh mục, giá, mô tả và số lượng.
Danh sách quản lý Product cũng hiển thị ảnh thu nhỏ. Hỗ trợ URL HTTP(S),
đường dẫn `/images/products/...` và tên file upload. Ảnh thiếu/lỗi có ảnh thay thế.
`sql/sample_data.sql` có dữ liệu và đường dẫn ảnh cho 18 sản phẩm mẫu;
`sql/sample_images.sql` bổ sung ảnh cho dữ liệu mẫu đã có mà chưa có ảnh.

## Quy tắc và kiểm tra

- page bắt đầu từ 0; size từ 1 đến 100.
- Tên không được rỗng; giá và số lượng không âm; giảm giá 0–100; status 0 hoặc 1.
- Product phải thuộc một Category tồn tại.
- Không được xóa Category đang có Product.
- Giao diện dùng textContent để hiển thị dữ liệu nhập và thông báo lỗi.
- Lỗi nghiệp vụ trả trong errors của GraphQL, có thể kèm HTTP 200.

Kiểm thử trong CatalogGraphqlIntegrationTest bao gồm:
sắp xếp giá, lọc danh mục, tìm kiếm và phân trang, CRUD,
ràng buộc danh mục, dữ liệu không hợp lệ, endpoint HTTP GraphQL,
render Thymeleaf, đường dẫn con và route JSP cũ.

`src/test/resources/static/catalog-smoke.html` kiểm tra thao tác AJAX thực tế
trên trình duyệt. Chỉ chạy với classpath kiểm thử và profile `test` (H2);
trang kiểm thử này không được đóng gói vào ứng dụng chính.

Đã xác minh trên JDK 17: 5 kiểm thử tích hợp và 1 kiểm thử Chrome headless
đều đạt. Kiểm thử trình duyệt bao gồm tạo/sửa/xóa Category và Product,
tìm kiếm/phân trang Product, lọc và sắp xếp trên home, chặn xóa danh mục
còn sản phẩm. Chạy tùy chọn `-Browser` cần Chrome tại đường dẫn Windows mặc định.
Các kiểm thử dùng H2; khi chạy thật vẫn cần cấu hình MySQL như README.

Thử trên trình duyệt:
1. Tạo hai danh mục.
2. Tạo ba sản phẩm giá khác nhau, chia vào hai danh mục.
3. Mở home, kiểm tra thứ tự giá và đổi danh mục lọc.
4. Tìm theo tên; đổi số dòng/trang; chuyển trang.
5. Sửa sản phẩm, đổi danh mục; tải lại home để kiểm tra.
6. Xóa danh mục có sản phẩm: phải có thông báo lỗi.
7. Xóa sản phẩm rồi xóa danh mục; kiểm tra danh sách và trang cuối cập nhật.

## Tài liệu

Link bài giảng GraphQL trên LMS không truy cập được trong phiên thực hiện;
triển khai dựa trên yêu cầu phần 5 và mô hình Product/Category hiện có.
Tham khảo cách ánh xạ query/mutation và argument:
https://docs.spring.io/spring-graphql/reference/controllers.html
