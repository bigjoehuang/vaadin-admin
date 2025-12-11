<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.mapper.${entityName}Mapper">

    <resultMap id="BaseResultMap" type="${packageName}.entity.${entityName}">
        <id column="id" property="id"/>
        <result column="createdAt" property="createdAt"/>
        <result column="updatedAt" property="updatedAt"/>
    </resultMap>

    <select id="selectById" resultMap="BaseResultMap">
        SELECT id, createdAt, updatedAt
        FROM ${tableName}
        WHERE id = #{id} AND deleted = 0
    </select>

    <select id="selectAll" resultMap="BaseResultMap">
        SELECT id, createdAt, updatedAt
        FROM ${tableName}
        WHERE deleted = 0
        ORDER BY createdAt DESC
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO ${tableName} (createdAt, updatedAt)
        VALUES (NOW(), NOW())
    </insert>

    <update id="updateById">
        UPDATE ${tableName}
        SET updatedAt = NOW()
        WHERE id = #{id} AND deleted = 0
    </update>

    <update id="deleteById">
        UPDATE ${tableName}
        SET deleted = 1, updatedAt = NOW()
        WHERE id = #{id}
    </update>

</mapper>


