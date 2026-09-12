package com.mplan.controller;

import com.mplan.modelo.Usuario;
import com.mplan.repositorio.UsuarioRepository;
import com.mplan.web.RegistroForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistroController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("registroForm", new RegistroForm());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute RegistroForm registroForm, Model model) {

        if (!registroForm.getPassword().equals(registroForm.getConfirmarPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }

        if (usuarioRepository.existsByUsuario(registroForm.getUsuario())) {
            model.addAttribute("error", "Ese nombre de usuario ya está en uso.");
            return "registro";
        }

        if (usuarioRepository.existsByEmail(registroForm.getEmail())) {
            model.addAttribute("error", "Ese email ya está registrado.");
            return "registro";
        }

        Usuario usuario = new Usuario();
        usuario.setUsuario(registroForm.getUsuario());
        usuario.setEmail(registroForm.getEmail());
        usuario.setNombre(registroForm.getNombre());
        usuario.setApellido(registroForm.getApellido());
        usuario.setPassword(passwordEncoder.encode(registroForm.getPassword()));
        usuario.setRol(Usuario.Rol.USUARIO);

        usuarioRepository.save(usuario);

        return "redirect:/login?registrado";
    }
}
