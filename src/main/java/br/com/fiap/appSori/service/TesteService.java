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
        // NOTA: Se o 'PerguntaRequestDto' não tiver um ID, o mapper
        // DEVE ser configurado para gerar um UUID aqui e atribuí-lo à entidade 'Pergunta'.
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
     * Este método é crucial para o Frontend carregar o Teste completo, incluindo os IDs das perguntas.
     * @param id O ID do teste
     * @return TesteResponseDto
     */
    public TesteResponseDto buscarPorId(String id) {
        // 1. Busca a entidade Teste pelo ID
        Teste teste = testeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teste não encontrado."));

        // 2. Mapeia a entidade para o DTO de resposta
        // Este DTO (TesteResponseDto) DEVE incluir o ID de cada pergunta.
        return testeMapper.toDto(teste);
    }

    public void excluirTeste(String id) {
        // 1. Verifica se o Teste existe para retornar 404 se não for encontrado
        if (!testeRepository.existsById(id)) {
            // Lança uma RuntimeException que será capturada no Controller e retornará 404
            throw new RuntimeException("Teste com ID " + id + " não encontrado.");
        }

        // 2. Exclui o teste
        testeRepository.deleteById(id);

        // IMPORTANTE: Em um sistema real, você também precisaria
        // verificar se há tentativas ou resultados ativos referenciando este teste
        // e impedir a exclusão ou limpar esses dados.
    }
}
