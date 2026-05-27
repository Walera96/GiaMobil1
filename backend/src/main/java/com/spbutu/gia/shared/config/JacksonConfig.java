package com.spbutu.gia.shared.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Jackson для корректной сериализации Hibernate-прокси.
 * Убирает ошибку "No serializer found for class ByteBuddyInterceptor".
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }
}
