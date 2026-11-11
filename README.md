# Ứng dụng Quản lý kho hàng (Android - SQLite)

Ứng dụng Android giúp quản lý sản phẩm trong kho hàng, theo dõi giá bán, số lượng tồn kho và cảnh báo sản phẩm sắp hết hàng.

## 1. Thông tin đồ án / bài thực hành
- **Ngôn ngữ:** Java & XML Layout
- **Hệ cơ sở dữ liệu:** SQLite thông qua `SQLiteOpenHelper` (tên DB: `warehouse.db`)
- **Kiến trúc:** 4 Activity kết nối qua `Intent`
- **Màu sắc chủ đạo:** Xanh ngọc đậm (Teal `#00695C`) chuẩn theo thiết kế

---

## 2. Tài khoản mẫu
Ứng dụng tự động khởi tạo tài khoản mẫu trong bảng `users`:
- **Tên đăng nhập:** `admin`
- **Mật khẩu:** `123456`

---

## 3. Cấu trúc các màn hình (4 Activity)

### 3.1. `LoginActivity` (Màn hình Đăng nhập)
- Form đăng nhập gồm tên đăng nhập và mật khẩu (ẩn mật khẩu `textPassword`).
- Kiểm tra tài khoản bằng cách truy vấn bảng `users` trong cơ sở dữ liệu SQLite (`checkLogin`).
- Không cho phép đăng nhập nếu để trống; hiển thị thông báo lỗi tương ứng khi sai mật khẩu.
- Sau khi đăng nhập thành công, chuyển sang `MainActivity` và gọi `finish()` để vô hiệu hóa nút Back quay lại màn hình đăng nhập.

### 3.2. `MainActivity` (Màn hình Danh sách & Thống kê)
- **Khối thống kê kho:**
  - Tổng số sản phẩm (`getProductCount()`)
  - Tổng giá trị kho (`getTotalValue() = SUM(price * quantity)`)
  - Số sản phẩm sắp hết hàng (`countLowStock()` với số lượng < 5)
- **Danh sách sản phẩm (`RecyclerView`):**
  - Hiển thị tên sản phẩm, giá bán (định dạng phân cách hàng nghìn `###.### đ`), loại sản phẩm và số lượng.
  - Tự động đánh dấu sản phẩm sắp hết hàng (số lượng < 5): số lượng hiển thị màu đỏ và có nhãn cảnh báo **"Sắp hết hàng"**.
  - Nhấn vào một sản phẩm để mở `ProductDetailActivity` (truyền `id` sản phẩm qua Intent).
- **Nút "+ Thêm sản phẩm":** Chuyển sang `AddProductActivity`.
- Luôn tự động làm mới danh sách và số liệu thống kê trong `onResume()`.

### 3.3. `AddProductActivity` (Màn hình Thêm sản phẩm)
- Form nhập gồm:
  - Tên sản phẩm (`EditText`, kiểm tra không được để trống).
  - Giá bán (`EditText`, kiểm tra kiểu số, phải > 0).
  - Số lượng (`EditText`, kiểm tra số nguyên, không được âm).
  - Loại sản phẩm (`Spinner`: Điện tử, Thực phẩm, Văn phòng phẩm, Gia dụng, Khác).
  - Đang kinh doanh (`Switch`, mặc định bật).
  - Mô tả (`EditText` nhiều dòng).
- Khi bấm **"Lưu sản phẩm"**, validate dữ liệu -> gọi `insertProduct()` vào SQLite -> hiển thị thông báo thành công -> `finish()` quay về `MainActivity`.

### 3.4. `ProductDetailActivity` (Màn hình Chi tiết sản phẩm)
- Nhận `id` qua Intent và truy vấn thông tin chi tiết từ cơ sở dữ liệu (`getProductById(id)`).
- Hiển thị tên, loại sản phẩm, giá bán nổi bật, số lượng tồn kho, giá trị tồn kho (giá bán × số lượng) và mô tả.
- **Nút "+ Nhập thêm 1":** Tăng số lượng lên 1 -> cập nhật vào SQLite (`updateQuantity()`) -> cập nhật ngay giao diện.
- **Nút "- Bán 1":** Giảm số lượng đi 1; nếu số lượng đã bằng 0 thì thông báo "Hết hàng" và không giảm; ngược lại cập nhật vào SQLite -> cập nhật giao diện.
- **Nút "Quay lại":** Đóng Activity (`finish()`).
- **Nút "Xóa sản phẩm":** Hiển thị hộp thoại xác nhận (AlertDialog) -> xóa khỏi SQLite (`deleteProduct()`) -> quay lại danh sách.

---

## 4. Cơ sở dữ liệu SQLite (`warehouse.db`)

### 4.1. Bảng `users`
| Tên cột | Kiểu dữ liệu | Ràng buộc | Ghi chú |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Mã tài khoản |
| `username` | TEXT | NOT NULL, UNIQUE | Tên đăng nhập |
| `password` | TEXT | NOT NULL | Mật khẩu |

### 4.2. Bảng `products`
| Tên cột | Kiểu dữ liệu | Ràng buộc | Ghi chú |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Mã sản phẩm |
| `name` | TEXT | NOT NULL | Tên sản phẩm |
| `price` | INTEGER | NOT NULL | Giá bán (VNĐ) |
| `quantity` | INTEGER | NOT NULL | Số lượng tồn kho |
| `category` | TEXT | NOT NULL | Loại sản phẩm |
| `active` | INTEGER | NOT NULL | 1: Đang kinh doanh, 0: Ngừng |
| `description` | TEXT | | Mô tả chi tiết |

### 4.3. Dữ liệu mẫu khởi tạo sẵn trong `onCreate()`
1. **Chuột không dây** - 150.000 đ - Điện tử - Số lượng: 20
2. **Bàn phím cơ** - 450.000 đ - Điện tử - Số lượng: 3 (Cảnh báo: Sắp hết hàng)
3. **Sổ tay A5** - 25.000 đ - Văn phòng phẩm - Số lượng: 40

---

## 5. Hướng dẫn chạy trên Android Studio
1. Mở Android Studio.
2. Chọn **File -> Open...** và duyệt tới thư mục `buoi_2`.
3. Chờ Android Studio đồng bộ (Gradle Sync) các dependencies.
4. Chọn máy ảo Android (Emulator) hoặc cắm thiết bị thật (bật USB Debugging).
5. Nhấn nút **Run 'app'** (phím tắt `Shift + F10`) để chạy ứng dụng.
