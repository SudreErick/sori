package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.Organizacao;
import br.com.fiap.appSori.domain.dto.request.OrganizacaoRequestDto;
import br.com.fiap.appSori.domain.dto.response.OrganizacaoResponseDto;
import org.springframework.stereotype.Component;

@Component
public class OrganizacaoMapper {

    /**
     * Converte um DTO de Requisição em um objeto de Domínio (Organização).
     * Este método é usado para traduzir os dados recebidos da API
     * para o formato que a lógica de negócio irá processar.
     *
     * @param dto O DTO de requisição com os dados da organização.
     * @return O objeto de domínio 'Organizacao'.
     */
    public Organizacao toDomain(OrganizacaoRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Organizacao organizacao = new Organizacao();
        organizacao.setNome(dto.getNome());
        organizacao.setCnpj(dto.getCnpj());
        organizacao.setEndereco(dto.getEndereco());
        organizacao.setTelefone(dto.getTelefone());
        organizacao.setEmailContato(dto.getEmailContato());
        return organizacao;
    }

    /**
     * Converte um objeto de Domínio (Organização) em um DTO de Resposta.
     * Este método é usado para traduzir os dados internos da aplicação
     * para o formato que a API irá expor para o front-end.
     *
     * @param domain O objeto de domínio 'Organizacao'.
     * @return O DTO de resposta com todos os dados da organização.
     */
    public OrganizacaoResponseDto toDto(Organizacao domain) {
        if (domain == null) {
            return null;
        }

        OrganizacaoResponseDto dto = new OrganizacaoResponseDto();
        dto.setId(domain.getId());
        dto.setNome(domain.getNome());
        dto.setCnpj(domain.getCnpj());
        dto.setEndereco(domain.getEndereco());
        dto.setTelefone(domain.getTelefone());
        dto.setEmailContato(domain.getEmailContato());
        dto.setAtivo(domain.isAtivo());
        dto.setCriadoEm(domain.getCriadoEm());
        dto.setAtualizadoEm(domain.getAtualizadoEm());
        return dto;
    }
}
