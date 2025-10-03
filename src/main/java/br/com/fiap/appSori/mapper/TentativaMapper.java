package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.RespostaBruta;
import br.com.fiap.appSori.domain.Tentativa;
import br.com.fiap.appSori.domain.Teste;
import br.com.fiap.appSori.domain.dto.RespostaBrutaDto;
import br.com.fiap.appSori.domain.dto.response.TentativaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TentativaMapper {
    // --- Mapeamento RespostaBruta (Domain <-> DTO) ---
    List<RespostaBruta> toDomainList(List<RespostaBrutaDto> dtos);
    List<RespostaBrutaDto> toDtoList(List<RespostaBruta> domains);

    // --- Mapeamento Tentativa (Domain -> Response DTO) ---
    @Mappings({
            // Mapeia o ID do Teste da entidade aninhada 'teste' para o campo 'testeId' do DTO
            @Mapping(source = "teste.id", target = "testeId"),

            // Mapeia o título do Teste da entidade aninhada
            @Mapping(source = "teste.titulo", target = "nomeTeste"),

            // Mapeia a lista de respostas, usando o método auxiliar (toDtoList)
            @Mapping(source = "respostas", target = "respostas"),

            // Calcula o número total de respostas salvas
            @Mapping(target = "totalRespostasSalvas", expression = "java(domain.getRespostas() != null ? domain.getRespostas().size() : 0)")

            // O mapeamento para 'usuarioId' foi removido para corrigir a falha de compilação.
    })
    TentativaResponseDto toDto(Tentativa domain);

    // --- MÉTODOS: Suporte ao TesteService ---
    /**
     * Mapeia a entidade Tentativa para a entidade Teste (o objeto aninhado).
     * Essencial para o TesteService.buscarTestesRealizados.
     */
    default Teste toTesteDomain(Tentativa tentativa) {
        // Retorna a entidade Teste que está sendo referenciada dentro da Tentativa
        return tentativa.getTeste();
    }

    /**
     * Mapeia uma lista de Tentativas para uma lista de Teste.
     */
    default List<Teste> toTesteDomainList(List<Tentativa> tentativas) {
        if (tentativas == null) {
            return null;
        }
        return tentativas.stream()
                .map(this::toTesteDomain)
                .collect(Collectors.toList());
    }
}
