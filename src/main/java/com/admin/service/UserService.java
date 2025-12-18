package com.admin.service;

import com.admin.dto.PageRequest;
import com.admin.dto.UserQueryDTO;
import com.admin.entity.User;
import com.admin.service.base.BaseService;
import com.admin.util.PageResult;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author Admin
 * @date 2024-01-01
 */
public interface UserService extends BaseService<User> {
    /**
     * 根据用户名查询用户
     */
    User getUserByUserName(String userName);

    /**
     * 分页查询用户
     *
     * @param request 分页请求
     * @param query   查询条件
     * @return 分页结果
     */
    PageResult<User> pageUsers(PageRequest request, UserQueryDTO query);

    /**
     * 根据条件查询用户列表
     *
     * @param query 查询条件
     * @return 用户列表
     */
    List<User> listUsersByCondition(UserQueryDTO query);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 更新用户状态
     *
     * @param id        用户ID
     * @param isEnabled 是否启用
     */
    void updateUserStatus(Long id, Boolean isEnabled);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     */
    void batchDeleteUsers(List<Long> ids);

    /**
     * 批量更新用户状态
     *
     * @param ids       用户ID列表
     * @param isEnabled 是否启用
     */
    void batchUpdateUserStatus(List<Long> ids, Boolean isEnabled);

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

    /**
     * 解锁用户
     *
     * @param id 用户ID
     */
    void unlockUser(Long id);

    /**
     * 批量解锁用户
     *
     * @param ids 用户ID列表
     */
    void batchUnlockUsers(List<Long> ids);

    /**
     * 发送密码重置邮件
     *
     * @param userName 用户名
     * @param email 邮箱
     */
    void sendPasswordResetEmail(String userName, String email);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);
}

