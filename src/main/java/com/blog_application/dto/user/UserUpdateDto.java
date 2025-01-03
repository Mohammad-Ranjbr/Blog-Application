package com.blog_application.dto.user;

import com.blog_application.util.constants.ApplicationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "UserUpdateDto Model Information")
public class UserUpdateDto {

    @Schema(description = "Blog user name")
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @Size(max = 200, message = "About must be less than 200 characters")
    private String about;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    private String userName;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = ApplicationConstants.PHONE_PATTERN_REGEX, message = "Phone number must be 11 digits and start with 09")
    private String phoneNumber;

}
