package com.blog_application.dto.user;

import com.blog_application.dto.image.ImageData;
import com.blog_application.util.constants.ApplicationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserCreateDto extends UserUpdateDto {

    @NotBlank(message = "Password is mandatory")
    @Pattern(regexp = ApplicationConstants.PASSWORD_PATTERN_REGEX,
            message = "Password must contain at least one uppercase letter, one lowercase letter, one special character, and be at least 8 characters long")
    private String password;
    private ImageData imageData;

}
