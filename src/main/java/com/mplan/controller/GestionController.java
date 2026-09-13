package com.mplan.controller;

import com.mplan.modelo.Usuario;
import com.mplan.repositorio.UsuarioRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GestionController {

    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;

    public GestionController(UsuarioRepository usuarioRepository, JavaMailSender mailSender) {
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
    }

    @GetMapping("/gestionar")
    public String gestionar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "gestionar";
    }

    @GetMapping("/gestionar/autorizaciones")
    public String autorizaciones(Model model) {
        model.addAttribute("pendientes", usuarioRepository.findByAprobadoFalse());
        return "autorizaciones";
    }

    @PostMapping("/gestionar/autorizaciones/{id}/autorizar")
    public String autorizar(@PathVariable Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setAprobado(true);
            usuarioRepository.save(usuario);

            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("adm@mpluna.com");
            mensaje.setTo(usuario.getEmail());
            mensaje.setSubject("Registro autorizado - mplan");
            mensaje.setText("Hola " + usuario.getNombre() + ",\n\n"
                    + "Tu registro en mplan ha sido autorizado. Ya puedes iniciar sesión.");
            mailSender.send(mensaje);
        });

        return "redirect:/gestionar/autorizaciones";
    }
}