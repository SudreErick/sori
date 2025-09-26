package br.com.fiap.appSori.service;

import br.com.fiap.appSori.domain.Relacionamento;
import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.repository.RelacionamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelacionamentoService {
    private final RelacionamentoRepository relacionamentoRepository;
    private final UsuarioService usuarioService;
    private final OrganizacaoService organizacaoService;

    public Relacionamento criarRelacionamento(Relacionamento relacionamento) {
        // Validação de negócio: garantir que o usuário e a organização existem
        if (usuarioService.buscarPorId(relacionamento.getUsuarioId()).isEmpty()) {
            throw new IllegalArgumentException("Usuário com ID " + relacionamento.getUsuarioId() + " não encontrado.");
        }
        if (organizacaoService.buscarPorId(relacionamento.getOrganizacaoId()).isEmpty()) {
            throw new IllegalArgumentException("Organização com ID " + relacionamento.getOrganizacaoId() + " não encontrada.");
        }

        return relacionamentoRepository.save(relacionamento);
    }

    public Optional<Relacionamento> buscarPorId(String id) {
        return relacionamentoRepository.findById(id);
    }

    public List<Relacionamento> buscarTodos() {
        return relacionamentoRepository.findAll();
    }

    public List<Relacionamento> buscarPorUsuarioId(String usuarioId) {
        return relacionamentoRepository.findByUsuarioId(usuarioId);
    }

    public List<Relacionamento> buscarPorOrganizacaoId(String organizacaoId) {
        return relacionamentoRepository.findByOrganizacaoId(organizacaoId);
    }

    public Optional<Relacionamento> buscarPorUsuarioIdAndOrganizacaoId(String usuarioId, String organizacaoId) {
        return relacionamentoRepository.findByUsuarioIdAndOrganizacaoId(usuarioId, organizacaoId);
    }

    public List<Usuario> buscarUsuariosPorOrganizacao(String organizacaoId) {
        if (organizacaoService.buscarPorId(organizacaoId).isEmpty()) {
            throw new IllegalArgumentException("Organização com ID " + organizacaoId + " não encontrada.");
        }

        var relacionamentos = relacionamentoRepository.findByOrganizacaoId(organizacaoId);

        if (relacionamentos.isEmpty()) {
            return Collections.emptyList();
        }

        return relacionamentos.stream()
                .map(r -> usuarioService.buscarPorId(r.getUsuarioId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}
