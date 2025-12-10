package com.admin.dto;

import lombok.Data;

/**
 * 用户 DTO
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
public class UserDTO {
    private Long id;
    private String userName;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Boolean isEnabled;
}

