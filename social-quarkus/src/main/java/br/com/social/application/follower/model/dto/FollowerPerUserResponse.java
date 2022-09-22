package br.com.social.application.follower.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowerPerUserResponse {

    private Integer followerPerCount;
    private List<FollowerResponse> content;
}
