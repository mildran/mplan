package com.mplan.repositorio;

import com.mplan.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);

    Optional<Usuario> findByEmail(String email);

    boolean existsByUsuario(String usuario);

    boolean existsByEmail(String email);
}