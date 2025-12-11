package ${packageName}.views;

import ${packageName}.component.BaseFormDialog;
import ${packageName}.dto.PageRequest;
import ${packageName}.dto.${entityName}QueryDTO;
import ${packageName}.entity.${entityName};
import ${packageName}.service.${entityName}Service;
import ${packageName}.util.DataProviderUtil;
import ${packageName}.util.I18NUtil;
import ${packageName}.util.PageResult;
import ${packageName}.views.MainLayout;
import ${packageName}.views.base.BaseListView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * ${tableComment}列表视图
 *
 * @author ${author}
 * @date ${date}
 */
@Route(value = "${entityName?uncap_first}s", layout = MainLayout.class)
public class ${entityName}ListView extends BaseListView<${entityName}, ${entityName}Service> implements HasDynamicTitle {

    // 查询条件
    private ${entityName}QueryDTO currentQuery = new ${entityName}QueryDTO();
    private PageRequest currentPageRequest = new PageRequest();

    // 当前分页数据
    private PageResult<${entityName}> currentPageResult;
    
    // DataProvider
    private DataProvider<${entityName}, Void> dataProvider;

    public ${entityName}ListView(${entityName}Service ${entityName?uncap_first}Service) {
        super(${entityName?uncap_first}Service, ${entityName}.class, I18NUtil.get("${entityName?uncap_first}.title"), I18NUtil.get("${entityName?uncap_first}.add"), "${entityName?uncap_first}-list-view");
        
        // 初始化 DataProvider
        initDataProvider();
        
        // 重新构建布局
        removeAll();
        add(getToolbar(), grid);
        
        // 初始化查询
        performSearch();
    }

    /**
     * 初始化 DataProvider
     */
    private void initDataProvider() {
        // 确保分页请求已初始化
        if (currentPageRequest == null) {
            currentPageRequest = new PageRequest();
        }
        
        // 设置 Grid 的 pageSize 与分页请求一致
        grid.setPageSize(currentPageRequest.getPageSize());
        
        dataProvider = DataProviderUtil.createPageDataProvider(
            () -> currentQuery != null ? currentQuery : new ${entityName}QueryDTO(),
            () -> {
                // 确保返回的分页请求不为 null
                if (currentPageRequest == null) {
                    currentPageRequest = new PageRequest();
                }
                return currentPageRequest;
            },
            service::page${entityName}s
        );
        grid.setDataProvider(dataProvider);
    }

    @Override
    protected void configureColumns() {
        grid.removeAllColumns();
        
        grid.addColumn(${entityName}::getId).setHeader("ID").setWidth("80px").setFlexGrow(0).setSortable(true);
        // TODO: 添加其他列配置
    }

    @Override
    protected List<${entityName}> getListData() {
        // 使用 DataProvider 懒加载，这里返回空列表
        return new ArrayList<>();
    }

    @Override
    protected BaseFormDialog<${entityName}> getFormDialog(boolean isEdit, ${entityName} entity) {
        // TODO: 实现表单对话框
        throw new UnsupportedOperationException("请实现 getFormDialog 方法");
    }

    @Override
    protected void performDelete(${entityName} entity) {
        service.delete${entityName}(entity.getId());
    }

    /**
     * 执行搜索
     */
    private void performSearch() {
        try {
            // 确保分页请求不为null
            if (currentPageRequest == null) {
                currentPageRequest = new PageRequest();
            }

            // 构建查询条件
            buildQuery();

            // 确保查询条件不为null
            if (currentQuery == null) {
                currentQuery = new ${entityName}QueryDTO();
            }

            // 刷新 DataProvider（懒加载会自动从 Service 获取数据）
            if (dataProvider != null) {
                dataProvider.refreshAll();
            }
            
            // 获取当前页数据用于更新分页信息
            currentPageResult = service.page${entityName}s(currentPageRequest, currentQuery);

        } catch (Exception e) {
            com.admin.util.NotificationUtil.showError(I18NUtil.get("${entityName?uncap_first}.query.failed", e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * 构建查询条件
     */
    private void buildQuery() {
        currentQuery = new ${entityName}QueryDTO();
        // TODO: 根据搜索字段构建查询条件
    }

    @Override
    protected void updateList() {
        // 重写此方法，使用分页查询
        performSearch();
    }

    @Override
    public String getPageTitle() {
        return I18NUtil.get("page.${entityName?uncap_first}.management");
    }
}


