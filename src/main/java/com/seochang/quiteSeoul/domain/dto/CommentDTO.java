package com.seochang.quiteSeoul.domain.dto;

import com.seochang.quiteSeoul.domain.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class CommentDTO {

    private Integer commentId;
    private String content;
    private double rating;
    private String nickname;
    private String placeName;
    private LocalDateTime createdAt;

    public CommentDTO(Comment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.rating = comment.getRating();
        this.nickname = comment.getMember().getNickname();
        this.placeName = comment.getPlace().getPlaceName();
        this.createdAt = comment.getCreatedAt();
    }

}
