package com.mplan.controller;

import com.mplan.modelo.Usuario;
import com.mplan.repositorio.UsuarioRepository;
import com.mplan.web.PerfilForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByUsuario(userDetails.getUsername()).orElseThrow();

        PerfilForm perfilForm = new PerfilForm();
        perfilForm.setEmail(usuario.getEmail());
        perfilForm.setNombre(usuario.getNombre());
        perfilForm.setApellido(usuario.getApellido());

        model.addAttribute("perfilForm", perfilForm);
        model.addAttribute("usuarioLogin", usuario.getUsuario());
        return "perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@AuthenticationPrincipal UserDetails userDetails,
                                   @Valid @ModelAttribute PerfilForm perfilForm,
                                   BindingResult bindingResult,
                                   Model model) {

        Usuario usuario = usuarioRepository.findByUsuario(userDetails.getUsername()).orElseThrow();
        model.addAttribute("usuarioLogin", usuario.getUsuario());

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Todos los campos son obligatorios y el email debe ser válido.");
            return "perfil";
        }

        if (!usuario.getEmail().equals(perfilForm.getEmail())
                && usuarioRepository.existsByEmail(perfilForm.getEmail())) {
            model.addAttribute("error", "Ese email ya está en uso.");
            return "perfil";
        }

        String nuevaPassword = perfilForm.getNuevaPassword();
        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            if (!nuevaPassword.equals(perfilForm.getConfirmarNuevaPassword())) {
                model.addAttribute("error", "Las contraseñas nuevas no coinciden.");
                return "perfil";
            }
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        }

        usuario.setEmail(perfilForm.getEmail());
        usuario.setNombre(perfilForm.getNombre());
        usuario.setApellido(perfilForm.getApellido());
        usuarioRepository.save(usuario);

        model.addAttribute("mensaje", "Datos actualizados correctamente.");
        return "perfil";
    }

    @PostMapping("/perfil/borrar")
    public String borrarCuenta(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) throws Exception {
        usuarioRepository.findByUsuario(userDetails.getUsername()).ifPresent(usuarioRepository::delete);
        request.logout();
        return "redirect:/login?cuentaEliminada";
    }
}
