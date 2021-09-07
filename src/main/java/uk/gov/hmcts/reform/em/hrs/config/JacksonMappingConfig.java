package uk.gov.hmcts.reform.em.hrs.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonMappingConfig {

    @Bean
    public Jackson2ObjectMapperBuilder provideJackson2ObjectMapperBuilder() {
        final Jackson2ObjectMapperBuilder jsonBuilderConfig = new Jackson2ObjectMapperBuilder();
        jsonBuilderConfig.propertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        jsonBuilderConfig.findModulesViaServiceLoader(true);
        jsonBuilderConfig.failOnUnknownProperties(false);

        return jsonBuilderConfig;
    }


}
