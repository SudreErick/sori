package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.Relacionamento;
import br.com.fiap.appSori.domain.dto.request.RelacionamentoRequestDto;
import br.com.fiap.appSori.domain.dto.response.RelacionamentoResponseDto;
import org.springframework.stereotype.Component;

@Component
public class RelacionamentoMapper {
    public Relacionamento toDomain(RelacionamentoRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Relacionamento relacionamento = new Relacionamento();
        relacionamento.setUsuarioId(dto.getUsuarioId());
        relacionamento.setOrganizacaoId(dto.getOrganizacaoId());
        relacionamento.setCargo(dto.getCargo());
        return relacionamento;
    }

    public RelacionamentoResponseDto toDto(Relacionamento domain) {
        if (domain == null) {
            return null;
        }
        RelacionamentoResponseDto dto = new RelacionamentoResponseDto();
        dto.setId(domain.getId());
        dto.setUsuarioId(domain.getUsuarioId());
        dto.setOrganizacaoId(domain.getOrganizacaoId());
        dto.setCargo(domain.getCargo());
        dto.setCriadoEm(domain.getCriadoEm());
        dto.setAtualizadoEm(domain.getAtualizadoEm());
        dto.setAtivo(domain.isAtivo());
        return dto;
    }
}
