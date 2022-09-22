package br.com.social.application.post.resource;

import br.com.social.application.follower.repository.FollowerRepository;
import br.com.social.application.post.dto.CreatePostRequest;
import br.com.social.application.post.dto.PostResponse;
import br.com.social.application.user.model.User;
import br.com.social.application.user.repository.UserRepository;
import br.com.social.application.post.model.Post;
import br.com.social.application.post.repository.PostRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.social.core.consts.ConstsStatusCode.*;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response createPost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(NOT_FOUND_STATUS).build();
        }

        postRepository.persist(instanceEntityPost(request, user));

        return Response.status(CREATED_STATUS).build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId,
                              @HeaderParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(NOT_FOUND_STATUS).build();
        }

        if (followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId").build();
        }

        User follower = userRepository.findById(followerId);
        if (follower == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Inexistent followerId").build();
        }

        boolean isFollow = followerRepository.isFollower(follower, user);

        if (!isFollow) {
            return Response.status(FORBIDDEN).entity("You can't see these posts").build();
        }

        return Response.ok(organizeListPosts(user)).build();
    }

    private List<PostResponse> organizeListPosts(User user) {
        PanacheQuery<Post> query = postRepository.find(
                "user",
                Sort.by("dateTime", Sort.Direction.Descending),
                user);
        List<Post> list = query.list();

        List<PostResponse> postResponseList = list
                .stream()
                .map(PostResponse::fromEntity) //.map(post -> PostResponse.fromEntity(post))
                .collect(Collectors.toList());
        return postResponseList;
    }

    private static Post instanceEntityPost(CreatePostRequest request, User user) {
        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);
        return post;
    }
}
