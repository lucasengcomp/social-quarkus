package br.com.social.follower.resource;


import br.com.social.application.follower.model.Follower;
import br.com.social.application.follower.model.dto.FollowerRequest;
import br.com.social.application.follower.repository.FollowerRepository;
import br.com.social.application.follower.resource.FollowerResource;
import br.com.social.application.user.model.User;
import br.com.social.application.user.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static br.com.social.core.consts.ConstsStatusCode.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
public class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;
    Long inexistentId;

    @BeforeEach
    @Transactional
    void setUp() {
        inexistentId = 1000L;

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

        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
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
                .statusCode(CONFLICT)
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("Should return 404 when User id doesn't exist")
    public void userNotFoundTest() {
        var follower = new FollowerRequest();
        follower.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(follower)
                .pathParam("userId", inexistentId)
                .when()
                .put()
                .then()
                .statusCode(NOT_FOUND_STATUS);
    }

    @Test
    @DisplayName("Should follow a user")
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
                .statusCode(NO_CONTENT);
    }

    @Test
    @DisplayName("Should return 404 on list users followers and User id doesn't exist")
    public void userNotFoundWhenListingFollowersTest() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentId)
                .when()
                .get()
                .then()
                .statusCode(NOT_FOUND_STATUS);
    }

    @Test
    @DisplayName("Should list a user followers")
    public void listFollowersTest() {
        var response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("userId", userId)
                        .when()
                        .get()
                        .then()
                        .extract().response();

        var followersCount = response.jsonPath().get("followerPerCount");
        var followersContent = response.jsonPath().getList("content");

        assertEquals(OK, response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());
    }

    @Test
    @DisplayName("Should unfollow an user")
    public void unfollowUserTest() {
        given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(NO_CONTENT);
    }
}
