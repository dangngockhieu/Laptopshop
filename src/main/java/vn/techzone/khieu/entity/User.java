package vn.techzone.khieu.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", indexes = @Index(name = "idx_users_email", columnList = "email"))
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Column(unique = true, length = 100)
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @Column(length = 100)
    @NotEmpty(message = "Role cannot be empty")
    private String role;

    @NotEmpty(message = "IsVerified cannot be empty")
    private Boolean isVerified = false;

    private String refreshToken;

    private LocalDateTime sentAt;

    private String verificationCode;

    private LocalDateTime codeExpired;

    @OneToMany(mappedBy = "user")
    private List<Cart> carts;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "user")
    private List<ChatMessage> chatMessages;
}