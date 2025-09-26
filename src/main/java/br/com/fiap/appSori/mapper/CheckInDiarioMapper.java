package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.CheckInDiario;
import br.com.fiap.appSori.domain.dto.request.CheckInDiarioRequestDto;
import br.com.fiap.appSori.domain.dto.response.CheckInDiarioResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CheckInDiarioMapper {
    // --- Mapeamento do Request para o Domain ---
    @Mappings({
            // Ignorar campos de persistência, pois serão definidos no Service
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "usuario", ignore = true),         // Definido no Service (usuário logado)
            @Mapping(target = "dataCheckin", ignore = true),     // Definido no Service (LocalDate.now())
            @Mapping(target = "dataRegistro", ignore = true)     // Definido automaticamente na Entidade
    })
    CheckInDiario toDomain(CheckInDiarioRequestDto dto);

    // --- Mapeamento do Domain para o Response ---
    @Mappings({
            // Mapeia o ID do usuário aninhado (DBRef) para o campo 'usuarioId' do DTO
            @Mapping(source = "usuario.id", target = "usuarioId")
    })
    CheckInDiarioResponseDto toDto(CheckInDiario domain);
}
