package br.com.social.post.resource;


import br.com.social.application.follower.model.Follower;
import br.com.social.application.follower.repository.FollowerRepository;
import br.com.social.application.post.dto.CreatePostRequest;
import br.com.social.application.post.model.Post;
import br.com.social.application.post.repository.PostRepository;
import br.com.social.application.post.resource.PostResource;
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

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
public class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUp() {
        var user = new User();
        user.setAge(20);
        user.setName("Godofredo");
        userRepository.persist(user);
        userId = user.getId();

        var post = new Post();
        post.setText("Speak up guys! I'm away for a few days");
        post.setUser(user);
        postRepository.persist(post);

        var userNotFollower = new User();
        userNotFollower.setAge(20);
        userNotFollower.setName("Mestre Yoda");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        var userFollower = new User();
        userFollower.setAge(32);
        userFollower.setName("Cipriano Naves");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        var follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("Should create a post for a user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Ok let's do it guys!");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParams("userId", userId)
                .when()
                .post()
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Ok let's do it guys!");

        var inexistentUserId = 1000;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParams("userId", inexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest() {
        var inexistentUserId = 1000;

        given()
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest() {
        given()
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("Should return 400 when follower doesn't exist")
    public void listPostFollowerNotFoundTest() {
        var inexistentFollower = 1000;
        given()
                .pathParam("userId", userId)
                .header("followerId", inexistentFollower)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @DisplayName("Should return 403 when follower isn't a follower")
    public void listPostNotAFollowerTest() {
        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("Should list posts")
    public void listAllPostsPerUserTest() {
        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}
