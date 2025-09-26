package br.com.fiap.appSori.mapper;

import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.dto.request.UsuarioRequestDto;
import br.com.fiap.appSori.domain.dto.response.UsuarioResponseDto;
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
        return dto;
    }
}
