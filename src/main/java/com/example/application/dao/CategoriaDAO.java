package com.example.application.dao;

import com.example.application.connection.ConexaoBanco;
import com.example.application.entidade.CategoriaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    private static Connection connection;

    static {
        try {
            connection = new ConexaoBanco().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void cadastrarCategoria(CategoriaDTO categoria) {
        String sql = """
                    INSERT INTO categoria (categoria)
                    VALUES (?);
            """;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao usar o prepareStatement", e);
        }
        try {
            preparedStatement.setString(1, categoria.getCategoria());
            preparedStatement.executeUpdate();

        } catch (SQLException ex){
            throw new RuntimeException(ex);
        } finally {
            if (preparedStatement !=null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException("Erro ao fechar o prepareStatement", e);
                }
            }
        }
    }
    public static CategoriaDTO encontrarCategoriaPorId(Integer id) {
        String sql = "SELECT * FROM categoria WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    CategoriaDTO categoria = new CategoriaDTO();
                    categoria.setId(resultSet.getInt("id"));
                    categoria.setCategoria(resultSet.getString("categoria"));
                    return categoria;
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao encontrar categoria por ID", ex);
        }
    }

    public static void atualizarCategoriaPorId(Integer id, CategoriaDTO novaCategoria) {
        String sql = "UPDATE categoria SET categoria = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, novaCategoria.getCategoria());
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao atualizar categoria por ID", ex);
        }
    }

    public static List<CategoriaDTO> listarTodasCategorias() {
        List<CategoriaDTO> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                CategoriaDTO categoria = new CategoriaDTO();
                categoria.setId(resultSet.getInt("id"));
                categoria.setCategoria(resultSet.getString("categoria"));
                categorias.add(categoria);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar todas as categorias", ex);
        }
        return categorias;
    }


    public static void excluirCategoriaPorId(Integer id) {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao excluir categoria por ID", ex);
        }
    }

}
