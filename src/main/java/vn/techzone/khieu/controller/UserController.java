package vn.techzone.khieu.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import vn.techzone.khieu.dto.request.user.UpdateUserDTO;
import vn.techzone.khieu.dto.response.PageResponseDTO;
import vn.techzone.khieu.dto.response.user.ResUserDTO;
import vn.techzone.khieu.service.UserService;
import vn.techzone.khieu.utils.annotation.ApiMessage;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

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
    @ApiMessage("Tạo mới người dùng")
    public ResponseEntity<ResUserDTO> createUser(@Valid @RequestBody CreateUserDTO userDTO) {
        String hashPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(hashPassword);
        ResUserDTO user = this.userService.handleCreateUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật thông tin người dùng")
    public ResponseEntity<ResUserDTO> updateUser(@PathVariable("id") long id,
            @RequestBody UpdateUserDTO updateUserDTO) {
        ResUserDTO user = this.userService.handleUpdateUser(id, updateUserDTO);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
