package com.admin.service.impl;

import com.admin.dto.PageRequest;
import com.admin.dto.RoleQueryDTO;
import com.admin.entity.Role;
import com.admin.exception.BusinessException;
import com.admin.exception.ErrorCode;
import com.admin.mapper.RoleMapper;
import com.admin.mapper.RolePermissionMapper;
import com.admin.service.RoleService;
import com.admin.util.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    private final RolePermissionMapper rolePermissionMapper;

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
    public PageResult<Role> pageRoles(PageRequest request, RoleQueryDTO query) {
        // 参数校验
        if (request == null) {
            request = new PageRequest(); // 使用默认分页参数
        }
        if (query == null) {
            query = new RoleQueryDTO(); // 使用默认查询条件
        }
        
        // 使用PageHelper进行分页
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<Role> roles = roleMapper.selectByCondition(query);
        PageInfo<Role> pageInfo = new PageInfo<>(roles);
        
        return PageResult.success(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize()
        );
    }

    @Override
    public List<Role> listRolesByCondition(RoleQueryDTO query) {
        return roleMapper.selectByCondition(query);
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
        log.info("保存角色成功，ID: {}, 编码: {}", role.getId(), role.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Role role) {
        Role existRole = roleMapper.selectById(role.getId());
        if (existRole == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        
        // 修复：检查编码唯一性时排除当前记录
        Role existRoleByCode = roleMapper.selectByCodeExcludeId(role.getCode(), role.getId());
        if (existRoleByCode != null) {
            throw new BusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
        }
        
        roleMapper.updateById(role);
        log.info("更新角色成功，ID: {}, 编码: {}", role.getId(), role.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(Long id, Boolean isEnabled) {
        Role existRole = roleMapper.selectById(id);
        if (existRole == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        roleMapper.updateStatusById(id, isEnabled);
        log.info("更新角色状态成功，ID: {}, 状态: {}", id, isEnabled ? "启用" : "禁用");
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRoles(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "角色ID列表不能为空");
        }
        int count = roleMapper.batchDeleteByIds(ids);
        log.info("批量删除角色成功，删除数量: {}", count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateRoleStatus(List<Long> ids, Boolean isEnabled) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "角色ID列表不能为空");
        }
        int count = roleMapper.batchUpdateStatus(ids, isEnabled);
        log.info("批量更新角色状态成功，更新数量: {}, 状态: {}", count, isEnabled ? "启用" : "禁用");
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 验证角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 先删除角色的所有权限关联
        rolePermissionMapper.deleteByRoleId(roleId);

        // 如果有权限ID，则批量插入
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.insertBatch(roleId, permissionIds);
        }

        log.info("分配角色权限成功，角色ID: {}, 权限ID列表: {}", roleId, permissionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRolePermission(Long roleId, Long permissionId) {
        rolePermissionMapper.deleteByRoleIdAndPermissionId(roleId, permissionId);
        log.info("移除角色权限成功，角色ID: {}, 权限ID: {}", roleId, permissionId);
    }
}

