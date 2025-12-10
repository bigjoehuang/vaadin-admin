package com.admin.mapper;

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
     * 查询所有用户
     */
    List<User> selectAll();

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户
     */
    int updateById(User user);

    /**
     * 根据ID删除用户
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新用户密码
     *
     * @param id       用户ID
     * @param password 新密码（已加密）
     * @return 更新行数
     */
    int updatePasswordById(@Param("id") Long id, @Param("password") String password);
}

