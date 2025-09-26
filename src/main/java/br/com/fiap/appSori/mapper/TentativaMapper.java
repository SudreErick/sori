package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.RespostaBruta;
import br.com.fiap.appSori.domain.Tentativa;
import br.com.fiap.appSori.domain.dto.RespostaBrutaDto;
import br.com.fiap.appSori.domain.dto.response.TentativaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TentativaMapper {
    // --- Mapeamento RespostaBruta (Domain <-> DTO) ---
    List<RespostaBruta> toDomainList(List<RespostaBrutaDto> dtos);
    List<RespostaBrutaDto> toDtoList(List<RespostaBruta> domains);

    // --- Mapeamento Tentativa (Domain -> Response DTO) ---
    @Mappings({
            // Mapeia o ID do Teste da entidade aninhada 'teste' para o campo 'testeId' do DTO
            @Mapping(source = "teste.id", target = "testeId"),

            // CORREÇÃO AQUI: Usando 'titulo' em vez de 'nome'
            @Mapping(source = "teste.titulo", target = "nomeTeste"),

            // Mapeia a lista de respostas, usando o método auxiliar (toDtoList)
            @Mapping(source = "respostas", target = "respostas"),

            // Calcula o número total de respostas salvas
            @Mapping(target = "totalRespostasSalvas", expression = "java(domain.getRespostas() != null ? domain.getRespostas().size() : 0)")
    })
    TentativaResponseDto toDto(Tentativa domain);
}
