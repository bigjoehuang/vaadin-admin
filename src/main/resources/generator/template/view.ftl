package ${packageName}.views;

import ${packageName}.entity.${entityName};
import ${packageName}.service.${entityName}Service;
import ${packageName}.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * ${tableComment}列表视图
 *
 * @author ${author}
 * @date ${date}
 */
@Route(value = "${entityName?uncap_first}s", layout = MainLayout.class)
@PageTitle("${tableComment}")
public class ${entityName}ListView extends VerticalLayout {

    private final ${entityName}Service ${entityName?uncap_first}Service;
    private final Grid<${entityName}> grid = new Grid<>(${entityName}.class, false);

    public ${entityName}ListView(${entityName}Service ${entityName?uncap_first}Service) {
        this.${entityName?uncap_first}Service = ${entityName?uncap_first}Service;
        addClassName("${entityName?uncap_first}-list-view");
        setSizeFull();

        configureGrid();
        add(getToolbar(), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addColumn(${entityName}::getId).setHeader("ID").setWidth("80px");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("添加");
        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        grid.setItems(${entityName?uncap_first}Service.list${entityName}s());
    }
}

