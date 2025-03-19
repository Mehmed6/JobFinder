package com.doganmehmet.app.mapper;

import com.doganmehmet.app.dto.register.RegisterRequestDTO;
import com.doganmehmet.app.dto.user.UserDTO;
import com.doganmehmet.app.entity.User;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(implementationName = "UserMapperImpl", componentModel = "spring")
public interface IUserMapper {

    User toUser(RegisterRequestDTO registerRequestDTO);

    UserDTO toUserDTO(User user);

    default Page<UserDTO> toUserDTOPage(Page<User> users)
    {
        return users.map(this::toUserDTO);
    }
}
