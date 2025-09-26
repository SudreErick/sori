package br.com.fiap.appSori.rest;

import br.com.fiap.appSori.domain.dto.request.RelacionamentoRequestDto;
import br.com.fiap.appSori.domain.dto.response.RelacionamentoResponseDto;
import br.com.fiap.appSori.domain.dto.response.UsuarioResponseDto;
import br.com.fiap.appSori.mapper.RelacionamentoMapper;
import br.com.fiap.appSori.mapper.UsuarioMapper;
import br.com.fiap.appSori.service.RelacionamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/relacionamentos")
@Tag(name = "Relacionamentos", description = "Endpoints para gerenciar os vínculos entre usuários e organizações")
public class RelacionamentoController {
    private final RelacionamentoService relacionamentoService;
    private final RelacionamentoMapper relacionamentoMapper;
    private final UsuarioMapper usuarioMapper;

    @PostMapping
    @Operation(summary = "Cria um novo relacionamento", description = "Vincula um usuário a uma organização com um cargo específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relacionamento criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
            @ApiResponse(responseCode = "404", description = "Usuário ou organização não encontrados."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })
    public ResponseEntity<RelacionamentoResponseDto> criarRelacionamento(@Valid @RequestBody RelacionamentoRequestDto requestDTO) {
        var relacionamentoDomain = relacionamentoMapper.toDomain(requestDTO);
        var relacionamentoSalvo = relacionamentoService.criarRelacionamento(relacionamentoDomain);
        var responseDTO = relacionamentoMapper.toDto(relacionamentoSalvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Lista os usuários de uma organização", description = "Busca todos os usuários vinculados a uma organização específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos."),
            @ApiResponse(responseCode = "404", description = "Organização não encontrada.")
    })
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuariosPorOrganizacao(@RequestParam String organizacaoId) {
        var usuarios = relacionamentoService.buscarUsuariosPorOrganizacao(organizacaoId);
        var responseDTOs = usuarios.stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
}
