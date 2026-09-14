package http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import http.exception.BadRequestException;

public final class Json {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Json() {

    }

    public static <T> T read(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new BadRequestException(
                    "잘못된 JSON입니다.", e
            );
        }

    }

    public static String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "JSON 직렬화에 실패했습니다.", e
            );
        }
    }
}
