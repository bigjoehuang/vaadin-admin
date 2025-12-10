package com.admin.service.impl;

import com.admin.entity.Role;
import com.admin.mapper.RoleMapper;
import com.admin.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public Role getRoleById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public List<Role> listRoles() {
        return roleMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(Role role) {
        roleMapper.insert(role);
        log.info("保存角色成功，ID: {}", role.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Role role) {
        roleMapper.updateById(role);
        log.info("更新角色成功，ID: {}", role.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        roleMapper.deleteById(id);
        log.info("删除角色成功，ID: {}", id);
    }
}

