package br.com.social.follower.resource;


import br.com.social.follower.model.dto.FollowerRequest;
import br.com.social.user.model.User;
import br.com.social.user.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
public class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        var user = new User();
        user.setAge(20);
        user.setName("Godofredo");
        userRepository.persist(user);
        userId = user.getId();

        var follower = new User();
        follower.setAge(4525); //idade do Gandolf é entre 3mil a 13mil anos
        follower.setName("Gandolf");
        userRepository.persist(follower);
        followerId = follower.getId();
    }

    @Test
    @DisplayName("Should return 409 when followerId is equal to User id")
    public void sameUserAsFollowerTest() {
        var follower = new FollowerRequest();
        follower.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(follower)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(409)
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("Should return 404 when User id doesn't exist")
    public void userNotFoundTest() {
        var follower = new FollowerRequest();
        follower.setFollowerId(userId);
        var inexistentUserId = 1000;

        given()
                .contentType(ContentType.JSON)
                .body(follower)
                .pathParam("userId", inexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest() {

        var follower = new FollowerRequest();
        follower.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(follower)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}
