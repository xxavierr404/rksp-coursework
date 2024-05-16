package com.xxavierr404.profile.controller;

import com.xxavierr404.profile.domain.UserProfile;
import com.xxavierr404.profile.dto.UserProfileResponseDto;
import com.xxavierr404.profile.dto.UserProfileUpdateRequestDto;
import com.xxavierr404.profile.security.UserAuthContext;
import com.xxavierr404.profile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("user-profile")
    public ResponseEntity<UserProfileResponseDto> getProfile(
            @AuthenticationPrincipal UserAuthContext userAuthContext
    ) {
        var userId = userAuthContext.getUser().getId();
        var profile = userProfileService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Профиль не найден"));
        var user = userProfileService.getUser(userId);
        return ResponseEntity.ok(new UserProfileResponseDto(profile, user));
    }

    @GetMapping("user-profile/{id}")
    public ResponseEntity<UserProfileResponseDto> getProfileById(
            @PathVariable Long id
    ) {
        var profile = userProfileService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Профиль не найден"));
        var user = userProfileService.getUser(id);
        return ResponseEntity.ok(new UserProfileResponseDto(profile, user));
    }

    @GetMapping("user-profile/company/{id}")
    public ResponseEntity<UserProfileResponseDto> getAllProfilesByOrganization(
            @PathVariable Long id
    ) {
        var profile = userProfileService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Профиль не найден"));
        var user = userProfileService.getUser(id);
        return ResponseEntity.ok(new UserProfileResponseDto(profile, user));
    }

    @PostMapping("user-profile")
    public ResponseEntity<Void> createProfile(
            @AuthenticationPrincipal UserAuthContext userAuthContext
    ) {
        var userId = userAuthContext.getUser().getId();
        var profile = UserProfile.builder()
                .id(userId)
                .build();
        userProfileService.create(profile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("user-profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @RequestBody UserProfileUpdateRequestDto dto,
            @AuthenticationPrincipal UserAuthContext userAuthContext
    ) {
        var userId = userAuthContext.getUser().getId();
        userProfileService.update(
                UserProfile.builder()
                        .id(userId)
                        .description(dto.getDescription())
                        .position(dto.getPosition())
                        .build()
        );
        return ResponseEntity.ok().build();
    }
}
