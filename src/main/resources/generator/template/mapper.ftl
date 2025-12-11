package ${packageName}.mapper;

import ${packageName}.entity.${entityName};
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ${tableComment} Mapper 接口
 *
 * @author ${author}
 * @date ${date}
 */
@Mapper
public interface ${entityName}Mapper {
    /**
     * 根据ID查询
     */
    ${entityName} selectById(@Param("id") Long id);

    /**
     * 查询所有
     */
    List<${entityName}> selectAll();

    /**
     * 插入
     */
    int insert(${entityName} entity);

    /**
     * 更新
     */
    int updateById(${entityName} entity);

    /**
     * 删除
     */
    int deleteById(@Param("id") Long id);
}


