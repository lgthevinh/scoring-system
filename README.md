# Hệ thống Phần mềm Tính điểm Trực tiếp cho các Cuộc thi Robotics

Kho lưu trữ này chứa mã nguồn cho hệ thống tính điểm trực tiếp được thiết kế riêng cho các cuộc thi robotics. Hệ thống cho phép theo dõi và hiển thị điểm số, hiệu suất của các đội và số liệu thống kê trận đấu theo thời gian thực.

## Hướng dẫn Cài đặt

### Giới thiệu về Hệ thống Tính điểm Trực tiếp

Hệ thống Tính điểm Trực tiếp là một ứng dụng web cung cấp các cập nhật thời gian thực về điểm số và số liệu thống kê trong suốt cuộc thi. Hệ thống được thiết kế thân thiện với người dùng và có thể truy cập từ nhiều thiết bị như máy tính để bàn, máy tính bảng và điện thoại thông minh.

**Ai sử dụng Hệ thống Tính điểm Trực tiếp?**

* **Ban tổ chức sự kiện:** Để quản lý và hiển thị điểm số, đảm bảo sự kiện vận hành trơn tru và thu hút khán giả.
* **Người ghi điểm (Scorekeepers):** Để nhập điểm và cập nhật thông tin trận đấu trực tiếp.
* **Các đội thi và Khán giả:** Để xem điểm số và số liệu thống kê trận đấu được hiển thị trên các màn hình lớn tại sự kiện.

### Yêu cầu Phần cứng

Để vận hành hệ thống hiệu quả, các thiết bị phần cứng sau được khuyến nghị:

**Màn hình hiển thị:**

* Màn hình lớn hoặc máy chiếu để hiển thị điểm cho khán giả, ưu tiên kết nối HDMI và kích thước từ 27 inch trở lên.
* Khuyến nghị có ít nhất một màn hình cho mỗi sân thi đấu để hiển thị điểm và bộ đếm giờ tại chỗ.

**Máy tính bảng:**

* Tương thích với cả Android và iOS. Cấu hình khuyến nghị:
* Kích thước màn hình: 9 inch trở lên.
* Hệ điều hành: Android 8.0+ hoặc iOS 12.0+.
* Kết nối: Có Wi-Fi.
* Ứng dụng: Sử dụng trình duyệt **Google Chrome** để đạt độ tương thích tốt nhất.



**Mạng nội bộ:**

* Sử dụng một mạng cục bộ (LAN) riêng biệt, ổn định và bảo mật.
* Nên thiết lập các chính sách hạn chế truy cập, chỉ cho phép các thiết bị được ủy quyền để đảm bảo an toàn hệ thống.
* Sử dụng kết nối có dây (Ethernet) cho máy chủ bất cứ khi nào có thể. *Lưu ý: Máy tính bảng của Trọng tài là thiết bị không dây.*

### Yêu cầu Hệ thống và Kiến trúc Vận hành

Phần mềm hoạt động trên Windows và macOS với các yêu cầu sau:

**Yêu cầu máy chủ:**

* Hệ điều hành: Windows 10 hoặc macOS 10.15 (Catalina) trở lên.
* Trình duyệt: Google Chrome (phiên bản mới nhất).
* RAM: Tối thiểu 4 GB (khuyến nghị 8 GB).
* Dung lượng đĩa trống: 500 MB.
* Cổng Ethernet (khuyến nghị).

**Yêu cầu máy tính bảng:**

* Hệ điều hành: Android 8.0+ hoặc iOS 12.0+.
* Trình duyệt: Google Chrome (phiên bản mới nhất).

**Kiến trúc Vận hành phổ biến:**

1. **Laptop Server:** Lưu trữ ứng dụng và quản lý dữ liệu điểm số. Kết nối mạng qua cáp Ethernet.
2. **Màn hình hiển thị tại sân:** Kết nối với laptop server để hiển thị điểm số thời gian thực.
3. **Máy tính bảng của Trọng tài:** Kết nối với server qua Wi-Fi để nhập điểm.
4. **Router/Switch mạng:** Tạo mạng cục bộ riêng cho hệ thống.

### Tải xuống Phần mềm

Tải phiên bản mới nhất tại:
[Releases - Live Scoring System](https://github.com/lgthevinh/scoring-system/releases)

> **Lưu ý:** Hãy tải đúng phiên bản dành cho sự kiện của bạn (ví dụ: FANROC, FARC,...) và đọc kỹ ghi chú phát hành (release notes).

**Mẹo quan trọng khi khởi chạy:**

* Sử dụng Google Chrome làm trình duyệt mặc định.
* **Không hỗ trợ trình duyệt Firefox!**
* Tắt tường lửa/phần mềm diệt virus trên máy tính chạy server.
* Tắt các trình chặn quảng cáo trên tất cả các thiết bị.
* Xóa bộ nhớ đệm (cache) trình duyệt trước khi dùng.

### Hướng dẫn Khởi chạy

1. Giải nén tệp ZIP đã tải về.
2. Truy cập vào thư mục chứa tệp chạy:
```text
{thư_mục_giải_nén}/vrc-scoring-system/run.bat (Windows)
{thư_mục_giải_nén}/vrc-scoring-system/run.sh (macOS)
```


3. Chạy tệp tương ứng với hệ điều hành:
* **Windows:** Nhấp đúp vào `run.bat`.
* **macOS:** Mở terminal và chạy lệnh `sh run.sh`.


4. Truy cập hệ thống bằng địa chỉ IP của máy chủ trên trình duyệt (ví dụ: `http://192.168.1.10`).

### Đăng nhập

Sử dụng thông tin mặc định khi truy cập từ máy chủ:

* **Username:** `local`
* *(Không cần mật khẩu đối với truy cập nội bộ)*

### Hướng dẫn Sử dụng

#### Chuẩn bị sự kiện

Vào mục **Event Dashboard** để thiết lập:

* Tạo thông tin sự kiện và nhấn **Set Active**.
* Trong tab **Event Tools**: Tạo tài khoản trọng tài, nhập danh sách đội thi và tạo lịch thi đấu.

#### Trong khi diễn ra sự kiện

Tại **Main screen**, bạn có thể theo dõi lịch đấu, bảng xếp hạng và kết quả các trận.

#### Sau sự kiện

* Kết quả và xếp hạng có thể được xuất để lưu trữ.
* Tệp cơ sở dữ liệu có định dạng `{MÃ_SỰ_KIỆN}.db`.
* Điểm chi tiết từng trận nằm trong thư mục `files` dưới dạng tệp JSON.

---

### Liên hệ Hỗ trợ

* **Email bảo trì chính thức:** `everwellmax@gmail.com`
* **Email hỗ trợ sự kiện FANROC:** `truongcongminhquan09@gmail.com`
