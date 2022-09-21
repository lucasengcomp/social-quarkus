package br.com.social.user.resource;

import br.com.social.user.dto.CreateUserRequest;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static br.com.social.core.consts.ConstsStatusCode.UNPROCESSABLE_ENTITY_STATUS;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("Should create an user sucessfully")
    @Order(1)
    public void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("Lucas");
        user.setAge(18);

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                        .when()
                        .post(apiURL)
                        .then()
                        .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("Should return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest() {
        var user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                        .when()
                        .post(apiURL)
                        .then()
                        .extract().response();

        assertEquals(UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
//        assertEquals("Age is required", errors.get(0).get("message"));
//        assertEquals("Name is required", errors.get(1).get("message"));
    }

    @Test
    @DisplayName("Should list all users")
    @Order(3)
    public void listAllUsers() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(apiURL)
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}
