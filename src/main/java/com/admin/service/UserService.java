package com.admin.service;

import com.admin.entity.User;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author Admin
 * @date 2024-01-01
 */
public interface UserService {
    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 根据用户名查询用户
     */
    User getUserByUserName(String userName);

    /**
     * 查询所有用户
     */
    List<User> listUsers();

    /**
     * 保存用户
     */
    void saveUser(User user);

    /**
     * 更新用户
     */
    void updateUser(User user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 修改密码
     *
     * @param userId          用户ID
     * @param oldPassword     旧密码
     * @param newPassword     新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 分配用户角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 移除用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void removeUserRole(Long userId, Long roleId);
}

