package com.spbutu.gia.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Конфигурация JPA Auditing.
 * Позволяет автоматически заполнять поля createdAt/updatedAt
 * через аннотации @CreatedDate и @LastModifiedDate.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
