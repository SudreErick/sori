package br.com.fiap.appSori.domain.enums;

import lombok.Getter;

@Getter
public enum SentimentoDiario {
    // Sentimentos de Alta Frequência (Negativos/Neutros)
    ANSIOSO("Ansioso"),
    CANSADO("Cansado"),
    ESTRESSADO("Estressado"),
    MEDO("Medo"),
    PREOCUPADO("Preocupado"),
    NEUTRO("Neutro"),

    // Sentimentos de Baixa Frequência (Positivos)
    ANIMADO("Animado"),
    MOTIVADO("Motivado"),
    SATISFEITO("Satisfeito");

    private final String descricao;

    SentimentoDiario(String descricao) {
        this.descricao = descricao;
    }
}
