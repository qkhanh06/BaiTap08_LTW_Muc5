# Spring Boot 3 - REST API + Swagger 3 + AJAX CRUD

Phần 5 (GraphQL + AJAX + Thymeleaf): xem [GRAPHQL.md](GRAPHQL.md).
Trang chủ mới: http://localhost:8080/home.

Project mẫu hoàn chỉnh cho bài Category + Product:

- Java 17
- Spring Boot 3.1.5
- Spring Data JPA + MySQL
- REST API CRUD
- Upload icon/image
- Swagger 3 (springdoc-openapi)
- JSP + Bootstrap + jQuery AJAX
- Search + Pagination cho Category và Product

## 1. Chuẩn bị MySQL

Chạy file `sql/create_database.sql` trong MySQL Workbench:

```sql
CREATE DATABASE IF NOT EXISTS springboot_api
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

## 2. Cấu hình MySQL

Mở:

`src/main/resources/application.properties`

Ứng dụng đang dùng tài khoản MySQL `root`. Để chạy lần đầu, sao chép
`src/main/resources/application-local.properties.example` thành
`src/main/resources/application-local.properties`, rồi điền mật khẩu MySQL của bạn:

```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

File `application-local.properties` được Git bỏ qua để không đưa mật khẩu lên GitHub.
Nếu dùng tài khoản MySQL khác `root`, đổi `spring.datasource.username` trong `application.properties`.

## 3. Chạy project

Trong VS Code Terminal:

Windows:

```powershell
mvn spring-boot:run
```

Nếu dùng Maven Wrapper thì:

```powershell
.\mvnw spring-boot:run
```

## 4. Link kiểm tra

- Category AJAX: http://localhost:8080/admin/categories
- Product AJAX: http://localhost:8080/admin/products
- Swagger: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 5. API Category

- `GET /api/category`
- `GET /api/category/search?keyword=&page=0&size=5`
- `POST /api/category/getCategory?id=1`
- `POST /api/category/addCategory`
- `PUT /api/category/updateCategory`
- `DELETE /api/category/deleteCategory?categoryId=1`

## 6. API Product

- `GET /api/product`
- `GET /api/product/search?keyword=&page=0&size=5`
- `GET /api/product/getProduct?id=1`
- `POST /api/product/addProduct`
- `PUT /api/product/updateProduct`
- `DELETE /api/product/deleteProduct?productId=1`

## 7. Thứ tự test

1. Tạo ít nhất 1 Category trước.
2. Test CRUD Category.
3. Tạo Product và chọn Category.
4. Test CRUD Product.
5. Test tìm kiếm và phân trang.
6. Mở Swagger để kiểm tra API.

## Lưu ý

- Ảnh được lưu trong thư mục `uploads/`.
- Database tables sẽ tự sinh nhờ `spring.jpa.hibernate.ddl-auto=update`.
- Nếu cổng 8080 bị chiếm, chạy `java -jar target/springboot-ajax-crud-0.0.1-SNAPSHOT.war --server.port=18080` hoặc đổi `server.port` trong `application.properties`.
