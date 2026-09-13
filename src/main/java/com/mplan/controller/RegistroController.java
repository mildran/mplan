package com.mplan.controller;

import com.mplan.modelo.Usuario;
import com.mplan.repositorio.UsuarioRepository;
import com.mplan.web.RegistroForm;
import jakarta.validation.Valid;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public RegistroController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("registroForm", new RegistroForm());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute RegistroForm registroForm,
                                   BindingResult bindingResult,
                                   Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Todos los campos son obligatorios y el email debe ser válido.");
            return "registro";
        }

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
        usuario.setAprobado(false);

        usuarioRepository.save(usuario);

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("adm@mpluna.com");
        mensaje.setTo(usuario.getEmail());
        mensaje.setSubject("Registro recibido - mplan");
        mensaje.setText("Hola " + usuario.getNombre() + ",\n\n"
                + "Hemos recibido tu registro en mplan. \nTu cuenta está pendiente de autorización " +
                "\n(mira tu correo para saber cuando se te ha autorizado)"
                + "por un administrador");
        mailSender.send(mensaje);

        return "redirect:/login?registrado";
    }
}