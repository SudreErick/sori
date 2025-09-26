package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.OpcaoResposta;
import br.com.fiap.appSori.domain.dto.request.OpcaoRespostaRequestDto;
import br.com.fiap.appSori.domain.dto.response.OpcaoRespostaResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OpcaoRespostaMapper {
    OpcaoResposta toDomain(OpcaoRespostaRequestDto dto);
    List<OpcaoResposta> toDomain(List<OpcaoRespostaRequestDto> dto);

    OpcaoRespostaResponseDto toDto(OpcaoResposta domain);
    List<OpcaoRespostaResponseDto> toDto(List<OpcaoResposta> domain);
}
