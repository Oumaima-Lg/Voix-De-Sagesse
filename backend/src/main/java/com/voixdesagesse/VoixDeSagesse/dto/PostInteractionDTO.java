package com.voixdesagesse.VoixDeSagesse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostInteractionDTO {
    private boolean isLiked;
    private boolean isFollowing;
}