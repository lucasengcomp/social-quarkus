package br.com.social.post.resource;

import br.com.social.post.dto.CreatePostRequest;
import br.com.social.post.dto.PostResponse;
import br.com.social.post.model.Post;
import br.com.social.post.repository.PostRepository;
import br.com.social.user.model.User;
import br.com.social.user.repository.UserRepository;
import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.social.core.consts.ConstsStatusCode.CREATED_STATUS;
import static br.com.social.core.consts.ConstsStatusCode.NOT_FOUND_STATUS;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private PostRepository postRepository;
    private UserRepository userRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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
    public Response listPosts(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(NOT_FOUND_STATUS).build();
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
