package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.service.ColecaoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "colecoes")
public class ColecaoController {
    private final ColecaoService colecaoService;

    public ColecaoController(ColecaoService colecaoService) {
        this.colecaoService = colecaoService;
    }


}
