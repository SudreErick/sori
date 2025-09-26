package br.com.fiap.appSori.service;

import br.com.fiap.appSori.domain.CheckInDiario;
import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.dto.request.CheckInDiarioRequestDto;
import br.com.fiap.appSori.domain.dto.response.CheckInDiarioResponseDto;
import br.com.fiap.appSori.domain.dto.response.EstatisticaSentimentoResponseDto;
import br.com.fiap.appSori.domain.enums.SentimentoDiario;
import br.com.fiap.appSori.mapper.CheckInDiarioMapper;
import br.com.fiap.appSori.repository.CheckInDiarioRepository;
import br.com.fiap.appSori.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInDiarioService {

    private final CheckInDiarioRepository checkinDiarioRepository;
    private final CheckInDiarioMapper checkinDiarioMapper;
    private final UsuarioRepository usuarioRepository;

    // --- Lógica de Persistência (POST /api/checkins) ---

    public CheckInDiarioResponseDto registrarCheckin(CheckInDiarioRequestDto requestDto) {
        Usuario usuario = getUsuarioLogado();
        LocalDate hoje = LocalDate.now();

        // 1. Regra de Negócio: Verifica se já existe um check-in para hoje
        checkinDiarioRepository.findByUsuarioAndDataCheckin(usuario, hoje)
                .ifPresent(c -> {
                    throw new RuntimeException("Check-in para hoje já foi registrado. Você pode editar o anterior, mas não criar um novo.");
                });

        // 2. Cria e preenche o domínio
        CheckInDiario checkin = checkinDiarioMapper.toDomain(requestDto);
        checkin.setUsuario(usuario);
        checkin.setDataCheckin(hoje); // Garante que a data é a de hoje

        // 3. Salva
        CheckInDiario checkinSalvo = checkinDiarioRepository.save(checkin);

        // 4. Retorna o DTO de resposta
        return checkinDiarioMapper.toDto(checkinSalvo);
    }

    // --- Lógica de Dashboard (GET /api/checkins/estatisticas) ---

    /**
     * Calcula a frequência e porcentagem dos sentimentos registrados para o Dashboard.
     * @param dias O período (ex: 7, 30, 365) para o cálculo.
     * @return DTO com o Sentimento Mais Frequente e a lista de detalhes.
     */
    public EstatisticaSentimentoResponseDto calcularEstatisticasSentimento(int dias) {
        Usuario usuario = getUsuarioLogado();

        // 1. Busca os últimos check-ins do usuário (ordenados por data)
        List<CheckInDiario> checkins = checkinDiarioRepository.findByUsuarioOrderByDataCheckinDesc(usuario);

        // Aplica o filtro de período
        List<CheckInDiario> checkinsFiltrados = checkins.stream()
                .filter(c -> c.getDataCheckin().isAfter(LocalDate.now().minusDays(dias)))
                .collect(Collectors.toList());

        if (checkinsFiltrados.isEmpty()) {
            return new EstatisticaSentimentoResponseDto();
        }

        // 2. Agrega a contagem de todos os sentimentos no período
        Map<SentimentoDiario, Long> contagemSentimentos = checkinsFiltrados.stream()
                .flatMap(checkin -> {
                    // Força a tipagem explícita aqui para resolver o erro "Cannot resolve method 'stream()'"
                    List<SentimentoDiario> sentimentos = checkin.getSentimentos();
                    return sentimentos.stream();
                })
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        // Contagem total de todos os sentimentos registrados
        long totalRegistros = contagemSentimentos.values().stream().mapToLong(Long::longValue).sum();

        // 3. Mapeia para o DTO de Detalhes e encontra o Sentimento Frequente
        List<EstatisticaSentimentoResponseDto.DetalheSentimento> detalhes = contagemSentimentos.entrySet().stream()
                .map(entry -> {
                    SentimentoDiario sentimento = entry.getKey();
                    long contagem = entry.getValue();
                    double porcentagem = (double) contagem * 100 / totalRegistros;

                    return EstatisticaSentimentoResponseDto.DetalheSentimento.builder()
                            .sentimento(sentimento.getDescricao()) // Acessando o valor string do Enum (requer @Getter no Enum)
                            .contagem((int) contagem)
                            .porcentagem(porcentagem)
                            .build();
                })
                .sorted(Comparator.comparing(EstatisticaSentimentoResponseDto.DetalheSentimento::getContagem).reversed())
                .collect(Collectors.toList());

        // 4. Identifica o sentimento mais frequente
        String sentimentoFrequente = detalhes.stream()
                .findFirst()
                .map(EstatisticaSentimentoResponseDto.DetalheSentimento::getSentimento)
                .orElse("N/A");

        // 5. Retorna o DTO final do Dashboard
        return EstatisticaSentimentoResponseDto.builder()
                .sentimentoFrequente(sentimentoFrequente)
                .detalhes(detalhes)
                .build();
    }

    // --- Método Auxiliar para Obter Usuário Logado ---

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioEmail = authentication.getName();
        return usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

}
