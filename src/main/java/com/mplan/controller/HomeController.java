package com.mplan.controller;

import com.mplan.repositorio.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UsuarioRepository usuarioRepository;

    public HomeController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            usuarioRepository.findByUsuario(userDetails.getUsername()).ifPresent(usuario -> {
                String nombre = usuario.getNombre();
                model.addAttribute("nombre", (nombre != null && !nombre.isBlank()) ? nombre : usuario.getUsuario());
            });
        }
        return "index";
    }
}