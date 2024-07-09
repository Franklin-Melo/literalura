package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor,Long> {
    @Query("SELECT a FROM Autor a WHERE a.dataFalecimento IS NULL OR a.dataFalecimento >= :anoEspecifico")
    List<Autor> findAutoresVivosApartirDe(@Param("anoEspecifico") int anoEspecifico);

    @Query("SELECT a FROM Autor a WHERE a.nome LIKE %:nome%")
    List<Autor> findByNomeContaining(@Param("nome") String nome);
}
