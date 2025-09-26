package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.Teste;
import br.com.fiap.appSori.domain.dto.request.TesteRequestDto;
import br.com.fiap.appSori.domain.dto.response.TesteResponseDto;
import br.com.fiap.appSori.domain.enums.TipoTeste;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring", uses = { PerguntaMapper.class })
public interface TesteMapper {
    // Instrução para ignorar o 'id' ao mapear do Request para o Domain
    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    Teste toDomain(TesteRequestDto dto);

    TesteResponseDto toDto(Teste domain);

    // Método para converter String para Enum (Request para Domain)
    default TipoTeste mapTipo(String tipo) {
        if (tipo == null) {
            return null;
        }
        return TipoTeste.valueOf(tipo.toUpperCase());
    }

    // Método para converter Enum para String (Domain para Response)
    default String mapTipo(TipoTeste tipo) {
        if (tipo == null) {
            return null;
        }
        return tipo.name();
    }
}
