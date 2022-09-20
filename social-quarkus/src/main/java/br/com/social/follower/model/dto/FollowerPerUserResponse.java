package br.com.social.follower.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowerPerUserResponse {

    private Integer followerPerCount;
    private List<FollowerResponse> content;
}
