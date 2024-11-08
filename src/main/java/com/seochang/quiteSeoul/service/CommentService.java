package com.seochang.quiteSeoul.service;

import com.seochang.quiteSeoul.domain.Comment;
import com.seochang.quiteSeoul.domain.dto.CommentDTO;
import com.seochang.quiteSeoul.repository.CommentRepository;
import com.seochang.quiteSeoul.repository.MemberRepository;
import com.seochang.quiteSeoul.repository.PlaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    //c
    public void createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setRating(commentDTO.getRating());
        comment.setContent(commentDTO.getContent());
        comment.setMember(memberRepository.findByNickname(commentDTO.getNickname())
                .orElseThrow(() -> new EntityNotFoundException("멤버 없음")));
        comment.setPlace(placeRepository.findByPlaceName(commentDTO.getPlaceName())
                .orElseThrow(() -> new EntityNotFoundException("장소 없음")));
        commentRepository.save(comment);
    }

    //read-place 상세페이지에서 사용
    public List<CommentDTO> readCommentByPlaceName(String placeName) {
        List<Comment> comments = commentRepository.findCommentsByPlaceName(placeName);

        return comments.stream()
                .map(comment -> {
                    CommentDTO dto = new CommentDTO(comment);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    //read-member 마이페이지에서 사용
    public List<Comment> readCommentByNickname(String nickname) {
        return commentRepository.findCommentsByNickname(nickname);
    }

    //u
    public void updateComment(CommentDTO commentDTO) {
        Comment comment = getCommentsById(commentDTO.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException("댓글 없음"));
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
    }

    //d
    public void deleteComment(CommentDTO commentDTO) {
        Comment comment = getCommentsById(commentDTO.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException("댓글 없음"));
        commentRepository.delete(comment);
    }

    public void addLikeCount(CommentDTO commentDTO) {
        Comment comment = getCommentsById(commentDTO.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException("댓글 없음"));
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }

    private Optional<Comment> getCommentsById(Integer commentId) {
        return commentRepository.findById(commentId);
    }
}
