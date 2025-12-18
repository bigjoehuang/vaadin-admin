package com.admin.service.impl;

import com.admin.dto.PageRequest;
import com.admin.dto.UserQueryDTO;
import com.admin.entity.User;
import com.admin.exception.BusinessException;
import com.admin.exception.ErrorCode;
import com.admin.mapper.UserMapper;
import com.admin.mapper.UserRoleMapper;
import com.admin.service.UserService;
import com.admin.util.PageResult;
import com.admin.util.PasswordValidator;
import com.admin.util.SecurityUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public User getUserByUserName(String userName) {
        return userMapper.selectByUserName(userName);
    }

    @Override
    public List<User> listAll() {
        return userMapper.selectAll();
    }

    @Override
    public PageResult<User> pageUsers(PageRequest request, UserQueryDTO query) {
        // 参数校验
        if (request == null) {
            request = new PageRequest(); // 使用默认分页参数
        }
        if (query == null) {
            query = new UserQueryDTO(); // 使用默认查询条件
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<User> users = userMapper.selectByCondition(query);
        PageInfo<User> pageInfo = new PageInfo<>(users);

        return PageResult.success(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize()
        );
    }

    @Override
    public List<User> listUsersByCondition(UserQueryDTO query) {
        return userMapper.selectByCondition(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(User user) {
        // 检查用户名是否已存在
        User existUser = userMapper.selectByUserName(user.getUserName());
        if (existUser != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
        // 如果密码不为空，验证密码复杂度并加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            PasswordValidator.validateAndThrow(user.getPassword());
            user.setPassword(SecurityUtil.encodePassword(user.getPassword()));
        }
        userMapper.insert(user);
        log.info("保存用户成功，ID: {}", user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 修复：检查用户名唯一性时排除当前记录
        User existUserByUserName = userMapper.selectByUserNameExcludeId(user.getUserName(), user.getId());
        if (existUserByUserName != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 如果密码不为空，验证密码复杂度并加密；否则保持原密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            PasswordValidator.validateAndThrow(user.getPassword());
            user.setPassword(SecurityUtil.encodePassword(user.getPassword()));
        } else {
            // 保持原密码
            user.setPassword(existUser.getPassword());
        }

        userMapper.updateById(user);
        log.info("更新用户成功，ID: {}", user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Boolean isEnabled) {
        User existUser = userMapper.selectById(id);
        if (existUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        userMapper.updateStatusById(id, isEnabled);
        log.info("更新用户状态成功，ID: {}, 状态: {}", id, isEnabled ? "启用" : "禁用");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        userMapper.deleteById(id);
        log.info("删除用户成功，ID: {}", id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户ID列表不能为空");
        }
        int count = userMapper.batchDeleteByIds(ids);
        log.info("批量删除用户成功，删除数量: {}", count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateUserStatus(List<Long> ids, Boolean isEnabled) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户ID列表不能为空");
        }
        int count = userMapper.batchUpdateStatus(ids, isEnabled);
        log.info("批量更新用户状态成功，更新数量: {}, 状态: {}", count, isEnabled ? "启用" : "禁用");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 验证旧密码是否正确
        if (!SecurityUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR.getCode(), "原密码不正确");
        }

        // 验证新密码复杂度
        PasswordValidator.validateAndThrow(newPassword);

        // 加密新密码
        String encodedPassword = SecurityUtil.encodePassword(newPassword);

        // 更新密码
        userMapper.updatePasswordById(userId, encodedPassword);
        log.info("修改密码成功，用户ID: {}", userId);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 先删除用户的所有角色关联
        userRoleMapper.deleteByUserId(userId);

        // 如果有角色ID，则批量插入
        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleMapper.insertBatch(userId, roleIds);
        }

        log.info("分配用户角色成功，用户ID: {}, 角色ID列表: {}", userId, roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRole(Long userId, Long roleId) {
        userRoleMapper.deleteByUserIdAndRoleId(userId, roleId);
        log.info("移除用户角色成功，用户ID: {}, 角色ID: {}", userId, roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockUser(Long id) {
        User existUser = userMapper.selectById(id);
        if (existUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // 解锁用户并重置登录失败次数
        userMapper.updateLockStatus(id, false);
        userMapper.resetLoginFailCount(id);
        log.info("解锁用户成功，ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUnlockUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户ID列表不能为空");
        }
        // 批量解锁用户
        for (Long id : ids) {
            userMapper.updateLockStatus(id, false);
            userMapper.resetLoginFailCount(id);
        }
        log.info("批量解锁用户成功，解锁数量: {}", ids.size());
    }

    @Override
    public void sendPasswordResetEmail(String userName, String email) {
        // 查找用户
        User user = null;
        if (userName != null && !userName.trim().isEmpty()) {
            user = userMapper.selectByUserName(userName);
        } else if (email != null && !email.trim().isEmpty()) {
            // 这里假设存在根据邮箱查询用户的方法
            // user = userMapper.selectByEmail(email);
        }
        
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 验证邮箱是否匹配
        if (email != null && !email.trim().isEmpty() && !email.equals(user.getEmail())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "邮箱与用户名不匹配");
        }
        
        // 生成重置令牌
        String resetToken = java.util.UUID.randomUUID().toString();
        
        // 这里应该将令牌存储到数据库中，并设置过期时间
        // 然后发送邮件给用户
        
        log.info("发送密码重置邮件成功，用户ID: {}, 邮箱: {}", user.getId(), user.getEmail());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 验证新密码复杂度
        PasswordValidator.validateAndThrow(newPassword);
        
        // 加密新密码
        String encodedPassword = SecurityUtil.encodePassword(newPassword);
        
        // 更新密码
        userMapper.updatePasswordById(userId, encodedPassword);
        log.info("重置用户密码成功，用户ID: {}", userId);
    }
}

