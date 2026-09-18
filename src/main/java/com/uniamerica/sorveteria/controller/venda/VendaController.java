package com.uniamerica.sorveteria.controller.venda;

import com.uniamerica.sorveteria.controller.venda.dto.VendaRequest;
import com.uniamerica.sorveteria.controller.venda.dto.VendaResponse;
import com.uniamerica.sorveteria.entity.StatusVenda;
import com.uniamerica.sorveteria.entity.Venda;
import com.uniamerica.sorveteria.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;


    @PostMapping
    public ResponseEntity<VendaResponse> registrar(@Valid @RequestBody VendaRequest request) {
        Venda salva = vendaService.registrar(request);
        log.info("Venda {} realiada com sucesso", salva.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(VendaResponse.fromEntity(salva));
    }

    @GetMapping
    public ResponseEntity<List<VendaResponse>> listarTodas() {
        List<VendaResponse> lista = vendaService.listar().stream()
                .map(VendaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponse> buscarPorId(@PathVariable Long id) {
        Venda venda = vendaService.buscarPorId(id);
        return ResponseEntity.ok(VendaResponse.fromEntity(venda));
    }

    @GetMapping("/busca")
    public ResponseEntity<List<VendaResponse>> buscar(@RequestParam(required = false) Long produtoId, @RequestParam(required = false) StatusVenda status) {
        List<VendaResponse> lista = vendaService.buscar(produtoId, status).stream()
                .map(VendaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<VendaResponse> cancelar(@PathVariable Long id) {
        Venda venda = vendaService.cancelar(id);
        return ResponseEntity.ok(VendaResponse.fromEntity(venda));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        vendaService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
