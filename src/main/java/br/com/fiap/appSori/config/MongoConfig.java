package br.com.fiap.appSori.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.core.convert.converter.Converter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Configuration
public class MongoConfig {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
                ZonedDateTimeToStringConverter.INSTANCE,
                StringToZonedDateTimeConverter.INSTANCE
        ));
    }

    // Converters como classes aninhadas para simplicidade
    @WritingConverter
    public static class ZonedDateTimeToStringConverter implements Converter<ZonedDateTime, String> {
        public static final ZonedDateTimeToStringConverter INSTANCE = new ZonedDateTimeToStringConverter();

        @Override
        public String convert(ZonedDateTime source) {
            return source == null ? null : source.format(DateTimeFormatter.ISO_INSTANT);
        }
    }

    @ReadingConverter
    public static class StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime> {
        public static final StringToZonedDateTimeConverter INSTANCE = new StringToZonedDateTimeConverter();

        @Override
        public ZonedDateTime convert(String source) {
            // Converte a string para um Instant e depois para um ZonedDateTime no fuso horário do sistema
            return source == null ? null : ZonedDateTime.ofInstant(java.time.Instant.parse(source), java.time.ZoneId.systemDefault());
        }
    }
}
