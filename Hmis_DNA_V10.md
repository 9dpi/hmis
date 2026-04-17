🧬 Project DNA: SCP HMIS (Hospital Management Platform)
1. Định danh & Tầm nhìn (Identity & Vision)
Tên dự án: SCP HMIS (Hospital Management Information System).
Sứ mệnh: Xây dựng một "Trung tâm chỉ huy" (Command Center) hiện đại cho bệnh viện, nơi mọi thông số vận hành từ lâm sàng đến hạ tầng kỹ thuật đều được giám sát thời gian thực.
Giá trị cốt lõi: Minh bạch dữ liệu, Phản ứng nhanh (Real-time), và Trải nghiệm người dùng cao cấp.
2. Kiến trúc Công nghệ (Tech Stack)
Frontend:
AlpineJS: Xử lý logic reactivity và state management nhẹ nhàng.
TailwindCSS: Hệ thống thiết kế (Design System) nhất quán, hiện đại.
Chart.js: Trực quan hóa dữ liệu đo lường (Nhiệt độ, độ ẩm, viện phí...).
Panzoom: Hỗ trợ tương tác với các sơ đồ kỹ thuật/3D phức tạp.
Backend & Data Strategy:
Google Ecosystem: Sử dụng Google Sheets làm cơ sở dữ liệu và Google Apps Script làm API trung gian. Đây là một hướng tiếp cận "Serverless" thông minh, giúp triển khai nhanh và dễ quản lý cho nhân viên y tế.
3. Các Module Tính năng Chính (Core Modules)
Dashboard Điều hành: Giám sát các chỉ số sinh tồn của bệnh viện (AQI, Năng lượng, Oxy, Rác thải y tế).
3D LAB (Facility Management): Quản lý hạ tầng theo lớp (Kiến trúc, Điện lưới, Nước sạch, PCCC) với khả năng mô phỏng (Simulation Mode).
Clinical Ops: Quản lý danh sách bệnh nhân, sơ đồ giường bệnh và trạng thái xét nghiệm.
Hậu cần & Vận hành: Quy trình vệ sinh (Housekeeping) được đồng bộ trực tiếp từ ảnh chụp thực tế qua Google Drive; Quản lý kho dược và viện phí.
Quản lý chất lượng: Tích hợp sẵn bộ 83 tiêu chí chất lượng của Bộ Y Tế để tự đánh giá và chấm điểm mức độ hoàn thiện.
4. Ngôn ngữ Thiết kế (Design DNA)
Phong cách: "Sci-fi Control Room" kết hợp với "Modern Clinical".
Hi ứng thị giác: Sử dụng Glassmorphism (hiệu ứng kính mờ), màu sắc tương phản cao trên nền Dark Mode (Teal, Cyan, Orange), và các micro-animations giúp giao diện cảm giác "sống".
Tính linh hoạt: Hỗ trợ đa thiết bị (Desktop/Mobile) và đa chế độ sáng/tối (Dark/Light Mode).
5. Điểm khác biệt (USP - Unique Selling Points)
Tính di động: Chỉ sử dụng HTML/JS/CSS, không cần build phức tạp, có thể chạy ngay trên trình duyệt hoặc tích hợp vào các WebView.
Khả năng mở rộng: Dễ dàng thêm các cảm biến IoT để đẩy dữ liệu vào Google Sheets và hiển thị ngay lập tức lên Dashboard.
Quy trình khép kín: Từ giám sát (Monitor) -> Cảnh báo (Alert) -> Điều phối (Action) đều được tích hợp trong một giao diện duy nhất.

6. Kiến trúc Logic & Vận hành (Implementation Logic)
*   **Dữ liệu Tập trung (Global State)**: Sử dụng `x-data` của AlpineJS tại tag `<html>` để quản lý trạng thái toàn cục (Dark Mode, Thông tin người dùng, Trạng thái Sidebar).
*   **Luồng Dữ liệu (Data Pipeline)**: 
    *   **Polling Cơ bản**: `setInterval` thực hiện fetch dữ liệu từ Google Apps Script mỗi 30-60 giây.
    *   **Data Service**: Module `assets/js/data-service.js` đóng vai trò là "Data Mapper", chuyển đổi JSON thô từ Google Sheets sang Object có cấu trúc để UI hiển thị.
*   **Logic Mô phỏng (Simulation Logic)**:
    *   Sử dụng hệ thống "Layered PNGs" chồng lên nhau trong `Panzoom container`. 
    *   Logic chuyển đổi lớp `showLayer` thay đổi `src` hoặc `visibility` của ảnh mà không cần tải lại trang.
*   **Quy trình Cảnh báo (Alert Workflow)**:
    *   **Detector**: Script kiểm tra các ngưỡng (threshold) của dữ liệu (Ví dụ: Áp suất PCCC < 10 bar).
    *   **Trigger**: Kích hoạt `showModal = true` và hiệu ứng `animate-ping` trên tọa độ X, Y của sơ đồ.
*   **Tối ưu hóa Hiệu suất**:
    *   **Anti-Flicker**: Script kiểm tra `localStorage` ngay tại `<head>` để tránh hiện tượng nháy trắng khi load Dark Mode.
    *   **Lazy Loading**: Các module Chart.js chỉ khởi tạo khi section tương ứng được kích hoạt.

7. Sơ đồ Cấu trúc File (File Map)
*   `/index.html`: Cửa sổ trung tâm (Dashboard).
*   `/assets/js/data-store.js`: Lưu trữ các hằng số và dữ liệu mẫu (Mock data).
*   `/assets/js/sidebar-sync.js`: Đảm bảo trạng thái menu đồng bộ giữa các trang con.
*   `/desktop/*.html`: Các module nghiệp vụ chuyên sâu.
*   `/shared/`: Chứa các thành phần giao diện dùng chung (Header, Sidebar).