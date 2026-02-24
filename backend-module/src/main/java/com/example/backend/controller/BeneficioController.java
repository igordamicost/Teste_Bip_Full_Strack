package com.example.backend.controller;

import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.entity.Beneficio;
import com.example.backend.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Benefícios", description = "CRUD e transferência entre benefícios")
@SecurityRequirement(name = "bearerAuth")
public class BeneficioController {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar ativos")
    public List<BeneficioDto> listar() {
        return service.listar().stream().map(BeneficioDto::from).toList();
    }

    @GetMapping("/all")
    @Operation(summary = "Listar todos")
    public List<BeneficioDto> listarTodos() {
        return service.listarTodos().stream().map(BeneficioDto::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID")
    public ResponseEntity<BeneficioDto> buscar(@PathVariable Long id) {
        Beneficio b = service.buscarPorId(id);
        return ResponseEntity.ok(BeneficioDto.from(b));
    }

    @PostMapping
    @Operation(summary = "Criar benefício")
    public ResponseEntity<BeneficioDto> criar(@Valid @RequestBody BeneficioDto dto) {
        Beneficio b = service.criar(dto.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(BeneficioDto.from(b));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício")
    public ResponseEntity<BeneficioDto> atualizar(@PathVariable Long id, @Valid @RequestBody BeneficioDto dto) {
        Beneficio b = service.atualizar(id, dto.toEntity());
        return ResponseEntity.ok(BeneficioDto.from(b));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir benefício")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transferir valor entre benefícios")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transferir(@Valid @RequestBody TransferenciaRequest request) {
        service.transferir(request.fromId(), request.toId(), request.amount());
    }
}
