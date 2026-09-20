-- Du lieu gia lap de kiem thu muc 5; gia tinh bang VND.
-- Chay sau khi ung dung da tao bang. Khong xoa hoac ghi de du lieu hien co.
-- Co the chay lai: bo qua danh muc/san pham da co cung ten.
SET NAMES utf8mb4;
USE springboot_api;
START TRANSACTION;

INSERT INTO categories (category_name, icon)
SELECT 'Điện thoại', '' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE category_name = 'Điện thoại');
INSERT INTO categories (category_name, icon)
SELECT 'Laptop', '' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE category_name = 'Laptop');
INSERT INTO categories (category_name, icon)
SELECT 'Máy tính bảng', '' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE category_name = 'Máy tính bảng');
INSERT INTO categories (category_name, icon)
SELECT 'Tai nghe', '' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE category_name = 'Tai nghe');
INSERT INTO categories (category_name, icon)
SELECT 'Phụ kiện', '' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE category_name = 'Phụ kiện');
INSERT INTO categories (category_name, icon)
SELECT 'Màn hình', '' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE category_name = 'Màn hình');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Điện thoại Nova Mini', 10, 2490000, '', 'Dữ liệu mẫu: Điện thoại Nova Mini. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 0, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Điện thoại')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Điện thoại Nova Mini');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Điện thoại Nova Plus', 17, 5990000, '', 'Dữ liệu mẫu: Điện thoại Nova Plus. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 5, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Điện thoại')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Điện thoại Nova Plus');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Điện thoại Nova Pro', 24, 9990000, '', 'Dữ liệu mẫu: Điện thoại Nova Pro. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 10, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Điện thoại')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Điện thoại Nova Pro');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Laptop Horizon Office', 10, 10990000, '', 'Dữ liệu mẫu: Laptop Horizon Office. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 0, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Laptop')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Laptop Horizon Office');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Laptop Horizon Study', 17, 13990000, '', 'Dữ liệu mẫu: Laptop Horizon Study. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 5, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Laptop')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Laptop Horizon Study');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Laptop Horizon Gaming', 24, 22990000, '', 'Dữ liệu mẫu: Laptop Horizon Gaming. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 10, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Laptop')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Laptop Horizon Gaming');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Máy tính bảng Luna Mini', 10, 3490000, '', 'Dữ liệu mẫu: Máy tính bảng Luna Mini. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 0, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Máy tính bảng')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Máy tính bảng Luna Mini');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Máy tính bảng Luna Air', 17, 6490000, '', 'Dữ liệu mẫu: Máy tính bảng Luna Air. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 5, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Máy tính bảng')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Máy tính bảng Luna Air');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Máy tính bảng Luna Pro', 24, 11490000, '', 'Dữ liệu mẫu: Máy tính bảng Luna Pro. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 10, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Máy tính bảng')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Máy tính bảng Luna Pro');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Tai nghe Echo Basic', 10, 199000, '', 'Dữ liệu mẫu: Tai nghe Echo Basic. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 0, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Tai nghe')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Tai nghe Echo Basic');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Tai nghe Echo Wireless', 17, 790000, '', 'Dữ liệu mẫu: Tai nghe Echo Wireless. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 5, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Tai nghe')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Tai nghe Echo Wireless');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Tai nghe Echo Studio', 24, 1490000, '', 'Dữ liệu mẫu: Tai nghe Echo Studio. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 10, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Tai nghe')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Tai nghe Echo Studio');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Chuột không dây Swift', 10, 159000, '', 'Dữ liệu mẫu: Chuột không dây Swift. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 0, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Phụ kiện')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Chuột không dây Swift');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Bàn phím cơ Swift', 17, 690000, '', 'Dữ liệu mẫu: Bàn phím cơ Swift. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 5, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Phụ kiện')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Bàn phím cơ Swift');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Sạc nhanh Swift 65W', 24, 450000, '', 'Dữ liệu mẫu: Sạc nhanh Swift 65W. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 10, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Phụ kiện')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Sạc nhanh Swift 65W');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Màn hình Vision 24 inch', 10, 2490000, '', 'Dữ liệu mẫu: Màn hình Vision 24 inch. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 0, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Màn hình')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Màn hình Vision 24 inch');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Màn hình Vision 27 inch', 17, 3990000, '', 'Dữ liệu mẫu: Màn hình Vision 27 inch. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 5, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Màn hình')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Màn hình Vision 27 inch');

INSERT INTO products (product_name, quantity, unit_price, images, description, discount, create_date, status, category_id)
SELECT 'Màn hình Vision 32 inch', 24, 6990000, '', 'Dữ liệu mẫu: Màn hình Vision 32 inch. Dùng để thử CRUD, tìm kiếm và lọc danh mục.', 10, CURRENT_TIMESTAMP, 1,
       (SELECT MIN(category_id) FROM categories WHERE category_name = 'Màn hình')
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_name = 'Màn hình Vision 32 inch');

COMMIT;

-- Anh minh hoa cho du lieu mau; khong ghi de anh nguoi dung da chon.
SET NAMES utf8mb4;
USE springboot_api;
START TRANSACTION;
UPDATE products SET images = '/images/products/sample-1.webp' WHERE product_name = 'Điện thoại Nova Mini' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-2.webp' WHERE product_name = 'Điện thoại Nova Plus' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-3.webp' WHERE product_name = 'Điện thoại Nova Pro' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-4.webp' WHERE product_name = 'Laptop Horizon Office' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-5.webp' WHERE product_name = 'Laptop Horizon Study' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-6.webp' WHERE product_name = 'Laptop Horizon Gaming' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-7.webp' WHERE product_name = 'Máy tính bảng Luna Mini' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-8.webp' WHERE product_name = 'Máy tính bảng Luna Air' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-9.webp' WHERE product_name = 'Máy tính bảng Luna Pro' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-10.webp' WHERE product_name = 'Tai nghe Echo Basic' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-11.webp' WHERE product_name = 'Tai nghe Echo Wireless' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-12.webp' WHERE product_name = 'Tai nghe Echo Studio' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-13.jpg' WHERE product_name = 'Chuột không dây Swift' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-14.jpg' WHERE product_name = 'Bàn phím cơ Swift' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-15.webp' WHERE product_name = 'Sạc nhanh Swift 65W' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-16.jpg' WHERE product_name = 'Màn hình Vision 24 inch' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-17.jpg' WHERE product_name = 'Màn hình Vision 27 inch' AND (images IS NULL OR images = '');
UPDATE products SET images = '/images/products/sample-18.jpg' WHERE product_name = 'Màn hình Vision 32 inch' AND (images IS NULL OR images = '');
COMMIT;

