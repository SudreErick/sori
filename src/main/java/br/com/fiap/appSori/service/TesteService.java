package br.com.fiap.appSori.service;

import br.com.fiap.appSori.domain.Teste;
import br.com.fiap.appSori.domain.dto.request.TesteRequestDto;
import br.com.fiap.appSori.domain.dto.response.TesteResponseDto;
import br.com.fiap.appSori.domain.enums.TipoTeste;
import br.com.fiap.appSori.mapper.TesteMapper;
import br.com.fiap.appSori.repository.TesteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TesteService {
    private final TesteRepository testeRepository;
    private final TesteMapper testeMapper;

    /**
     * Cria um novo Teste.
     * @param dto O DTO de requisição do Teste.
     * @return O DTO de resposta do Teste criado.
     */
    public TesteResponseDto criarTeste(TesteRequestDto dto) {
        // Mapeia o DTO para a entidade de domínio
        Teste teste = testeMapper.toDomain(dto);

        // Salva no repositório
        Teste testeSalvo = testeRepository.save(teste);

        // Mapeia a entidade salva para o DTO de resposta
        return testeMapper.toDto(testeSalvo);
    }

    /**
     * Busca todos os testes disponíveis.
     * @return Lista de TesteResponseDto.
     */
    public List<TesteResponseDto> buscarTodos() {
        List<Teste> testes = testeRepository.findAll();

        return testes.stream()
                .map(testeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca um teste pelo ID e lança exceção se não for encontrado.
     * Este método foi adicionado para suportar a rota GET /api/testes/{id}.
     * * @param id O ID do teste
     * @return TesteResponseDto
     */
    public TesteResponseDto buscarPorId(String id) {
        // 1. Busca a entidade Teste pelo ID
        Teste teste = testeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teste não encontrado."));

        // 2. Mapeia a entidade para o DTO de resposta
        return testeMapper.toDto(teste);
    }

    /*
     * Nota: O método de filtro (buscarPorFiltro) mencionado no Controller
     * não está implementado aqui, mas você adicionaria ele assim:
     * * public List<TesteResponseDto> buscarPorFiltro(String filtro) {
     * // Ex: return testeRepository.findByTituloContainingIgnoreCase(filtro)
     * // .stream().map(testeMapper::toDto).collect(Collectors.toList());
     * }
     */
}
