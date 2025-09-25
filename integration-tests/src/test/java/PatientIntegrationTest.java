import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class PatientIntegrationTest {
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnPatientsWithValidToken() {
        // 1. Arrange
        String token = getToken();

        // 2. Act
        Response response = given()
                .header("Authorization", String.format("Bearer %s", token))
                .when()
                .get("api/patients");

        // 3. Assert
        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertNotNull(response.body());

        System.out.println(response.body().prettyPrint());
    }

    @Test
    public void shouldReturn429AfterLimitExceed() throws InterruptedException {
        int total = 20;
        int tooManyRequests = 0;
        String token = getToken();

        for (int i = 1; i <= total; i++) {
            Response response = RestAssured
                    .given()
                    .header("Authorization", "Bearer " + token)
                    .get("/api/patients");
            System.out.printf("Request %d status: %d%n", i, response.statusCode());
            if (response.statusCode() == 429) {
                tooManyRequests++;
            }
            Thread.sleep(100);
        }
        assertTrue(tooManyRequests >= 1, "At least 1 request to be Rate Limited(429)");
    }

    private static String getToken() {
        String loginPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "password123"
                }
                """;
        return given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath()
                .get("token");
    }
}
