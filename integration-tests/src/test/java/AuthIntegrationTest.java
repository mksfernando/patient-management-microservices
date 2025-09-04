import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthIntegrationTest {
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnOkWithValidToken() {
        // 1. Arrange
        String loginPayload = """
                    {
                        "email": "testuser@test.com",
                        "password": "password123"
                    }
                """;

        // 2. Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login");

        // 3. Assert
        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertNotNull(response.body().jsonPath().getString("token"));

        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnUnauthorisedOnInvalidLogin() {
        // 1. Arrange
        String loginPayload = """
                    {
                        "email": "invaliduser@test.com",
                        "password": "wrongpassword"
                    }
                """;

        // 2. Act
        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login");

        // 3. Assert
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }
}
