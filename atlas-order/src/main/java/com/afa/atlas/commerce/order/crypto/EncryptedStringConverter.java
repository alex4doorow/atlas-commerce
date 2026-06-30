package com.afa.atlas.commerce.order.crypto;

import com.afa.atlas.crypto.service.CryptoService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Converter
@RequiredArgsConstructor
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private final CryptoService cryptoService;

    @Override
    public String convertToDatabaseColumn(final String value) {

        if (StringUtils.isBlank(value)) {
            return value;
        }
        return cryptoService.encrypt(value);
    }

    @Override
    public String convertToEntityAttribute(final String value) {

        if (StringUtils.isBlank(value)) {
            return value;
        }
        return cryptoService.decrypt(value);
    }
}