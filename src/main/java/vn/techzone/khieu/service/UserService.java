package vn.techzone.khieu.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.user.CreateUserDTO;
import vn.techzone.khieu.dto.request.user.UpdateUserDTO;
import vn.techzone.khieu.dto.response.user.ResUserDTO;
import vn.techzone.khieu.entity.User;
import vn.techzone.khieu.mapper.UserMapper;
import vn.techzone.khieu.repository.UserRepository;
import vn.techzone.khieu.utils.error.NotFoundUserException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public Optional<User> findUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public List<ResUserDTO> getAllUsers() {
        List<ResUserDTO> users = userRepository.findAll().stream()
                .map(user -> userMapper.toResUserDTO(user))
                .collect(Collectors.toList());
        return users;
    }

    public ResUserDTO getUserById(long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException("Không tìm thấy User với ID: " + id));
        return this.userMapper.toResUserDTO(user);
    }

    public ResUserDTO handleCreateUser(CreateUserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        user.setVerified(true);
        return this.userMapper.toResUserDTO(user);
    }

    public ResUserDTO handleUpdateUser(long id, UpdateUserDTO updateUserDTO) {
        User existingUser = this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException("Không tìm thấy User với ID: " + id));
        userMapper.updateUserFromDTO(updateUserDTO, existingUser);
        User user = this.userRepository.save(existingUser);
        return this.userMapper.toResUserDTO(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }
}
