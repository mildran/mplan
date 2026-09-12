package com.mplan.controller;

import com.mplan.repositorio.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GestionController {

    private final UsuarioRepository usuarioRepository;

    public GestionController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/gestionar")
    public String gestionar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "gestionar";
    }
}