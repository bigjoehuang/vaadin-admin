package ${packageName}.controller;

import ${packageName}.entity.${entityName};
import ${packageName}.service.${entityName}Service;
import ${packageName}.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ${tableComment}控制器
 *
 * @author ${author}
 * @date ${date}
 */
@RestController
@RequestMapping("/api/${entityName?uncap_first}")
@RequiredArgsConstructor
public class ${entityName}Controller {

    private final ${entityName}Service ${entityName?uncap_first}Service;

    @GetMapping("/{id}")
    public Result<${entityName}> getById(@PathVariable Long id) {
        ${entityName} entity = ${entityName?uncap_first}Service.get${entityName}ById(id);
        return Result.success(entity);
    }

    @GetMapping("/list")
    public Result<List<${entityName}>> list() {
        List<${entityName}> list = ${entityName?uncap_first}Service.list${entityName}s();
        return Result.success(list);
    }

    @PostMapping
    public Result<?> save(@RequestBody ${entityName} entity) {
        ${entityName?uncap_first}Service.save${entityName}(entity);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ${entityName} entity) {
        entity.setId(id);
        ${entityName?uncap_first}Service.update${entityName}(entity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        ${entityName?uncap_first}Service.delete${entityName}(id);
        return Result.success();
    }
}



