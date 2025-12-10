package com.admin.service.impl;

import com.admin.entity.Role;
import com.admin.exception.BusinessException;
import com.admin.exception.ErrorCode;
import com.admin.mapper.RoleMapper;
import com.admin.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现
 *
 * @author Admin
 * @date 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleMapper roleMapper;

    @Override
    public Role getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        return role;
    }

    @Override
    public Role getRoleByCode(String code) {
        return roleMapper.selectByCode(code);
    }

    @Override
    public List<Role> listRoles() {
        return roleMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(Role role) {
        // 检查角色编码是否已存在
        Role existRole = roleMapper.selectByCode(role.getCode());
        if (existRole != null) {
            throw new BusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
        }
        roleMapper.insert(role);
        log.info("保存角色成功，ID: {}", role.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Role role) {
        Role existRole = roleMapper.selectById(role.getId());
        if (existRole == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        roleMapper.updateById(role);
        log.info("更新角色成功，ID: {}", role.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        Role existRole = roleMapper.selectById(id);
        if (existRole == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        roleMapper.deleteById(id);
        log.info("删除角色成功，ID: {}", id);
    }
}

