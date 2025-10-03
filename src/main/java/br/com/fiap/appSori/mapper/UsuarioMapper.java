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
        usuario.setCargo(dto.getCargo());

        // CPF e Telefone serão nulos aqui se não vieram no Request DTO simplificado,
        // o que sinaliza que o usuário precisa completar o cadastro.

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
        dto.setCargo(domain.getCargo());
        dto.setCriadoEm(domain.getCriadoEm());
        dto.setAtualizadoEm(domain.getAtualizadoEm());
        dto.setAtivo(domain.isAtivo());

        // Mapeamento da Role DE SAÍDA
        if (domain.getRole() != null) {
            dto.setRole(domain.getRole().name());
        }

        // A LÓGICA DO PRIMEIRO LOGIN SERÁ INJETADA NO SERVICE/CONTROLLER.
        // O Mapper apenas prepara o DTO.

        return dto;
    }
}
