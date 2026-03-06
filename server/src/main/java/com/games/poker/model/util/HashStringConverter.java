package com.games.poker.model.util;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;




@Converter
public class HashStringConverter implements AttributeConverter<String, String> {
	
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


	@Override
	public String convertToDatabaseColumn(String value) {
		if(isBlank(value)) {
			return value;
		}
		if (!value.startsWith("$2a$")) {
            return passwordEncoder.encode(value);
        }
        return value;
	}

	@Override
	public String convertToEntityAttribute(String value) {
		return value;
	}
	
}
