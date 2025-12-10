package com.admin.service.impl;

import com.admin.entity.User;
import com.admin.exception.BusinessException;
import com.admin.exception.ErrorCode;
import com.admin.mapper.UserMapper;
import com.admin.service.UserService;
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

    @Override
    public User getUserById(Long id) {
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
    public List<User> listUsers() {
        return userMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        // 检查用户名是否已存在
        User existUser = userMapper.selectByUserName(user.getUserName());
        if (existUser != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
        userMapper.insert(user);
        log.info("保存用户成功，ID: {}", user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        userMapper.updateById(user);
        log.info("更新用户成功，ID: {}", user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        userMapper.deleteById(id);
        log.info("删除用户成功，ID: {}", id);
    }
}

