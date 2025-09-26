package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.Pergunta;
import br.com.fiap.appSori.domain.dto.request.PerguntaRequestDto;
import br.com.fiap.appSori.domain.dto.response.PerguntaResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = { OpcaoRespostaMapper.class })
public interface PerguntaMapper {
    Pergunta toDomain(PerguntaRequestDto dto);
    List<Pergunta> toDomain(List<PerguntaRequestDto> dto);

    PerguntaResponseDto toDto(Pergunta domain);
    List<PerguntaResponseDto> toDto(List<Pergunta> domain);
}
