package ${packageName}.service;

import ${packageName}.entity.${entityName};

import java.util.List;

/**
 * ${tableComment}服务接口
 *
 * @author ${author}
 * @date ${date}
 */
public interface ${entityName}Service {
    /**
     * 根据ID查询
     */
    ${entityName} get${entityName}ById(Long id);

    /**
     * 查询所有
     */
    List<${entityName}> list${entityName}s();

    /**
     * 保存
     */
    void save${entityName}(${entityName} entity);

    /**
     * 更新
     */
    void update${entityName}(${entityName} entity);

    /**
     * 删除
     */
    void delete${entityName}(Long id);
}





