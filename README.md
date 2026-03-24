# 🛍️ TechZone – E-commerce System

Trang web mua sắm laptop trực tuyến với **Spring and React**

---

## 📘 Tổng quan

**TechZone** là một nền tảng thương mại điện tử mini, cho phép người dùng:

- Đăng ký / đăng nhập / xác thực email
- Quản lý thông tin cá nhân
- Xem và mua sản phẩm laptop
- Quản lý đơn hàng và thanh toán (trong tương lai)

Dự án bao gồm:

- 🧠 **Backend**: REST API với Spring, JPA, JWT, Argon2
- 🗄️ **Database**: PostgreSQL

---

## ⚙️ Công nghệ chính

| Phần         | Công nghệ                           | Mô tả                            |
| ------------ | ----------------------------------- | -------------------------------- |
| **Backend**  | Spring, JPA, Argon2                 | Xử lý logic & API                |
| **Auth**     | JWT, Cookies, Email Verification    | Hệ thống xác thực                |
| **Mailer**   | Gmail App Password                  | Gửi mail xác thực/reset password |
| **Database** | PostgreSQL                          | Lưu trữ dữ liệu                  |

---

## 📥 1 Clone project backend spring

```bash
git clone https://github.com/dangngockhieu/Laptopshop.git
```

## 📥 2 Clone project frontend react

```bash
git clone https://github.com/dangngockhieu/Frontend-laptopshop.git
```

```

## 🔐 Các tính năng chính

| Nhóm           | Tính năng                       | Mô tả                          |
| -------------- | ------------------------------- | ------------------------------ |
| Auth           | Đăng ký / Đăng nhập / Đăng xuất | Có xác thực email và JWT       |
| Email          | Xác thực qua email              | Gửi link xác minh              |
| Token          | Refresh Token                   | Làm mới JWT khi hết hạn        |
| Reset Password | Gửi mã đặt lại qua email        | Có thời hạn sử dụng            |
| User           | Cập nhật thông tin cá nhân      | Chỉnh sửa thông tin người dùng |
| Admin          | Quản lý người dùng / sản phẩm   | CRUD nâng cao                  |

---

🧠 Dev Notes
Mật khẩu được mã hóa bằng Argon2

Token được ký bằng JWT (access + refresh)

Xác thực qua HTTP-only Cookie

Prisma được khởi tạo theo Singleton pattern để tránh leak connection
```
