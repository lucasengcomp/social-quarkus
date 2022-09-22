package br.com.social.application.follower.resource;

import br.com.social.application.follower.model.Follower;
import br.com.social.application.follower.repository.FollowerRepository;
import br.com.social.application.user.repository.UserRepository;
import br.com.social.application.follower.model.dto.FollowerPerUserResponse;
import br.com.social.application.follower.model.dto.FollowerRequest;
import br.com.social.application.follower.model.dto.FollowerResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.social.core.consts.ConstsStatusCode.*;

@Path("users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request) {

        if (userId.equals(request.getFollowerId())) {
            return Response.status(CONFLICT).entity("You can't follow yourself").build();
        }

        var user = userRepository.findById(userId);

        if (user == null) {
            return Response.status(NOT_FOUND_STATUS).build();
        }

        var follower = userRepository.findById(request.getFollowerId());
        boolean isFollow = followerRepository.isFollower(follower, user);

        if (!isFollow) {
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);
            followerRepository.persist(entity);
        }

        return Response.status(NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {
        var user = userRepository.findById(userId);

        if (user == null) {
            return Response.status(NOT_FOUND_STATUS).build();
        }

        var list = followerRepository.findByUser(userId);
        FollowerPerUserResponse responseObject = new FollowerPerUserResponse();

        responseObject.setFollowerPerCount(list.size());

        List<FollowerResponse> followerList = list.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());

        responseObject.setContent(followerList);

        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userId") Long userId,
            @QueryParam("followerId")  Long followerId ){
        var user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
