package com.mplan.controller;

import com.mplan.modelo.Libreta;
import com.mplan.modelo.Pagina;
import com.mplan.repositorio.LibretaRepository;
import com.mplan.repositorio.PaginaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class LibretaVerController {

    private final LibretaRepository libretaRepository;
    private final PaginaRepository paginaRepository;

    public LibretaVerController(LibretaRepository libretaRepository, PaginaRepository paginaRepository) {
        this.libretaRepository = libretaRepository;
        this.paginaRepository = paginaRepository;
    }

    @GetMapping("/libretas/{id}")
    public String ver(@PathVariable Long id,
                       @RequestParam(defaultValue = "0") int pagina,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {

        Libreta libreta = libretaRepository.findById(id).orElseThrow();
        if (!esPropietario(libreta, userDetails)) {
            return "redirect:/libretas";
        }

        List<Pagina> paginas = paginaRepository.findByLibretaOrderByOrden(libreta);
        int total = paginas.size();

        model.addAttribute("libreta", libreta);
        model.addAttribute("totalPaginas", total);

        if (pagina <= 0 || total == 0) {
            model.addAttribute("esPortada", true);
        } else {
            int ordenActual = Math.min(pagina, total);
            Pagina paginaActual = paginas.get(ordenActual - 1);
            model.addAttribute("esPortada", false);
            model.addAttribute("ordenActual", ordenActual);
            model.addAttribute("paginaActual", paginaActual);
        }

        return "libreta-ver";
    }

    @PostMapping("/libretas/{id}/paginas/nueva")
    public String nuevaDesdeCubierta(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails) {

        Libreta libreta = libretaRepository.findById(id).orElseThrow();
        if (!esPropietario(libreta, userDetails)) {
            return "redirect:/libretas";
        }

        insertarPaginaDespuesDe(libreta, 0);

        return "redirect:/libretas/" + id + "?pagina=1";
    }

    @PostMapping("/libretas/{id}/paginas/{paginaId}/guardar")
    public String guardar(@PathVariable Long id,
                           @PathVariable Long paginaId,
                           @RequestParam String contenido,
                           @RequestParam String accion,
                           @RequestParam(required = false) Integer paginaDestino,
                           @AuthenticationPrincipal UserDetails userDetails) {

        Libreta libreta = libretaRepository.findById(id).orElseThrow();
        if (!esPropietario(libreta, userDetails)) {
            return "redirect:/libretas";
        }

        Pagina pagina = paginaRepository.findById(paginaId).orElseThrow();
        pagina.setContenido(contenido);
        paginaRepository.save(pagina);

        int destino;
        if ("anterior".equals(accion)) {
            destino = pagina.getOrden() - 1;
        } else if ("nueva".equals(accion)) {
            insertarPaginaDespuesDe(libreta, pagina.getOrden());
            destino = pagina.getOrden() + 1;
        } else if ("saltar".equals(accion)) {
            destino = paginaDestino != null ? paginaDestino : pagina.getOrden();
        } else {
            destino = pagina.getOrden() + 1;
        }

        return "redirect:/libretas/" + id + "?pagina=" + destino;
    }

    @PostMapping("/libretas/{id}/paginas/{paginaId}/borrar")
    public String borrarPagina(@PathVariable Long id,
                                @PathVariable Long paginaId,
                                @AuthenticationPrincipal UserDetails userDetails) {

        Libreta libreta = libretaRepository.findById(id).orElseThrow();
        if (!esPropietario(libreta, userDetails)) {
            return "redirect:/libretas";
        }

        Pagina pagina = paginaRepository.findById(paginaId).orElseThrow();
        int ordenBorrada = pagina.getOrden();

        paginaRepository.delete(pagina);

        List<Pagina> restantes = paginaRepository.findByLibretaOrderByOrden(libreta);
        for (Pagina p : restantes) {
            if (p.getOrden() > ordenBorrada) {
                p.setOrden(p.getOrden() - 1);
            }
        }
        paginaRepository.saveAll(restantes);

        int destino = ordenBorrada - 1;
        return destino <= 0
                ? "redirect:/libretas/" + id
                : "redirect:/libretas/" + id + "?pagina=" + destino;
    }

    @PostMapping("/libretas/{id}/paginas/{paginaId}/autoguardar")
    @ResponseBody
    public ResponseEntity<Void> autoguardar(@PathVariable Long id,
                                             @PathVariable Long paginaId,
                                             @RequestParam String contenido,
                                             @AuthenticationPrincipal UserDetails userDetails) {

        Libreta libreta = libretaRepository.findById(id).orElseThrow();
        if (!esPropietario(libreta, userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Pagina pagina = paginaRepository.findById(paginaId).orElseThrow();
        pagina.setContenido(contenido);
        paginaRepository.save(pagina);

        return ResponseEntity.ok().build();
    }

    private void insertarPaginaDespuesDe(Libreta libreta, int ordenActual) {
        List<Pagina> paginas = paginaRepository.findByLibretaOrderByOrden(libreta);
        for (Pagina p : paginas) {
            if (p.getOrden() > ordenActual) {
                p.setOrden(p.getOrden() + 1);
            }
        }
        paginaRepository.saveAll(paginas);
        paginaRepository.save(new Pagina(libreta, ordenActual + 1, "<p><br></p>"));
    }

    private boolean esPropietario(Libreta libreta, UserDetails userDetails) {
        return libreta.getUsuario().getUsuario().equals(userDetails.getUsername());
    }
}
