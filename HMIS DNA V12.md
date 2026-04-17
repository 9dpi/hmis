🧬 HMIS DNA V 1.2 – Enterprise Scale (400+ Sites)
(Cập nhật compliance, roadmap & ngân sách chi tiết – Tháng 4/2026)

1. Chiến lược đa đối tượng & đa cấp (Multi-tenant Architecture)

┌─────────────────────────────────────────────────────────────┐
│                    CENTRAL COMMAND CENTER                    │
│          (Bộ Y Tế / Sở Y Tế / Tập đoàn Bệnh viện)            │
├──────────────┬──────────────┬──────────────┬────────────────┤
│  Bệnh viện A │  Bệnh viện B │ Trạm Y tế C  │ Phòng khám D   │
│  (500 giường)│  (300 giường)│  (10 giường) │  (5 giường)    │
└──────────────┴──────────────┴──────────────┴────────────────┘

Mô hình tổ chức
- Cấp Central: Giám sát tổng hợp, điều phối, cảnh báo dịch tễ, đẩy dữ liệu quốc gia.
- Cấp Hospital: Quản lý nội bộ toàn diện, HSBAĐT đầy đủ.
- Cấp Clinic: Phiên bản nhẹ, đồng bộ định kỳ.
- Cấp Station: Mobile-first, offline-first cho trạm y tế vùng cao.

2. Kiến trúc Module hóa cho HMIS – Lựa chọn ngôn ngữ & Chuẩn hóa

Yêu cầu kiến trúc
Hệ thống cần module hóa hoàn toàn (IoT, AI, EHR, PACS là các module độc lập “cắm vào”), API chuẩn hóa, và vận hành như một “Hệ điều hành y tế”.

Khuyến nghị: Hybrid Architecture (Kiến trúc “2 Tốc độ”)
- Core Platform (Java Spring Boot): Quản lý Users, RBAC, Billing, EMPI, Multi-tenant foundation.
- Feature Modules (Python FastAPI): Hỗ trợ chẩn đoán AI, xử lý dữ liệu IoT, Analytics, BI Report.
- Giao tiếp: REST/gRPC + Kafka + PostgreSQL shared.

