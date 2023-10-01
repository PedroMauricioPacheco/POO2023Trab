package com.example.application.views.main;

import com.example.application.dao.CategoriaDataProvider;
import com.example.application.entidade.CategoriaDTO;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.Arrays;
import java.util.List;

public class CadastrarCategorias extends Div {
    private Crud<CategoriaDTO> crud;
    private CategoriaDataProvider dataProvider = new CategoriaDataProvider();
    private String CATEGORIA = "categoria";
    private String EDIT_COLUMN = "vaadin-crud-edit-column";

    public CadastrarCategorias() {
        crud = new Crud<>(CategoriaDTO.class, createEditor());

        setupGrid();
        setupDataProvider();
        setupToolbar();

        add(crud);

    }

    private CrudEditor<CategoriaDTO> createEditor() {
        TextField categoria = new TextField("Categoria");


        FormLayout form = new FormLayout(categoria);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2),
                new FormLayout.ResponsiveStep("1000px", 3));


        Binder<CategoriaDTO> binder = new Binder<>(CategoriaDTO.class);
        binder.forField(categoria).asRequired().bind(CategoriaDTO::getCategoria, CategoriaDTO::setCategoria);

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupGrid() {
        Grid<CategoriaDTO> grid = crud.getGrid();

        List<String> visibleColumns = Arrays.asList(CATEGORIA, EDIT_COLUMN);
        grid.getColumns().forEach(column -> {
            String key = column.getKey();
            if (!visibleColumns.contains(key)) {
                grid.removeColumn(column);
            }
        });

        grid.setColumnOrder(grid.getColumnByKey(CATEGORIA),
                grid.getColumnByKey(EDIT_COLUMN));

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

    }

    private void setupDataProvider() {
        CategoriaDataProvider dataProvider = new CategoriaDataProvider();
        crud.setDataProvider(dataProvider);
        crud.addDeleteListener(
                deleteEvent -> {
                    dataProvider.delete(deleteEvent.getItem());
                    crud.getDataProvider().refreshAll();
                });

        crud.addSaveListener(
                saveEvent -> {
                    dataProvider.persist(saveEvent.getItem());
                    crud.getDataProvider().refreshAll();
                });
    }

    private void setupToolbar() {


        Html total = new Html("<span>Quantidade de categorias: <b>" + dataProvider.DATABASE.size());

        Button button = new Button("Nova Categoria", VaadinIcon.PLUS.create());
        button.addClickListener(event -> {
            crud.edit(new CategoriaDTO(), Crud.EditMode.NEW_ITEM);
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        crud.setNewButton(button);

        HorizontalLayout toolbar = new HorizontalLayout(total);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setFlexGrow(1, toolbar);
        toolbar.setSpacing(false);
        crud.setToolbar(toolbar);
    }
}
