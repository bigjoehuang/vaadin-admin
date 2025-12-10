package ${packageName}.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${tableComment}
 *
 * @author ${author}
 * @date ${date}
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ${entityName} extends BaseEntity {
<#-- 这里需要根据数据库表结构生成字段，暂时留空，需要手动补充 -->
}

