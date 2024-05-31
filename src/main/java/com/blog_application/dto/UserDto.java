package com.blog_application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    //@NotBlank: Checks that the string is not empty and does not consist of only white spaces.
    //@NotEmpty: Checks that a string, collection, array, or Map is not empty.
    //@NotNull: Checks that the value of a variable is not null.

    private Long id;

    @NotBlank
    @Size(min = 4 , message = "Username must be min of 4 characters")
    private String name;

    @NotBlank
    @Email(message = "Email address is not valid")
    private String email;

    @NotBlank
    private String about;

    @NotBlank
    @Size(min = 4 , max = 20 , message = "Password must be min of 4 and max of 20 characters")
    private String password;

}
