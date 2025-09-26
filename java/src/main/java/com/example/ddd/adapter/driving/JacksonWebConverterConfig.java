package com.example.ddd.adapter.driving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.math.BigDecimal;

/**
 * Jackson Web转换配置
 */
@Configuration
public class JacksonWebConverterConfig {

    /**
     * 配置 Spring MVC 的JSON转换器
     */
    @Bean
    public HttpMessageConverters jacksonWebConverters() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                //.indentOutput(true)
                .simpleDateFormat("yyyy-MM-dd HH:mm:ss")                            // Date转String
                .serializerByType(java.time.LocalDateTime.class,                    // LocalDateTime转String
                        new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        )
                )
                .serializerByType(Long.TYPE, new ToStringSerializer())              // long转String
                .serializerByType(Long.class, new ToStringSerializer())             // Long转String
                .serializerByType(BigDecimal.class, new ToStringSerializer())       // BigDecimal转String
                ;
        ObjectMapper objectMapper = builder.build();
        return new HttpMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper));
    }
}