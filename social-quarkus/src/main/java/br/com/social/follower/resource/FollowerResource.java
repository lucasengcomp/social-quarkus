package br.com.social.follower.resource;

import br.com.social.follower.model.Follower;
import br.com.social.follower.model.dto.FollowerRequest;
import br.com.social.follower.repository.FollowerRepository;
import br.com.social.user.repository.UserRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static br.com.social.core.consts.ConstsStatusCode.NOT_FOUND_STATUS;
import static br.com.social.core.consts.ConstsStatusCode.NO_CONTENT;

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
}
