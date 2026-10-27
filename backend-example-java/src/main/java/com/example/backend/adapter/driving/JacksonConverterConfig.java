package com.example.backend.adapter.driving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson Web 转换配置
 */
@Configuration
public class JacksonConverterConfig {

    /**
     * 配置 Spring MVC JSON 转换器
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        ObjectMapper objectMapper = builder
                //.indentOutput(true)
                .serializerByType(Long.TYPE, ToStringSerializer.instance)                                   // long转String
                .serializerByType(Long.class, ToStringSerializer.instance)                                  // Long转String
                .serializerByType(BigDecimal.class, ToStringSerializer.instance)                            // BigDecimal转String
                .simpleDateFormat("yyyy-MM-dd HH:mm:ss")                                                    // Date转String，默认为timestamp
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter))      // LocalDateTime转String
                .serializerByType(LocalDate.class, new LocalDateSerializer(dateFormatter))                  // LocalDate转String
                .serializerByType(LocalTime.class, new LocalTimeSerializer(timeFormatter))                  // LocalTime转String
                .build();

        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}