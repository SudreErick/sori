package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.Resultado;
import br.com.fiap.appSori.domain.dto.request.RespostaUsuarioRequestDto;
import br.com.fiap.appSori.domain.dto.request.ResultadoRequestDto;
import br.com.fiap.appSori.domain.dto.response.RespostaUsuarioResponseDto;
import br.com.fiap.appSori.domain.dto.response.ResultadoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResultadoMapper {
    // Mapeamento de RequestDto para Entidade
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "teste", ignore = true),
            @Mapping(target = "usuario", ignore = true),
            @Mapping(target = "dataRealizacao", ignore = true),
            @Mapping(target = "nivelRisco", ignore = true),
            @Mapping(target = "pontuacaoTotal", ignore = true)
    })
    Resultado toDomain(ResultadoRequestDto dto);

    // Mapeamento de Entidade para ResponseDto
    @Mappings({
            @Mapping(target = "testeId", source = "teste.id"),
            @Mapping(target = "tipoTeste", source = "teste.tipo"),
            @Mapping(target = "usuarioId", source = "usuario.id")
    })
    ResultadoResponseDto toDto(Resultado domain);

    // Mapeamento de listas e subdocumentos
    List<RespostaUsuarioResponseDto> toRespostaUsuarioResponseDtoList(List<RespostaUsuarioRequestDto> requestList);
    List<RespostaUsuarioRequestDto> toRespostaUsuarioRequestDtoList(List<RespostaUsuarioResponseDto> responseList);

    // Mapeamento entre os subdocumentos
    RespostaUsuarioResponseDto toRespostaUsuarioResponseDto(RespostaUsuarioRequestDto request);
    RespostaUsuarioRequestDto toRespostaUsuarioRequestDto(RespostaUsuarioResponseDto response);
}
