package vn.techzone.khieu.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.user.CreateUserDTO;
import vn.techzone.khieu.dto.request.user.UpdatePasswordDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.user.ResUserDTO;
import vn.techzone.khieu.service.UserService;
import vn.techzone.khieu.utils.SecurityUtil;
import vn.techzone.khieu.utils.annotation.ApiMessage;
import vn.techzone.khieu.utils.error.NotFoundUserException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    @ApiMessage("Lấy danh sách người dùng")
    public ResponseEntity<PageResponseDTO<ResUserDTO>> getAllUsers(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(current - 1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        PageResponseDTO<ResUserDTO> users = this.userService.getAllUsers(pageable, keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy thông tin người dùng theo ID")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) {
        ResUserDTO user = this.userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping()
    @ApiMessage("Tạo mới người dùng cho Admin")
    public ResponseEntity<ResUserDTO> createUserForAdmin(@Valid @RequestBody CreateUserDTO userDTO) {
        ResUserDTO user = this.userService.handleCreateUserForAdmin(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/change-password")
    @ApiMessage("Cập nhật mật khẩu người dùng")
    public ResponseEntity<ResUserDTO> updatePasswordUser(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (email == null) {
            throw new NotFoundUserException("Không tìm thấy người dùng hiện tại");
        }
        ResUserDTO user = this.userService.handleUpdatePassword(email, updatePasswordDTO);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/role/{id}")
    @ApiMessage("Cập nhật quyền hạn người dùng")
    public ResponseEntity<ResUserDTO> updateRoleUser(@PathVariable("id") long id,
            @RequestBody String role) {
        ResUserDTO user = this.userService.handleUpdateRole(id, role);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/count")
    @ApiMessage("Đếm số lượng người dùng")
    public ResponseEntity<Long> countUsers() {
        long count = this.userService.countUsers();
        return ResponseEntity.ok(count);
    }
}
