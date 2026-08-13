package dev.ozodn.onlineshop.common.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Test for PagedResponse's JSON-serialization")
class PagedResponseJsonTest {

    private JacksonTester<PagedResponse<TestProductDto>> json;

    record TestProductDto(UUID id, String name) {}

    @BeforeEach
    void setUp() {
        // Manual initialization of JacksonTester without Spring Boot context
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    @DisplayName("PagedResponse serialization must strictly adhere to the JSON standard.")
    void shouldSerializeToStandardJsonFormat() throws IOException {
        // Arrange
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        TestProductDto item = new TestProductDto(id, "Wireless Mouse");

        PageRequest pageable = PageRequest.of(0, 20);
        Page<TestProductDto> springPage = new PageImpl<>(List.of(item), pageable, 1);

        PagedResponse<TestProductDto> pagedResponse = PagedResponse.from(springPage);

        // Act & Assert
        assertThat(this.json.write(pagedResponse)).isEqualToJson("""
                {
                  "content": [
                    {
                      "id": "550e8400-e29b-41d4-a716-446655440000",
                      "name": "Wireless Mouse"
                    }
                  ],
                  "page": {
                    "number": 0,
                    "size": 20,
                    "totalElements": 1,
                    "totalPages": 1
                  }
                }
                """);
    }

    @Test
    @DisplayName("Edge Case: Must serialize an empty page correctly (Page.empty())")
    void shouldSerializeEmptyPageToStandardJson() throws IOException {
        // Arrange
        Page<TestProductDto> emptyPage = Page.empty();

        // Act
        PagedResponse<TestProductDto> response = PagedResponse.from(emptyPage);

        // Assert
        assertThat(this.json.write(response)).isEqualToJson("""
                {
                  "content": [],
                  "page": {
                    "number": 0,
                    "size": 0,
                    "totalElements": 0,
                    "totalPages": 1
                  }
                }
                """);
    }

    @Test
    @DisplayName("Edge Case: Should throw NullPointerException if null is passed.")
    void shouldThrowNullPointerExceptionWhenSpringPageIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> PagedResponse.from(null))
                .isInstanceOf(NullPointerException.class);
    }
}