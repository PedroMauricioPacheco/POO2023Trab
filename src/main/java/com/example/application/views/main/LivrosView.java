package com.example.application.views.main;

import com.example.application.dao.LivroDataProvider;
import com.example.application.entidade.LivroDTO;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


@Route(value = "livros")
public class LivrosView extends Div {

    private Crud<LivroDTO> crud;
    private LivroDataProvider dataProvider = new LivroDataProvider();
    private String TITULO = "titulo";
    private String AUTOR = "autor";
    private String DESCRICAO = "descricao";
    private String CATEGORIA = "categoria";
    private String EDIT_COLUMN = "vaadin-crud-edit-column";


    public LivrosView() {
        crud = new Crud<>(LivroDTO.class, createEditor());

        setupGrid();
        setupDataProvider();
        setupToolbar();

        add(crud);

    }

    private CrudEditor<LivroDTO> createEditor() {
        TextField titulo = new TextField("Titulo");
        TextField autor = new TextField("Autor");
        TextField descricao = new TextField("Descricao");
        descricao.setMaxLength(255);
        ComboBox<String> categoria = new ComboBox<>("Categoria");
        categoria.setItems(categorias());

        FormLayout form = new FormLayout(titulo, autor, categoria,descricao);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2),
                new FormLayout.ResponsiveStep("1000px", 3));


        Binder<LivroDTO> binder = new Binder<>(LivroDTO.class);
        binder.forField(titulo).asRequired().bind(LivroDTO::getTitulo, LivroDTO::setTitulo);
        binder.forField(autor).asRequired().bind(LivroDTO::getAutor, LivroDTO::setAutor);
        binder.forField(descricao).asRequired().bind(LivroDTO::getDescricao, LivroDTO::setDescricao);
        binder.forField(categoria).asRequired().bind(LivroDTO::getCategoria, LivroDTO::setCategoria);

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupGrid() {
        Grid<LivroDTO> grid = crud.getGrid();

        List<String> visibleColumns = Arrays.asList(TITULO, AUTOR, DESCRICAO, CATEGORIA, EDIT_COLUMN);
        grid.getColumns().forEach(column -> {
            String key = column.getKey();
            if (!visibleColumns.contains(key)) {
                grid.removeColumn(column);
            }
        });

        grid.setColumnOrder(grid.getColumnByKey(TITULO),
                grid.getColumnByKey(AUTOR), grid.getColumnByKey(CATEGORIA),grid.getColumnByKey(DESCRICAO),
                grid.getColumnByKey(EDIT_COLUMN));

        grid.addColumn(createToggleDetailsRenderer(grid));
        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(createLivroDetailsRenderer());

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

    }

    private ComponentRenderer<LivroDetailsFormLayout, LivroDTO> createLivroDetailsRenderer() {
        return new ComponentRenderer<>(LivroDetailsFormLayout::new,
                LivroDetailsFormLayout::setLivro);
    }

    private Renderer<LivroDTO> createToggleDetailsRenderer(Grid<LivroDTO> grid) {
        return LitRenderer.<LivroDTO> of(
                "<vaadin-button theme=\"tertiary\" @click=\"${handleClick}\">Mais detalhes</vaadin-button>"
        ).withFunction("handleClick", livro ->{
            grid.setDetailsVisible(livro, !grid.isDetailsVisible(livro));
        });
    }

    private static class LivroDetailsFormLayout extends FormLayout {
        private final TextField titulo = new TextField("Titulo");
        private final TextField autor = new TextField("Autor");
        private final TextArea descricao = new TextArea("Descricao");
        private final TextField categoria = new TextField("Categoria");

        public LivroDetailsFormLayout() {
            Stream.of(titulo, autor, descricao,categoria).forEach(field -> {
                field.setReadOnly(true);
                add(field);
            });

            setResponsiveSteps(new ResponsiveStep("0", 3));
            setColspan(titulo, 3);
            setColspan(autor, 3);
            setColspan(descricao, 3);
            setColspan(categoria, 3);
        }
        public void setLivro(LivroDTO livro) {
            titulo.setValue(livro.getTitulo());
            autor.setValue(livro.getAutor());
            descricao.setValue(livro.getDescricao());
            categoria.setValue(livro.getCategoria());
        }
    }

    private void setupDataProvider() {
        LivroDataProvider dataProvider = new LivroDataProvider();
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


        Html total = new Html("<span>Quantidade de livros: <b>" + dataProvider.DATABASE.size());

        Button button = new Button("Novo Livro", VaadinIcon.PLUS.create());
        button.addClickListener(event -> {
            crud.edit(new LivroDTO(), Crud.EditMode.NEW_ITEM);
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        crud.setNewButton(button);

        HorizontalLayout toolbar = new HorizontalLayout(total);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setFlexGrow(1, toolbar);
        toolbar.setSpacing(false);
        crud.setToolbar(toolbar);
    }

    static List<String> categorias(){
        return Arrays.asList("Ação", "Aventura", "Comédia", "Drama", "Fantasia", "Ficção científica", "Romance", "Terror");
    }

}
