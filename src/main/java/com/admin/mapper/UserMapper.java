package com.admin.mapper;

import com.admin.dto.UserQueryDTO;
import com.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper 接口
 *
 * @author Admin
 * @date 2024-01-01
 */
@Mapper
public interface UserMapper {
    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    User selectByUserName(@Param("userName") String userName);

    /**
     * 根据用户名查询用户（排除指定ID，用于编辑时验证唯一性）
     *
     * @param userName  用户名
     * @param excludeId 排除的ID
     * @return 用户信息
     */
    User selectByUserNameExcludeId(@Param("userName") String userName, @Param("excludeId") Long excludeId);

    /**
     * 查询所有用户
     */
    List<User> selectAll();

    /**
     * 根据条件查询用户列表
     *
     * @param query 查询条件
     * @return 用户列表
     */
    List<User> selectByCondition(UserQueryDTO query);

    /**
     * 根据条件统计用户数量
     *
     * @param query 查询条件
     * @return 记录数
     */
    Long countByCondition(UserQueryDTO query);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户
     */
    int updateById(User user);

    /**
     * 更新用户状态
     *
     * @param id        用户ID
     * @param isEnabled 是否启用
     * @return 更新行数
     */
    int updateStatusById(@Param("id") Long id, @Param("isEnabled") Boolean isEnabled);

    /**
     * 根据ID删除用户
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 删除行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);

    /**
     * 批量更新用户状态
     *
     * @param ids       用户ID列表
     * @param isEnabled 是否启用
     * @return 更新行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("isEnabled") Boolean isEnabled);

    /**
     * 更新用户密码
     *
     * @param id       用户ID
     * @param password 新密码（已加密）
     * @return 更新行数
     */
    int updatePasswordById(@Param("id") Long id, @Param("password") String password);
}

