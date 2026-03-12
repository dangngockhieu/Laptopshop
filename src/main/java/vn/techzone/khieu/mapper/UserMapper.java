package vn.techzone.khieu.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import vn.techzone.khieu.dto.request.user.CreateUserDTO;
import vn.techzone.khieu.dto.request.user.RegisterUserDTO;
import vn.techzone.khieu.dto.response.user.ResUserDTO;
import vn.techzone.khieu.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toCreateUser(CreateUserDTO dto);

    User toRegisterUser(RegisterUserDTO dto);

    ResUserDTO toResUserDTO(User user);
}
