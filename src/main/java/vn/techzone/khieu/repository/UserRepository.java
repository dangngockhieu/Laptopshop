package vn.techzone.khieu.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.techzone.khieu.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndRefreshToken(String email, String refreshToken);

    boolean existsByEmail(String email);

    long countByVerifiedTrue();

}
