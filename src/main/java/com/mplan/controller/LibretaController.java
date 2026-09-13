package com.mplan.controller;

import com.mplan.modelo.Libreta;
import com.mplan.modelo.Usuario;
import com.mplan.repositorio.LibretaRepository;
import com.mplan.repositorio.PaginaRepository;
import com.mplan.repositorio.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LibretaController {

    private final LibretaRepository libretaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PaginaRepository paginaRepository;

    public LibretaController(LibretaRepository libretaRepository, UsuarioRepository usuarioRepository, PaginaRepository paginaRepository) {
        this.libretaRepository = libretaRepository;
        this.usuarioRepository = usuarioRepository;
        this.paginaRepository = paginaRepository;
    }

    @GetMapping("/libretas")
    public String listar(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByUsuario(userDetails.getUsername()).orElseThrow();
        model.addAttribute("libretas", libretaRepository.findByUsuarioOrderByNombre(usuario));
        return "libretas";
    }

    @PostMapping("/libretas/nueva")
    public String crear(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam String nombre,
                         @RequestParam Libreta.Tamano tamano,
                         @RequestParam Libreta.Estilo estilo,
                         Model model) {

        Usuario usuario = usuarioRepository.findByUsuario(userDetails.getUsername()).orElseThrow();

        if (nombre == null || nombre.isBlank()) {
            model.addAttribute("error", "El nombre de la libreta es obligatorio.");
            model.addAttribute("libretas", libretaRepository.findByUsuarioOrderByNombre(usuario));
            return "libretas";
        }

        libretaRepository.save(new Libreta(nombre, tamano, estilo, usuario));

        return "redirect:/libretas";
    }

    @PostMapping("/libretas/{id}/borrar")
    public String borrar(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        libretaRepository.findById(id).ifPresent(libreta -> {
            if (libreta.getUsuario().getUsuario().equals(userDetails.getUsername())) {
                paginaRepository.deleteAll(paginaRepository.findByLibretaOrderByOrden(libreta));
                libretaRepository.delete(libreta);
            }
        });
        return "redirect:/libretas";
    }
}
