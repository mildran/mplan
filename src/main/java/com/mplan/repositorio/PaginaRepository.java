package com.mplan.repositorio;

import com.mplan.modelo.Libreta;
import com.mplan.modelo.Pagina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaginaRepository extends JpaRepository<Pagina, Long> {

    List<Pagina> findByLibretaOrderByOrden(Libreta libreta);
}
