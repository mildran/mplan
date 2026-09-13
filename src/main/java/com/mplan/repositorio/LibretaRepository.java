package com.mplan.repositorio;

import com.mplan.modelo.Libreta;
import com.mplan.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibretaRepository extends JpaRepository<Libreta, Long> {

    List<Libreta> findByUsuarioOrderByNombre(Usuario usuario);
}
