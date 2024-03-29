package com.example.application.dao;

import com.example.application.entidade.CategoriaDTO;
import com.example.application.entidade.LivroDTO;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;

public class CategoriaDataProvider extends AbstractBackEndDataProvider<CategoriaDTO, CrudFilter> {
    public final List<CategoriaDTO> DATABASE = new ArrayList<>(getCategorias());

    public List<CategoriaDTO> getCategorias(){
        return CategoriaDAO.listarTodasCategorias();
    }

    private Consumer<Long> sizeChangeListener;

    @Override
    protected Stream<CategoriaDTO> fetchFromBackEnd(Query<CategoriaDTO, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<CategoriaDTO> stream = DATABASE.stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<CategoriaDTO, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    void setSizeChangeListener(Consumer<Long> listener) {
        sizeChangeListener = listener;
    }

    private static Predicate<CategoriaDTO> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<CategoriaDTO>) categoria -> {
                    try {
                        Object value = valueOf(constraint.getKey(), categoria);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }).reduce(Predicate::and).orElse(e -> true);
    }

    private static Comparator<CategoriaDTO> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<CategoriaDTO> comparator = Comparator.comparing(
                        categoria -> (Comparable) valueOf(sortClause.getKey(),
                                categoria));

                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }

                return comparator;

            } catch (Exception ex) {
                return (Comparator<CategoriaDTO>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, CategoriaDTO categoria) {
        try {
            Field field = CategoriaDTO.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(categoria);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void persist(CategoriaDTO item) {
        if (item.getId() == null) {
            item.setId(DATABASE.stream().map(CategoriaDTO::getId).max(naturalOrder())
                    .orElse(0) + 1);
        }

        final Optional<CategoriaDTO> existingItem = find(item.getId());
        if (existingItem.isPresent()) {
            CategoriaDTO categoria;
            int position = DATABASE.indexOf(existingItem.get());
            DATABASE.remove(existingItem.get());
            DATABASE.add(position, item);
            categoria = CategoriaDAO.encontrarCategoriaPorId(item.getId());
            CategoriaDAO.atualizarCategoriaPorId(item.getId(), categoria);
        } else {
            CategoriaDAO.cadastrarCategoria(item);
            DATABASE.add(item);
        }
    }

    Optional<CategoriaDTO> find(Integer id) {
        return DATABASE.stream().filter(entity -> entity.getId().equals(id))
                .findFirst();
    }

    public void delete(CategoriaDTO item) {
        DATABASE.removeIf(entity -> entity.getId().equals(item.getId()));
        CategoriaDAO.excluirCategoriaPorId(item.getId());
    }
}
