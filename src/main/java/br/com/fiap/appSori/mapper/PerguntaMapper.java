package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.Pergunta;
import br.com.fiap.appSori.domain.dto.request.PerguntaRequestDto;
import br.com.fiap.appSori.domain.dto.response.PerguntaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = { OpcaoRespostaMapper.class })
public interface PerguntaMapper {
    // --- Mapeamento Request (DTO) para Domain (Criação de Teste) ---
    // O MapStruct é instruído a gerar um ID único para cada nova Pergunta,
    // garantindo que ela seja rastreável.
    @Mappings({
            @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    })
    Pergunta toDomain(PerguntaRequestDto dto);

    List<Pergunta> toDomain(List<PerguntaRequestDto> dto);

    // --- Mapeamento Domain para Response (Consulta de Teste) ---
    // Agora, o MapStruct mapeará automaticamente o ID.
    PerguntaResponseDto toDto(Pergunta domain);

    List<PerguntaResponseDto> toDto(List<Pergunta> domain);
}