Chuẩn Module Interface (Bắt buộc)
1. Lifecycle Hooks: on_install(), on_enable(), on_disable(), on_uninstall().
2. API Contract: OpenAPI/Swagger, Versioning /api/v1/*.
3. Database Isolation: Schema prefix + Row Level Security (RLS) per tenant.
4. Event System: Subscribe/Publish qua Kafka.

3. Kiến trúc Công nghệ mới (Scalable Stack)

3.1 Backend & Data Layer
- PostgreSQL (RDS/Aurora): Dữ liệu chính.
- TimescaleDB: Dữ liệu IoT sensors.
- Redis: Cache real-time & session.
- Hasura / PostgREST: Tự động hóa GraphQL/REST API.
- LiteSync / PgEdge: Đồng bộ 2 chiều online-offline.

3.2 Frontend Architecture
Hệ thống tự nhận diện cấp độ site (Site Type) dựa trên domain để render tính năng phù hợp (Full feature cho Bệnh viện, Lite cho Phòng khám, Mobile cho Trạm y tế).

3.3 Data Pipeline
IoT Hub → Kafka → TimescaleDB → Grafana Monitoring + National Gateway.

4. Module Tính năng theo cấp độ
(Chi tiết bảng phân bổ tính năng từ Dashboard, Quản lý bệnh nhân, Kho dược đến Báo cáo tổng hợp cho 400 cơ sở).

5. Tuân thủ Quy định (Compliance Framework)

Khung pháp lý hiện hành (tháng 4/2026):
- Nghị định 13/2023/NĐ-CP: Bảo vệ dữ liệu cá nhân (dữ liệu sức khỏe thuộc dữ liệu nhạy cảm).
- Thông tư 13/2025/TT-BYT: Hướng dẫn triển khai Hồ sơ bệnh án điện tử (hiệu lực 21/07/2025).
- Thông tư 38/2024/TT-BYT: Hệ thống thông tin quản lý hoạt động khám chữa bệnh quốc gia.
- Thông tư 54/2017/TT-BYT: Tiêu chí ứng dụng CNTT trong hoạt động khám chữa bệnh (phần còn hiệu lực).

Compliance Matrix 2026 (Bắt buộc triển khai):
- HSBAĐT đầy đủ + ký số điện tử + sinh trắc học.
- Kết nối VNeID và mã định danh cá nhân.
- Đẩy dữ liệu định kỳ lên Hệ thống thông tin quản lý KCB quốc gia.
- Consent explicit + Privacy Impact Assessment (PIA).
- Row Level Security + Tenant isolation + AES-256-GCM.
- Audit Trail immutable (WORM) ít nhất 5 năm.
- Offline-first với integrity check và conflict resolution.

Bảo mật: Mã hóa AES-256-GCM, Audit Trail immutable, SIEM tích hợp Kafka.

6. Triển khai 400 cơ sở (Deployment Strategy)

- Phase 1 (6 tháng): Pilot 10 site + tích hợp đầy đủ HSBAĐT, ký số điện tử, VNeID và national gateway.
- Phase 2 (6 tháng): Scale lên 150 site.
- Phase 3 (9 tháng): Hoàn tất mass rollout 400 site (hoàn thành trước 31/12/2026 cho clinic và station).
- Hạ tầng Cloud: Ưu tiên Region Việt Nam (Viettel Cloud / VNPT / VNG) để đảm bảo data residency và pháp lý.

7. Chi phí ước tính hàng tháng (VNĐ)
- Tổng hạ tầng Cloud cho 400 site: ~246.000.000 VNĐ/tháng.
- Tiết kiệm 84% so với việc dùng Cloud quốc tế (AWS/Azure/GCP).

8. Ngân sách dự án (Budget Breakdown)

Tổng ngân sách ước tính cho giai đoạn triển khai ban đầu (18 tháng – Phase 1 + Phase 2):

| Hạng mục                              | Phase 1 (6 tháng)     | Phase 2 (6 tháng)     | Phase 3 (6 tháng)     | Tổng (18 tháng)       |
|---------------------------------------|-----------------------|-----------------------|-----------------------|-----------------------|
| Nhân sự (Team + Tư vấn pháp lý/DPO)  | 1.800.000.000        | 1.500.000.000        | 1.200.000.000        | 4.500.000.000        |
| Phát triển Core Platform & Compliance (HSBAĐT, ký số, VNeID, National Gateway) | 2.200.000.000        | 800.000.000          | 400.000.000          | 3.400.000.000        |
| Phát triển Feature Modules (AI, IoT, Analytics) | 900.000.000          | 1.100.000.000        | 700.000.000          | 2.700.000.000        |
| Hạ tầng Cloud & License (Viettel/VNPT) | 600.000.000          | 900.000.000          | 1.200.000.000        | 2.700.000.000        |
| Thiết bị pilot & Training (10 sites) | 400.000.000          | 300.000.000          | 200.000.000          | 900.000.000          |
| Tư vấn pháp lý, Audit & Compliance    | 300.000.000          | 200.000.000          | 150.000.000          | 650.000.000          |
| Dự phòng (10%)                        | 620.000.000          | 480.000.000          | 385.000.000          | 1.485.000.000        |
| **Tổng theo Phase**                   | **6.820.000.000**    | **5.280.000.000**    | **4.235.000.000**    | **16.335.000.000**   |

**Ghi chú ngân sách:**
- Tổng ngân sách triển khai ban đầu (18 tháng): **~16,335 tỷ VNĐ**.
- Phase 1 (6 tháng) tập trung mạnh vào compliance và core platform (~6,82 tỷ) để đảm bảo đáp ứng deadline HSBAĐT 30/09/2025.
- Chi phí nhân sự bao gồm 9 nhân sự nòng cốt + tư vấn pháp lý y tế/DPO.
- Sau 18 tháng, chuyển sang chi phí vận hành hàng tháng (~246 triệu VNĐ/tháng cho hạ tầng + bảo trì).

9. Đội ngũ dự án (TEAM)
- Central Team: 9 nhân sự nòng cốt (PM, Kiến trúc sư, Fullstack dev, DevOps, QA, BA Y tế).
- Bổ sung: 1 tư vấn pháp lý y tế / Data Protection Officer (DPO).

10. Triển khai thí điểm (Commune Health Station)
- Ứng dụng PWA hoạt động Offline-first.
- Các module: Tiếp nhận, Khám bệnh, Kho thuốc trạm, Tiêm chủng mở rộng + HSBAĐT lite + đồng bộ national.
- Lộ trình: Setup DB → Tính năng lõi → Đồng bộ & Báo cáo → Triển khai thực địa.

11. Lộ trình Phát triển & Thành tựu (Roadmap)

Đã đạt được:
- Lõi UI Dashboard.
- Sync Google Sheets.
- Module 3D Lab.

Tiếp theo (6 tháng tới):
- Postgres Migration + Hasura Multi-tenant + Row Level Security.
- Tích hợp ký số điện tử, VNeID và API theo Thông tư 38/2024.
- IoT Integration.
- Compliance Dashboard (tự động kiểm tra mức độ tuân thủ).
- Hoàn thành HSBAĐT theo Thông tư 13/2025 trước 30/09/2025.