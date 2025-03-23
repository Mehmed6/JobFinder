package com.doganmehmet.app.mapper;

import com.doganmehmet.app.dto.register.RegisterRequestDTO;
import com.doganmehmet.app.dto.user.UpdateUserDTO;
import com.doganmehmet.app.dto.user.UserDTO;
import com.doganmehmet.app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(implementationName = "UserMapperImpl", componentModel = "spring")
public interface IUserMapper {

    User toUser(RegisterRequestDTO registerRequestDTO);

    @Mapping(target = "companyName", source = "company.name")
    UserDTO toUserDTO(User user);

    UpdateUserDTO toUpdateUserDTO(User user);

    default Page<UserDTO> toUserDTOPage(Page<User> users)
    {
        return users.map(this::toUserDTO);
    }
}
