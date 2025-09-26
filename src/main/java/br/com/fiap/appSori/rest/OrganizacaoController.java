package br.com.fiap.appSori.rest;

import br.com.fiap.appSori.domain.dto.request.OrganizacaoRequestDto;
import br.com.fiap.appSori.domain.dto.response.OrganizacaoResponseDto;
import br.com.fiap.appSori.mapper.OrganizacaoMapper;
import br.com.fiap.appSori.service.OrganizacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizacoes")
@Tag(name = "Organizações", description = "Endpoints para gerenciar as organizações")
public class OrganizacaoController {
    private final OrganizacaoService organizacaoService;
    private final OrganizacaoMapper organizacaoMapper;

    public OrganizacaoController(OrganizacaoService organizacaoService, OrganizacaoMapper organizacaoMapper) {
        this.organizacaoService = organizacaoService;
        this.organizacaoMapper = organizacaoMapper;
    }

    @PostMapping
    @Operation(summary = "Cria uma nova organização", description = "Endpoint para registrar uma nova organização na plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organização criada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    public ResponseEntity<OrganizacaoResponseDto> criarOrganizacao(@Valid @RequestBody OrganizacaoRequestDto requestDTO) {
        var organizacaoDomain = organizacaoMapper.toDomain(requestDTO);
        var organizacaoSalva = organizacaoService.criarOrganizacao(organizacaoDomain);
        var responseDTO = organizacaoMapper.toDto(organizacaoSalva);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Lista todas as organizações", description = "Retorna uma lista de todas as organizações cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de organizações retornada com sucesso.")
    })
    public ResponseEntity<List<OrganizacaoResponseDto>> listarOrganizacoes() {
        var organizacoes = organizacaoService.buscarTodas();
        var responseDTOs = organizacoes.stream()
                .map(organizacaoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma organização por ID", description = "Retorna uma organização específica com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organização encontrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Organização não encontrada.")
    })
    public ResponseEntity<OrganizacaoResponseDto> buscarOrganizacaoPorId(@PathVariable String id) {
        return organizacaoService.buscarPorId(id)
                .map(organizacaoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
