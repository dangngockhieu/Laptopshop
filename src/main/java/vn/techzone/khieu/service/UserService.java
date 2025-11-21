package vn.techzone.khieu.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.user.CreateUserDTO;
import vn.techzone.khieu.dto.request.user.UpdatePasswordDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.user.ResUserDTO;
import vn.techzone.khieu.entity.User;
import vn.techzone.khieu.mapper.UserMapper;
import vn.techzone.khieu.repository.UserRepository;
import vn.techzone.khieu.utils.GenericSpecification;
import vn.techzone.khieu.utils.error.FailRequestException;
import vn.techzone.khieu.utils.error.NotFindException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public Optional<User> findUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public PageResponseDTO<ResUserDTO> getAllUsers(Pageable pageable, String keyword) {
        Specification<User> spec = GenericSpecification.<User>equal("verified", true);

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(
                    GenericSpecification.<User>like("email", keyword)
                            .or(GenericSpecification.<User>like("name", keyword)));
        }
        Page<User> userPage = userRepository.findAll(spec, pageable);
        List<ResUserDTO> users = userPage.getContent().stream()
                .map(userMapper::toResUserDTO)
                .collect(Collectors.toList());

        return new PageResponseDTO<ResUserDTO>(
                users,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.getNumber() + 1,
                userPage.getSize());
    }

    public ResUserDTO getUserById(long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new NotFindException("Không tìm thấy User với ID: " + id));
        return this.userMapper.toResUserDTO(user);
    }

    public ResUserDTO handleCreateUserForAdmin(CreateUserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new FailRequestException("Thông tin gửi lên Không hợp lệ");
        }
        String hashPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(hashPassword);
        User user = userMapper.toCreateUser(userDTO);
        user.setVerified(true);
        User savedUser = this.userRepository.save(user);
        return this.userMapper.toResUserDTO(savedUser);
    }

    public ResUserDTO handleUpdatePassword(String email, UpdatePasswordDTO updatePasswordDTO) {
        if (!updatePasswordDTO.getNewPassword().equals(updatePasswordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        User existingUser = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFindException("Không tìm thấy User với ID: " + email));
        boolean isMatch = passwordEncoder.matches(updatePasswordDTO.getOldPassword(), existingUser.getPassword());
        if (!isMatch) {
            throw new IllegalArgumentException("Mật khẩu không chính xác");
        }
        String hashPassword = passwordEncoder.encode(updatePasswordDTO.getNewPassword());
        existingUser.setPassword(hashPassword);
        User user = this.userRepository.save(existingUser);
        return this.userMapper.toResUserDTO(user);
    }

    public ResUserDTO handleUpdateRole(long id, String role) {
        User existingUser = this.userRepository.findById(id)
                .orElseThrow(() -> new NotFindException("Không tìm thấy User với ID: " + id));
        if (!role.equals("USER") && !role.equals("ADMIN")) {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }
        existingUser.setRole(role);
        User user = this.userRepository.save(existingUser);
        return this.userMapper.toResUserDTO(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public long countUsers() {
        return this.userRepository.countByVerifiedTrue();
    }

}
