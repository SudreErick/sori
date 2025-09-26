package br.com.fiap.appSori.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor
public class OpcaoResposta {
    private String texto; // Ex: "Nunca", "Raramente", etc.
    private int valor; // Pontuação
}
