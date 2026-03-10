package vn.techzone.khieu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.techzone.khieu.dto.request.user.CreateUserDTO;
import vn.techzone.khieu.entity.User;
import vn.techzone.khieu.repository.UserRepository;
import vn.techzone.khieu.service.error.NotFoundUserException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public Optional<User> findUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public User getUserById(long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException("Không tìm thấy User với ID: " + id));
    }

    public User handleCreateUser(CreateUserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setVerified(true);

        return this.userRepository.save(user);
    }

    public void handleUpdateUser(long id, User user) {
        Optional<User> existingUser = this.userRepository.findById(id);
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            updatedUser.setName(user.getName());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setPassword(user.getPassword());
            this.userRepository.save(updatedUser);
        }
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }
}
