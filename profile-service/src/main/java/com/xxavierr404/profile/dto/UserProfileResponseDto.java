package com.xxavierr404.profile.dto;

import com.xxavierr404.profile.domain.User;
import com.xxavierr404.profile.domain.Role;
import com.xxavierr404.profile.domain.UserProfile;
import lombok.Value;

@Value
public class UserProfileResponseDto {
    Long id;
    String description;
    String position;
    String firstName;
    String lastName;
    Long organizationId;
    Role role;

    public UserProfileResponseDto(
            UserProfile userProfile,
            User user
    ) {
        this.id = userProfile.getId();
        this.description = userProfile.getDescription();
        this.position = userProfile.getPosition();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.organizationId = user.getOrganizationId();
        this.role = user.getRole();
    }
}
