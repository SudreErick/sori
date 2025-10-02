package br.com.fiap.appSori.service;

import br.com.fiap.appSori.domain.Pergunta;
import br.com.fiap.appSori.domain.Tentativa;
import br.com.fiap.appSori.domain.Teste;
import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.dto.request.RespostaUsuarioRequestDto;
import br.com.fiap.appSori.domain.dto.request.ResultadoRequestDto;
import br.com.fiap.appSori.domain.dto.request.TentativaRequestDto;
import br.com.fiap.appSori.domain.dto.response.TentativaResponseDto;
import br.com.fiap.appSori.domain.enums.StatusTentativa;
import br.com.fiap.appSori.mapper.TentativaMapper;
import br.com.fiap.appSori.repository.TentativaRepository;
import br.com.fiap.appSori.repository.TesteRepository;
import br.com.fiap.appSori.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TentativaService {
    private final TentativaRepository tentativaRepository;
    private final TesteRepository testeRepository;
    private final UsuarioRepository usuarioRepository;
    private final TentativaMapper tentativaMapper;
    private final ResultadoService resultadoService; // Injeção para processar o resultado

    /**
     * Inicia um novo teste, salva o progresso ou conclui uma tentativa.
     * @param requestDto O DTO contendo o ID do teste e as respostas atuais.
     * @return O DTO da Tentativa atualizada ou criada.
     */
    public TentativaResponseDto iniciarOuAtualizarTentativa(TentativaRequestDto requestDto) {
        // 1. Obter o usuário logado
        Usuario usuario = getUsuarioLogado();

        // 2. Tentar encontrar uma tentativa em andamento
        Optional<Tentativa> tentativaEmAndamento = buscarTentativaEmAndamento(usuario, requestDto.getTesteId());

        Tentativa tentativa;

        if (tentativaEmAndamento.isPresent()) {
            // Se encontrou, usa a existente
            tentativa = tentativaEmAndamento.get();
        } else if (requestDto.getTesteId() != null) {
            // Se não encontrou e o ID do teste foi fornecido, cria uma nova
            tentativa = criarNovaTentativa(usuario, requestDto.getTesteId());
        } else {
            // Se não encontrou e o ID do teste não foi fornecido (erro de request)
            throw new RuntimeException("Tentativa não encontrada e o ID do Teste não foi fornecido para iniciar uma nova.");
        }

        // 3. Processar as respostas e status
        if (!requestDto.getRespostas().isEmpty()) {
            // Mapear DTOs de RespostaBruta para entidades e salvar
            tentativa.setRespostas(tentativaMapper.toDomainList(requestDto.getRespostas()));
            tentativa.setStatus(StatusTentativa.EM_ANDAMENTO);
            tentativa.setDataAtualizacao(ZonedDateTime.now());
        }

        // 4. Se o usuário sinalizou para concluir, processa o resultado final
        if (requestDto.isConcluir()) {
            return concluirTentativa(tentativa);
        }

        // 5. Salva e retorna o DTO de resposta
        Tentativa tentativaSalva = tentativaRepository.save(tentativa);
        return tentativaMapper.toDto(tentativaSalva);
    }

    /**
     * Conclui a tentativa, aciona o serviço de resultado e salva o status final.
     * **REFATORADO**: Busca o Teste para obter o texto da Pergunta e da Opção.
     */
    private TentativaResponseDto concluirTentativa(Tentativa tentativa) {
        // ESSENCIAL: Buscar o Teste completo para mapear os textos das perguntas
        Teste testeCompleto = testeRepository.findById(tentativa.getTeste().getId())
                .orElseThrow(() -> new RuntimeException("Teste referenciado não encontrado. Impossível concluir."));

        // 1. Converte as respostas brutas da Tentativa para o formato de Request do Resultado
        List<RespostaUsuarioRequestDto> respostasResultado = tentativa.getRespostas().stream()
                .map(resposta -> {
                    // Localiza a pergunta correspondente no Teste usando o ID da RespostaBruta
                    Pergunta pergunta = testeCompleto.getPerguntas().stream()
                            .filter(p -> p.getId().equals(resposta.getPerguntaId())) // Requer que Pergunta tenha um ID!
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Pergunta ID " + resposta.getPerguntaId() + " não encontrada no Teste."));

                    // Localiza o texto da opção selecionada
                    String respostaTexto = pergunta.getOpcoes().stream()
                            .filter(o -> o.getValor() == resposta.getValorAtribuido())
                            .map(o -> o.getTexto())
                            .findFirst()
                            // Se a RespostaBruta tiver o campo 'respostaSelecionada', você pode usar ele aqui
                            // Caso contrário, usamos a busca acima
                            .orElse("Valor da Resposta não encontrado");

                    RespostaUsuarioRequestDto dto = new RespostaUsuarioRequestDto();

                    // Dados essenciais para o cálculo
                    dto.setPerguntaId(resposta.getPerguntaId());
                    dto.setValorAtribuido(resposta.getValorAtribuido());

                    // CAMPOS CORRIGIDOS: Preenchidos com a busca no Teste (Agora não são mais null)
                    dto.setPerguntaTexto(pergunta.getTexto());
                    dto.setRespostaSelecionada(respostaTexto);

                    return dto;
                })
                .collect(Collectors.toList());

        // 2. Cria o DTO do Resultado para enviar ao ResultadoService
        ResultadoRequestDto resultadoRequest = new ResultadoRequestDto();
        resultadoRequest.setTesteId(testeCompleto.getId());
        resultadoRequest.setRespostas(respostasResultado);

        // 3. Salva o resultado processado (isso dispara o cálculo de risco)
        resultadoService.salvarResultado(resultadoRequest);

        // 4. Atualiza o status da tentativa para CONCLUIDA
        tentativa.setStatus(StatusTentativa.CONCLUIDA);
        tentativa.setDataAtualizacao(ZonedDateTime.now());
        Tentativa tentativaConcluida = tentativaRepository.save(tentativa);

        // 5. Retorna o DTO da Tentativa concluída
        return tentativaMapper.toDto(tentativaConcluida);
    }

    // --- Métodos de Busca (GET) ---

    /**
     * Busca o histórico completo de tentativas do usuário logado.
     * @return Lista de TentativaResponseDto
     */
    public List<TentativaResponseDto> buscarTodasTentativasDoUsuario() {
        Usuario usuario = getUsuarioLogado();

        List<Tentativa> tentativas = tentativaRepository.findByUsuario(usuario);

        return tentativas.stream()
                .map(tentativaMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma tentativa específica por ID, garantindo que pertença ao usuário logado.
     * @param id O ID da Tentativa
     * @return TentativaResponseDto
     */
    public TentativaResponseDto buscarTentativaPorId(String id) {
        Usuario usuario = getUsuarioLogado();

        Tentativa tentativa = tentativaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tentativa não encontrada."));

        // Verificação de Segurança: Garante que a tentativa pertence ao usuário.
        if (!tentativa.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: A tentativa não pertence ao usuário logado.");
        }

        return tentativaMapper.toDto(tentativa);
    }

    // NOVO MÉTODO: Acesso Global para ADMIN
    /**
     * ADMIN: Busca o histórico completo de tentativas de TODOS os usuários.
     * @return Lista de TentativaResponseDto
     */
    public List<TentativaResponseDto> buscarTodasTentativasGlobais() {
        // Usa o findAll() do repositório para ignorar o filtro de usuário.
        List<Tentativa> todasTentativas = tentativaRepository.findAll();

        return todasTentativas.stream()
                .map(tentativaMapper::toDto)
                .collect(Collectors.toList());
    }

    // --- Métodos Auxiliares ---

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioEmail = authentication.getName();
        // Assume-se que o UsuarioRepository.findByEmail já normaliza para lowercase.
        return usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    private Tentativa criarNovaTentativa(Usuario usuario, String testeId) {
        // 1. Verifica se já existe uma tentativa EM_ANDAMENTO ou INICIADA
        List<StatusTentativa> statusAtivos = List.of(StatusTentativa.INICIADA, StatusTentativa.EM_ANDAMENTO);
        List<Tentativa> ativas = tentativaRepository.findByUsuarioAndTeste_IdAndStatusIn(usuario, testeId, statusAtivos);

        if (!ativas.isEmpty()) {
            throw new RuntimeException("Já existe uma tentativa ativa para este teste. Continue a anterior ou marque-a como concluída.");
        }

        // 2. Cria a nova entidade
        Tentativa novaTentativa = new Tentativa();
        novaTentativa.setUsuario(usuario);
        // Garante que o Teste é carregado no BE para futura referência
        novaTentativa.setTeste(testeRepository.findById(testeId)
                .orElseThrow(() -> new RuntimeException("Teste não encontrado.")));
        novaTentativa.setStatus(StatusTentativa.INICIADA);
        novaTentativa.setDataInicio(ZonedDateTime.now());
        novaTentativa.setDataAtualizacao(ZonedDateTime.now());

        return novaTentativa;
    }

    private Optional<Tentativa> buscarTentativaEmAndamento(Usuario usuario, String testeId) {
        if (testeId == null) return Optional.empty();

        List<StatusTentativa> statusAtivos = List.of(StatusTentativa.INICIADA, StatusTentativa.EM_ANDAMENTO);
        List<Tentativa> ativas = tentativaRepository.findByUsuarioAndTeste_IdAndStatusIn(usuario, testeId, statusAtivos);

        // Retorna a primeira (e única, se a regra for respeitada) tentativa ativa encontrada
        return ativas.isEmpty() ? Optional.empty() : Optional.of(ativas.get(0));
    }
}
