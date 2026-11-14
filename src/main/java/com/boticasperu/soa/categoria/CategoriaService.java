
package com.boticasperu.soa.categoria;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Listar categorías
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    // Buscar categoría por ID
    public Optional<Categoria> buscarPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    // Registrar categoría
    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    // Actualizar categoría
    public Categoria actualizarCategoria(
            Integer id, Categoria categoria) {

        categoria.setId(id);
        return categoriaRepository.save(categoria);
    }

    // Eliminar categoría
    public void eliminarCategoria(Integer id) {
        categoriaRepository.deleteById(id);
    }
}
