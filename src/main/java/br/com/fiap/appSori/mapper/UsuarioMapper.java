package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.dto.request.UsuarioRequestDto;
import br.com.fiap.appSori.domain.dto.response.UsuarioResponseDto;
import br.com.fiap.appSori.domain.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public Usuario toDomain(UsuarioRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setCpf(dto.getCpf());
        usuario.setTelefone(dto.getTelefone());
        usuario.setCargo(dto.getCargo());

        // Mapeamento da Role: Se a string não for nula, tente converter.
        // Se a conversão falhar (nome inválido) ou a string for nula,
        // o construtor de Usuario já garante o valor padrão (CLIENTE).
        if (dto.getRole() != null) {
            try {
                usuario.setRole(Role.valueOf(dto.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Deixa o valor padrão (CLIENTE) definido no construtor de Usuario.
                // Em produção, você pode querer logar esse erro ou lançar uma exceção de validação.
            }
        }

        return usuario;
    }

    public UsuarioResponseDto toDto(Usuario domain) {
        if (domain == null) {
            return null;
        }

        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(domain.getId());
        dto.setNomeCompleto(domain.getNomeCompleto());
        dto.setEmail(domain.getEmail());
        dto.setCpf(domain.getCpf());
        dto.setTelefone(domain.getTelefone());
        dto.setCargo(domain.getCargo());
        dto.setCriadoEm(domain.getCriadoEm());
        dto.setAtualizadoEm(domain.getAtualizadoEm());
        dto.setAtivo(domain.isAtivo());

        // Mapeamento da Role: Converte o Enum para String.
        if (domain.getRole() != null) {
            dto.setRole(domain.getRole().name());
        }

        return dto;
    }
}
