package com.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录 DTO
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;
}



