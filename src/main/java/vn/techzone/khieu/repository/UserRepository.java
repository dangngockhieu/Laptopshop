package vn.techzone.khieu.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.techzone.khieu.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.verified = true " +
            "AND (:keyword IS NULL OR u.email LIKE %:keyword% OR u.name LIKE %:keyword%)")
    Page<User> findVerifiedUsers(@Param("keyword") String keyword, Pageable pageable);

}
