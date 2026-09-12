package com.mplan.controller;

import com.mplan.modelo.TokenRecuperacion;
import com.mplan.modelo.Usuario;
import com.mplan.repositorio.TokenRecuperacionRepository;
import com.mplan.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class RecuperacionController {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public RecuperacionController(UsuarioRepository usuarioRepository,
                                  TokenRecuperacionRepository tokenRepository,
                                  PasswordEncoder passwordEncoder,
                                  JavaMailSender mailSender) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @GetMapping("/recuperar")
    public String mostrarFormulario() {
        return "recuperar";
    }

    @PostMapping("/recuperar")
    public String procesarSolicitud(@RequestParam String email, Model model) {

        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString();
            TokenRecuperacion tokenRecuperacion = new TokenRecuperacion(
                    token, usuario, LocalDateTime.now().plusHours(1));
            tokenRepository.save(tokenRecuperacion);

            String enlace = baseUrl + "/recuperar/restablecer?token=" + token;

            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("adm@mpluna.com");
            mensaje.setTo(usuario.getEmail());
            mensaje.setSubject("Recuperación de contraseña - mplan");
            mensaje.setText("Hola " + usuario.getUsuario() + ",\n\n"
                    + "Hemos recibido una solicitud para restablecer tu contraseña.\n"
                    + "Pulsa este enlace para crear una nueva (caduca en 1 hora):\n"
                    + enlace + "\n\n"
                    + "Si no has sido tú, ignora este mensaje.");
            mailSender.send(mensaje);

        });

        model.addAttribute("mensaje", "Si el email existe en nuestro sistema, te hemos enviado un enlace para restablecer la contraseña.");
        return "recuperar";
    }

    @GetMapping("/recuperar/restablecer")
    public String mostrarFormularioNuevaPassword(@RequestParam String token, Model model) {

        TokenRecuperacion tokenRecuperacion = tokenRepository.findByToken(token).orElse(null);

        if (tokenRecuperacion == null || tokenRecuperacion.estaExpirado()) {
            model.addAttribute("error", "El enlace no es válido o ha caducado.");
            return "recuperar";
        }

        model.addAttribute("token", token);
        return "restablecer";
    }

    @PostMapping("/recuperar/restablecer")
    public String procesarNuevaPassword(@RequestParam String token,
                                        @RequestParam String password,
                                        @RequestParam String confirmarPassword,
                                        Model model) {

        TokenRecuperacion tokenRecuperacion = tokenRepository.findByToken(token).orElse(null);

        if (tokenRecuperacion == null || tokenRecuperacion.estaExpirado()) {
            model.addAttribute("error", "El enlace no es válido o ha caducado.");
            return "recuperar";
        }

        if (!password.equals(confirmarPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            model.addAttribute("token", token);
            return "restablecer";
        }

        Usuario usuario = tokenRecuperacion.getUsuario();
        usuario.setPassword(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);

        tokenRepository.delete(tokenRecuperacion);

        return "redirect:/login?restablecida";
    }
}