package com.example.application.entidade;

import java.util.Objects;

public class LivroDTO {
    private Integer id;
    private String titulo;
    private String autor;
    private String descricao;
    private String categoria;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LivroDTO livroDTO = (LivroDTO) o;
        return Objects.equals(id, livroDTO.id) && Objects.equals(titulo, livroDTO.titulo) && Objects.equals(autor, livroDTO.autor) && Objects.equals(descricao, livroDTO.descricao) && Objects.equals(categoria, livroDTO.categoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, autor, descricao, categoria);
    }
}
