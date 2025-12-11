package ${packageName}.service.impl;

import ${packageName}.entity.${entityName};
import ${packageName}.mapper.${entityName}Mapper;
import ${packageName}.service.${entityName}Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ${tableComment}服务实现
 *
 * @author ${author}
 * @date ${date}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ${entityName}ServiceImpl implements ${entityName}Service {

    private final ${entityName}Mapper ${entityName?uncap_first}Mapper;

    @Override
    public ${entityName} get${entityName}ById(Long id) {
        return ${entityName?uncap_first}Mapper.selectById(id);
    }

    @Override
    public List<${entityName}> list${entityName}s() {
        return ${entityName?uncap_first}Mapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save${entityName}(${entityName} entity) {
        ${entityName?uncap_first}Mapper.insert(entity);
        log.info("保存${tableComment}成功，ID: {}", entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update${entityName}(${entityName} entity) {
        ${entityName?uncap_first}Mapper.updateById(entity);
        log.info("更新${tableComment}成功，ID: {}", entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete${entityName}(Long id) {
        ${entityName?uncap_first}Mapper.deleteById(id);
        log.info("删除${tableComment}成功，ID: {}", id);
    }
}



