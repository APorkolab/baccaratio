package com.prokey.baccaratio.mapper;

import com.prokey.baccaratio.controller.dto.UserDto;
import com.prokey.baccaratio.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(User user);
}
