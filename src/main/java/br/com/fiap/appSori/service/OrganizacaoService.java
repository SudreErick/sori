package br.com.fiap.appSori.service;

import br.com.fiap.appSori.domain.Organizacao;
import br.com.fiap.appSori.repository.OrganizacaoRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizacaoService {

    private final OrganizacaoRepository organizacaoRepository;

    // Injeção de dependência do repositório via construtor
    public OrganizacaoService(OrganizacaoRepository organizacaoRepository) {
        this.organizacaoRepository = organizacaoRepository;
    }

    // Método para criar uma nova organização
    public Organizacao criarOrganizacao(Organizacao novaOrganizacao) {
        // Lógica de negócio: Adicionar data de criação antes de salvar
        novaOrganizacao.setCriadoEm(ZonedDateTime.now());
        novaOrganizacao.setAtivo(true);
        return organizacaoRepository.save(novaOrganizacao);
    }

    // Método para buscar uma organização por ID
    public Optional<Organizacao> buscarPorId(String id) {
        return organizacaoRepository.findById(id);
    }

    // Método para buscar todas as organizações
    public List<Organizacao> buscarTodas() {
        return organizacaoRepository.findAll();
    }
}
