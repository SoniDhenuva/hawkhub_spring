package com.open.spring.mvc.clubs;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ClubCategoriesConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return "[]";
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(categories);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Unable to serialize club categories", exception);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String databaseValue) {
        if (databaseValue == null || databaseValue.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return OBJECT_MAPPER.readValue(databaseValue, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Unable to parse club categories", exception);
        }
    }
}