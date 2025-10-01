package br.com.fiap.appSori.service;

import br.com.fiap.appSori.domain.Resultado;
import br.com.fiap.appSori.domain.Teste;
import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.dto.request.RespostaUsuarioRequestDto;
import br.com.fiap.appSori.domain.dto.request.ResultadoRequestDto;
import br.com.fiap.appSori.domain.dto.response.ResultadoResponseDto;
import br.com.fiap.appSori.domain.enums.TipoTeste;
import br.com.fiap.appSori.mapper.ResultadoMapper;
import br.com.fiap.appSori.repository.ResultadoRepository;
import br.com.fiap.appSori.repository.TesteRepository;
import br.com.fiap.appSori.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultadoService {
    private final ResultadoRepository resultadoRepository;
    private final TesteRepository testeRepository;
    private final ResultadoMapper resultadoMapper;
    private final UsuarioRepository userRepository;

    public ResultadoResponseDto salvarResultado(ResultadoRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioEmail = authentication.getName();
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(usuarioEmail);

        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        Usuario usuario = usuarioOptional.get();

        Optional<Teste> testeOptional = testeRepository.findById(requestDto.getTesteId());
        if (testeOptional.isEmpty()) {
            throw new RuntimeException("Teste não encontrado.");
        }
        Teste teste = testeOptional.get();

        int pontuacaoTotal = requestDto.getRespostas().stream()
                .mapToInt(RespostaUsuarioRequestDto::getValorAtribuido)
                .sum();

        String nivelRisco = calcularNivelRisco(teste.getTipo(), requestDto.getRespostas());

        Resultado resultado = resultadoMapper.toDomain(requestDto);
        resultado.setTeste(teste);
        resultado.setUsuario(usuario);
        resultado.setPontuacaoTotal(pontuacaoTotal);
        resultado.setNivelRisco(nivelRisco);
        resultado.setDataRealizacao(ZonedDateTime.now());

        Resultado resultadoSalvo = resultadoRepository.save(resultado);

        return resultadoMapper.toDto(resultadoSalvo);
    }

    private String calcularNivelRisco(TipoTeste tipo, List<RespostaUsuarioRequestDto> respostas) {
        long contagemAltoRisco = respostas.stream()
                .filter(r -> r.getValorAtribuido() >= 2)
                .count();

        switch (tipo) {
            case ANSIEDADE:
                if (contagemAltoRisco >= 4) return "Alto";
                if (contagemAltoRisco >= 2) return "Moderado";
                return "Baixo";
            case DEPRESSAO:
                if (contagemAltoRisco >= 3) return "Alto";
                if (contagemAltoRisco >= 1) return "Moderado";
                return "Baixo";
            case BURNOUT:
                if (contagemAltoRisco >= 5) return "Alto";
                if (contagemAltoRisco >= 3) return "Moderado";
                return "Baixo";
            case GESTAO_DE_ESTRESSE:
                if (contagemAltoRisco >= 4) return "Baixo";
                if (contagemAltoRisco >= 2) return "Moderado";
                return "Alto";
            default:
                return "Não classificado";
        }
    }

    public List<ResultadoResponseDto> buscarResultadosPorUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioEmail = authentication.getName();
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(usuarioEmail);

        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        Usuario usuario = usuarioOptional.get();

        List<Resultado> resultados = resultadoRepository.findByUsuario(usuario);
        return resultados.stream().map(resultadoMapper::toDto).collect(Collectors.toList());
    }

    public List<ResultadoResponseDto> buscarResultadosPorUsuarioETipo(String tipo) {
        System.out.println("Valor do parâmetro 'tipo' recebido: '" + tipo + "'");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioEmail = authentication.getName();

        System.out.println("E-mail do usuário no contexto de segurança: '" + usuarioEmail + "'");

        Optional<Usuario> usuarioOptional = userRepository.findByEmail(usuarioEmail.toLowerCase());

        if (usuarioOptional.isEmpty()) {
            System.out.println("DEBUG: Usuário com e-mail '" + usuarioEmail + "' não foi encontrado. Lançando 404.");
            throw new RuntimeException("Usuário não encontrado.");
        }
        Usuario usuario = usuarioOptional.get();

        TipoTeste tipoTeste;
        try {
            tipoTeste = TipoTeste.valueOf(tipo.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return List.of();
        }

        List<Resultado> resultados = resultadoRepository.findByUsuarioAndTeste_Tipo(usuario, tipoTeste);
        return resultados.stream().map(resultadoMapper::toDto).collect(Collectors.toList());
    }

    // NOVO MÉTODO: Acesso Global para ADMIN
    /**
     * Busca TODOS os resultados de todos os usuários.
     * Este método será chamado pelo controller protegido por hasRole('ADMIN').
     */
    public List<ResultadoResponseDto> buscarTodosResultadosGlobais() {
        // Usa o método findAll() do repositório para ignorar qualquer filtro de usuário.
        List<Resultado> resultados = resultadoRepository.findAll();

        // Mapeia a lista de domínios para a lista de DTOs de resposta
        return resultados.stream()
                .map(resultadoMapper::toDto)
                .collect(Collectors.toList());
    }
}
