package org.example.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterDTO{
    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không đúng định dạng (ví dụ: user123@gmail.com)!")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|vn)$",
            message = "Email phải có đuôi .com hoặc .vn"
    )
    private String email;

    @NotBlank(message = "Password không được để trống!")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự!")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Mật khẩu phải có cả chữ và số và ký tự đặc biệt!")
    private String password;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu!")
    private String confirmPassword;

    @NotBlank(message = "Username khong được để trống!")
    private String userName;
}
