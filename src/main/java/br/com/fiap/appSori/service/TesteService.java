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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TesteService {
    private final TesteRepository testeRepository;
    private final TesteMapper testeMapper;

    // Dependências necessárias para a lógica de filtragem de usuário
    private final UsuarioService usuarioService;
    private final TentativaService tentativaService;


    /**
     * Cria um novo Teste. (Endpoint POST /api/testes - Requer ADMIN)
     * O campo 'ativo' será definido como 'false' (rascunho) na entidade de domínio
     * se não for explicitamente enviado no DTO ou se o construtor for usado.
     * @param dto O DTO de requisição do Teste.
     * @return O DTO de resposta do Teste criado.
     */
    public TesteResponseDto criarTeste(TesteRequestDto dto) {
        Teste teste = testeMapper.toDomain(dto);
        // Garante que novos testes são criados como inativos/rascunho por padrão,
        // a menos que o mapeamento do DTO já cuide disso.
        // teste.setAtivo(false);

        Teste testeSalvo = testeRepository.save(teste);
        return testeMapper.toDto(testeSalvo);
    }

    /**
     * Busca todos os testes cadastrados. (Usado pela rota ADMIN /admin/todos)
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
     * @param id O ID do teste
     * @return TesteResponseDto
     */
    public TesteResponseDto buscarPorId(String id) {
        Teste teste = testeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teste não encontrado."));
        return testeMapper.toDto(teste);
    }

    // --- MÉTODOS PARA O CLIENTE (FILTRAGEM) ---

    /**
     * Busca testes que o usuário logado AINDA PODE REALIZAR.
     * Filtra por: 1) Teste ATIVO/PUBLICADO e 2) Testes NÃO COMPLETADOS pelo usuário.
     * @param usuarioEmail O email do usuário logado.
     * @return Lista de TesteResponseDto.
     */
    public List<TesteResponseDto> buscarTestesDisponiveis(String usuarioEmail) {

        // 1. Buscar IDs dos testes que o usuário JÁ realizou
        Set<String> idsTestesRealizados = tentativaService.buscarIdsTestesRealizadosPor(usuarioEmail);

        // 2. Buscar todos os testes ATIVOS/PUBLICADOS no repositório.
        // CORREÇÃO: Usa findByAtivo(true) para obter apenas testes prontos.
        List<Teste> testesAtivos = testeRepository.findByAtivo(true);

        // 3. Filtrar os ativos que NÃO estão na lista de realizados
        List<Teste> disponiveis = testesAtivos.stream()
                .filter(teste -> !idsTestesRealizados.contains(teste.getId()))
                .collect(Collectors.toList());

        return disponiveis.stream()
                .map(testeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca testes que o usuário logado JÁ REALIZOU.
     * @param usuarioEmail O email do usuário logado.
     * @return Lista de TesteResponseDto.
     */
    public List<TesteResponseDto> buscarTestesRealizados(String usuarioEmail) {

        // 1. Busca todas as entidades Teste ligadas a tentativas CONCLUÍDAS do usuário.
        List<Teste> testesRealizados = tentativaService.buscarTestesRealizadosPor(usuarioEmail);

        return testesRealizados.stream()
                .map(testeMapper::toDto)
                .collect(Collectors.toList());
    }


    // --- Operação DELETE (ADMIN) ---

    public void excluirTeste(String id) {
        if (!testeRepository.existsById(id)) {
            throw new RuntimeException("Teste com ID " + id + " não encontrado.");
        }

        // RECOMENDAÇÃO: Implemente aqui uma verificação (via TentativaService)
        // para impedir a exclusão de um teste que já possui tentativas concluídas,
        // a menos que você queira forçar a exclusão em cascata (o que é arriscado).

        testeRepository.deleteById(id);
    }
}
