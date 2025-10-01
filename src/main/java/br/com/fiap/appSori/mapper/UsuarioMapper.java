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

        // A LÓGICA DE DEFINIR A ROLE FOI REMOVIDA DAQUI.
        // O construtor 'new Usuario()' garante que a ROLE seja CLIENTE.

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

        // Mapeamento da Role DE SAÍDA: Converte o Enum (que é CLIENTE) para String.
        if (domain.getRole() != null) {
            dto.setRole(domain.getRole().name());
        }

        return dto;
    }
}
