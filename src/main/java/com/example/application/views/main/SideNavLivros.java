package com.example.application.views.main;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@PageTitle("Main")
@Route("")
public class SideNavLivros extends AppLayout {

        public SideNavLivros() {
            DrawerToggle toggle = new DrawerToggle();

            H1 title = new H1("Livraria");
            title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                    .set("margin", "0");

            Tabs tabs = getTabs();

            addToDrawer(tabs);
            addToNavbar(toggle, title);

            setPrimarySection(Section.DRAWER);

            tabs.addSelectedChangeListener(event -> {
               String nome = event.getSelectedTab().getLabel();
               if (nome.equals("Livros")) {
                   if (this.getContent() != null) {
                       this.setContent(null);
                       this.setContent(new LivrosView());
                   } else {
                       this.setContent(null);
                       this.setContent(new LivrosView());
                   }
               } else if (nome.equals("Categorias")) {
                     if (this.getContent() != null) {
                          this.setContent(null);
                          this.setContent(new CadastrarCategorias());
                     } else {
                         this.setContent(null);
                          this.setContent(new CadastrarCategorias());
                     }
               } else {
                   this.setContent(null);
               }
            });
        }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.add(createTab(VaadinIcon.DASHBOARD, "Dashboard"),
                createTab(VaadinIcon.OPEN_BOOK, "Livros"), createTab(VaadinIcon.MODAL_LIST, "Categorias"));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private Tab createTab(VaadinIcon viewIcon, String viewName) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        link.setTabIndex(-1);

        Tab tab = new Tab(link);
        tab.setLabel(viewName);
        return tab;
    }
}
