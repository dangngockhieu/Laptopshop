# 🛍️ TechZone - Laptop E-commerce Backend

TechZone is a backend-only e-commerce system for selling laptops. It is built from scratch with Spring Boot and exposes REST APIs for authentication, users, products, carts, orders, and payments.

Frontend repository: [Frontend-laptopshop](https://github.com/dangngockhieu/Frontend-laptopshop)

---

## 📘 Overview

**TechZone** provides:

- User registration, login, and email verification
- Profile management
- Product, cart, and order management
- VNPay payment integration
- Admin management for users and products
- Email verification and password reset

---

## 🧱 Tech Stack

| Layer    | Technology                   |
| -------- | ---------------------------- |
| Language | Java 17                      |
| Backend  | Spring Boot 4, Spring MVC    |
| Build    | Maven                        |
| Data     | Spring Data JPA, PostgreSQL  |
| Security | Spring Security, JWT, Argon2 |
| Mail     | Spring Mail, Thymeleaf       |
| API docs | OpenAPI / Swagger UI         |

---

## 🏗️ Architecture

The backend follows a layered architecture:

- Controller: Handle HTTP requests
- Service: Business logic
- Repository: Data access layer

This structure improves maintainability and scalability.

---

## 📁 Project Structure

```text
Laptopshop/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── docs/
│   └── RelationalSchema.png
└── src/
	  ├── main/
	  │   ├── java/vn/techzone/khieu/
	  │   │   ├── KhieuApplication.java       # Điểm khởi động Spring Boot
	  │   │   ├── EnvLoader.java              # Đọc biến môi trường
	  │   │   ├── config/
	  │   │   │   ├── CorsConfig.java                    # Cho phép frontend gọi API
	  │   │   │   ├── CustomAccessDeniedHandler.java     # Xử lý lỗi không đủ quyền
  	  │   │   │   ├── CustomAuthenticationEntryPoint.java # Xử lý request chưa đăng nhập
  	  │   │   │   ├── JWTConfiguration.java               # Cấu hình xác thực JWT
  	  │   │   │   ├── SecurityConfiguration.java          # Luật bảo mật và phân quyền
  	  │   │   │   ├── StaticResourcesWebConfiguration.java # Cấu hình tài nguyên tĩnh
  	  │   │   │   ├── Swagger.java                        # Cấu hình Swagger/OpenAPI
  	  │   │   │   ├── VNPayConfig.java                    # Cấu hình thanh toán VNPay
  	  │   │   │   └── WebConfig.java                      # Cấu hình web và interceptor
  	  │   │   ├── controller/
  	  │   │   │   ├── AuthController.java      # Đăng ký, đăng nhập, xác minh email
  	  │   │   │   ├── CartController.java      # API quản lý giỏ hàng
  	  │   │   │   ├── ChatBotController.java   # API chatbot và tư vấn sản phẩm
  	  │   │   │   ├── MainController.java      # API dùng chung
  	  │   │   │   ├── OrderController.java     # Tạo và cập nhật đơn hàng
  	  │   │   │   ├── PaymentController.java   # Tạo và nhận callback thanh toán
  	  │   │   │   ├── ProductController.java   # Sản phẩm, lọc, ảnh và Excel
  	  │   │   │   └── UserController.java      # Hồ sơ, user và quyền admin
  	  │   │   ├── dto/
  	  │   │   │   ├── request/
  	  │   │   │   │   ├── ProductIdDTO.java
  	  │   │   │   │   ├── cart/UpdateQuantityCartDTO.java
  	  │   │   │   │   ├── order/CreateOrderDTO.java
  	  │   │   │   │   ├── order/UpdateToShippingDTO.java
  	  │   │   │   │   ├── order/UpdateToStatusDTO.java
  	  │   │   │   │   ├── product/ChatRequestDTO.java
  	  │   │   │   │   ├── product/CreateProductDTO.java
  	  │   │   │   │   ├── product/ProductFeatureDTO.java
  	  │   │   │   │   ├── product/UpdateProductDTO.java
  	  │   │   │   │   ├── review/CreateReviewDTO.java
  	  │   │   │   │   └── user/
      │   │   │   │       ├── CreateUserDTO.java
  	  │   │   │   │       ├── EmailDTO.java
  	  │   │   │   │       ├── LoginDTO.java
  	  │   │   │   │       ├── RegisterUserDTO.java
  	  │   │   │   │       ├── ResetPasswordDTO.java
  	  │   │   │   │       ├── UpdatePasswordDTO.java
      │   │   │   │       └── UpdateRoleDTO.java
  	  │   │   │   └── response/
  	  │   │   │       ├── PageResponseDTO.java
  	  │   │   │       ├── ResPaymentDTO.java
  	  │   │   │       ├── cart/ResCartDTO.java
  	  │   │   │       ├── order/
  	  │   │   │       │   ├── ResMonthlyRevenueDTO.java
  	  │   │   │       │   ├── ResOrderCountDTO.java
  	  │   │   │       │   ├── ResOrderDTO.java
  	  │   │   │       │   ├── ResOrderItemDTO.java
  	  │   │   │       │   ├── ResOrderUser/
  	  │   │   │       │   │   ├── ResOrderItemUser.java
  	  │   │   │       │   │   ├── ResOrderUserDTO.java
  	  │   │   │       │   │   └── ResUserOrder.java
  	  │   │   │       │   └── ResRevenue/
  	  │   │   │       │       ├── ResRevenue.java
  	  │   │   │       │       └── ResRevenueThisMonthDTO.java
  	  │   │   │       ├── product/
  	  │   │   │       │   ├── AllProductForChatBot/
  	  │   │   │       │   │   ├── ChatResponseDTO.java
  	  │   │   │       │   │   ├── ResProductforAiChatBotDTO.java
  	  │   │   │       │   │   └── ResProductforAiChatBotProjection.java
  	  │   │   │       │   ├── FilterProductResponseDTO.java
  	  │   │   │       │   ├── ProductDetailDTO/
  	  │   │   │       │   │   ├── ResProductDetail.java
  	  │   │   │       │   │   ├── ResProductDetailDTO.java
  	  │   │   │       │   │   ├── ResReview.java
  	  │   │   │       │   │   └── ResReviewSummary.java
  	  │   │   │       │   ├── ResBestSeller.java
  	  │   │   │       │   ├── ResCardProductDTO.java
   	  │   │   │       │   └── ResProductDTO.java
  	  │   │   │       └── user/
  	  │   │   │           ├── ResLoginDTO.java
  	  │   │   │           ├── ResStringDTO.java
  	  │   │   │           └── ResUserDTO.java
  	  │   │   ├── entity/                         # Các model ánh xạ với database
      │   │   │   ├── Cart.java                   # Sản phẩm trong giỏ hàng
  	  │   │   │   ├── CartId.java                 # Khóa ghép của giỏ hàng
  	  │   │   │   ├── ChatMessage.java            # Tin nhắn chatbot
  	  │   │   │   ├── Feature.java                # Thông số sản phẩm
  	  │   │   │   ├── Order.java                  # Đơn mua và trạng thái đơn
  	  │   │   │   ├── OrderItem.java              # Một dòng sản phẩm trong đơn
  	  │   │   │   ├── Payment.java                # Thông tin thanh toán
      │   │   │   ├── Product.java                # Sản phẩm laptop
  	  │   │   │   ├── ProductFeature.java         # Liên kết sản phẩm và tính năng
  	  │   │   │   ├── ProductFeatureId.java       # Khóa ghép liên kết tính năng
  	  │   │   │   ├── ProductImage.java           # Ảnh của sản phẩm
  	  │   │   │   ├── Review.java                 # Đánh giá sản phẩm
  	  │   │   │   └── User.java                   # Tài khoản và thông tin user
  	  │   │   ├── interceptor/RateLimitInterceptor.java # Giới hạn tần suất request
  	  │   │   ├── mapper/
  	  │   │   │   ├── ProductMapper.java          # Đổi Product sang DTO
      │   │   │   └── UserMapper.java             # Đổi User sang DTO
  	  │   │   ├── repository/
      │   │   │   ├── CartRepository.java         # Truy vấn giỏ hàng
      │   │   │   ├── ChatMessageRepository.java  # Lưu tin nhắn chatbot
  	  │   │   │   ├── OrderItemRepository.java    # Truy vấn dòng sản phẩm trong đơn
  	  │   │   │   ├── OrderRepository.java        # Truy vấn đơn hàng và doanh thu
  	  │   │   │   ├── PaymentRepository.java      # Lưu thông tin thanh toán
  	  │   │   │   ├── ProductImageRepository.java # Lưu ảnh sản phẩm
  	  │   │   │   ├── ProductRepository.java      # Truy vấn và lọc sản phẩm
  	  │   │   │   ├── ReviewRepository.java      # Lưu đánh giá và điểm rating
  	  │   │   │   └── UserRepository.java         # Truy vấn tài khoản
      │   │   ├── service/                         # Xử lý nghiệp vụ ứng dụng
  	  │   │   │   ├── AuthService.java             # Đăng nhập và xác minh tài khoản
  	  │   │   │   ├── CartService.java             # Quy tắc và thao tác giỏ hàng
      │   │   │   ├── ChatBotService.java          # Xử lý câu hỏi chatbot
  	  │   │   │   ├── ChatMessageService.java      # Lưu và đọc hội thoại
  	  │   │   │   ├── EmailService.java            # Gửi email xác minh và reset mật khẩu
  	  │   │   │   ├── FileService.java             # Upload và lưu trữ file
  	  │   │   │   ├── OrderService.java            # Tạo và xử lý đơn hàng
  	  │   │   │   ├── PaymentService.java          # Điều phối thanh toán
  	  │   │   │   ├── PaymentStrategy.java         # Interface chung cho phương thức trả tiền
  	  │   │   │   ├── ProductExcelService.java     # Nhập và xuất sản phẩm bằng Excel
  	  │   │   │   ├── ProductImageService.java     # Quản lý ảnh sản phẩm
  	  │   │   │   ├── ProductService.java          # Nghiệp vụ tạo, sửa, tìm sản phẩm
  	  │   │   │   ├── RevenueService.java          # Tính doanh thu và thống kê
  	  │   │   │   ├── ReviewService.java           # Tạo và quản lý đánh giá
  	  │   │   │   ├── UserService.java             # Quản lý tài khoản và quyền
  	  │   │   │   ├── payment/
  	  │   │   │   │   ├── CODStrategy.java         # Thanh toán khi nhận hàng
  	  │   │   │   │   └── VNPayStrategy.java       # Tạo và xác minh giao dịch VNPay
  	  │   │   │   └── user/
  	  │   │   │       ├── UserDetailsCustom.java   # Nối User với Spring Security
  	  │   │   │       └── UserPrincipal.java       # Thông tin user đang đăng nhập
      │   │   └── utils/
      │   │       ├── SecurityUtil.java            # Lấy user từ SecurityContext
  	  │   │       ├── VNPayUtil.java               # Tạo hash và chữ ký VNPay
  	  │   │       ├── annotation/
  	  │   │       │   ├── ApiMessage.java          # Annotation cho thông báo API
  	  │   │       │   └── RateLimit.java           # Annotation bật giới hạn request
  	  │   │       ├── error/
  	  │   │       │   ├── FailRequestException.java # Request không hợp lệ
  	  │   │       │   ├── GlobalException.java      # Bắt và chuẩn hóa lỗi REST
  	  │   │       │   ├── NotFindException.java     # Không tìm thấy tài nguyên
  	  │   │       │   ├── StorageException.java     # Lỗi lưu trữ file
   	  │   │       │   └── UnauthorizedException.java # Lỗi xác thực hoặc phân quyền
  	  │   │       └── format_response/
   	  │   │           ├── FormatResponse.java      # Tạo response thành công hoặc lỗi
  	  │   │           └── RestResponse.java        # Cấu trúc response REST chung
   	  │   └── resources/
  	  │       ├── application.properties            # Database, JWT, mail, VNPay, Gemini
  	  │       ├── static/verifySuccess.html         # Trang xác minh email thành công
	  │       └── templates/
  	  │           ├── passwordReset.html            # Mẫu email đặt lại mật khẩu
  	  │           └── verify.html                   # Mẫu email xác minh tài khoản
  	  └── test/java/vn/techzone/khieu/
  	      └── KhieuApplicationTests.java             # Kiểm tra Spring context
```

---

## 🔐 Key Features

- Authentication with JWT (Access + Refresh Token)
- Email verification & password reset
- Secure password hashing using Argon2
- HTTP-only Cookie for authentication
- Admin management (users & products)
- CRUD operations for core entities

---

## 🗄️ Database Design

![ERD](docs/RelationalSchema.png)

This diagram shows the main entities and relationships of the TechZone backend.

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or later
- PostgreSQL
- Maven, or use the included Maven Wrapper

### Clone and Run

```bash
git clone https://github.com/dangngockhieu/Laptopshop.git
cd Laptopshop
./mvnw spring-boot:run
```

On Windows, run `mvnw.cmd spring-boot:run` instead.

---

## ⚙️ Configuration

The application reads configuration from environment variables. Set the PostgreSQL, JWT, email, file-upload, VNPay, and Gemini variables referenced in `src/main/resources/application.properties` before starting the application.

The main API documentation is available at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 🧠 Dev Notes

- Passwords are securely hashed using Argon2
- JWT is used for authentication (access + refresh tokens)
- Authentication is handled via HTTP-only cookies
- Follows layered architecture with controllers, services, repositories, entities, DTOs, and mappers
