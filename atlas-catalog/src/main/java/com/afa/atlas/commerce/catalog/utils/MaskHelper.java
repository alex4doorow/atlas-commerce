package com.afa.atlas.commerce.catalog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public final class MaskHelper {

    private static final Set<String> MASKING_KEYS = Set.of(
            "password",
            "access_token",
            "refresh_token"
    );

    private static final int MIN_LENGTH = 4;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MaskHelper() {
    }

    public static String maskSecret(final String secret) {
        if (StringUtils.isBlank(secret)) {
            return secret;
        }

        final int valueLength = secret.length();

        if (valueLength <= MIN_LENGTH) {
            return "*".repeat(valueLength);
        }

        final int visibleLength = Math.min(valueLength / MIN_LENGTH, MIN_LENGTH);

        return secret.substring(0, visibleLength)
                + "*".repeat(MIN_LENGTH)
                + secret.substring(valueLength - visibleLength);
    }

    public static String maskJson(final String json) {
        if (json == null) {
            return null;
        }

        try {
            final JsonNode root = OBJECT_MAPPER.readTree(json);
            maskNode(root);
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private static void maskNode(final JsonNode node) {

        if (node instanceof ObjectNode objectNode) {

            objectNode.fieldNames().forEachRemaining(fieldName -> {

                final JsonNode child = objectNode.get(fieldName);

                if (child.isTextual() && MASKING_KEYS.contains(fieldName)) {
                    objectNode.put(fieldName, maskSecret(child.asText()));
                } else {
                    maskNode(child);
                }
            });

        } else if (node instanceof ArrayNode arrayNode) {

            for (final JsonNode child : arrayNode) {
                maskNode(child);
            }
        }
    }
}