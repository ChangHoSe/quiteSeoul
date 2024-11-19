package com.seochang.quiteSeoul.controller;

import com.seochang.quiteSeoul.domain.Comment;
import com.seochang.quiteSeoul.domain.dto.CommentDTO;
import com.seochang.quiteSeoul.domain.dto.MemberDTO;
import com.seochang.quiteSeoul.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/create")
    public ResponseEntity<Map<String, Object>> createComment(@RequestBody CommentDTO commentDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            commentService.createComment(commentDTO);
            response.put("message", "댓글 등록 완료");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "댓글 등록 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/comment/{placeName}")
    @ResponseBody
    public List<CommentDTO> showComments(@PathVariable String placeName) {
        List<CommentDTO> comments =  commentService.readCommentByPlaceName(placeName);
        return comments;
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @PathVariable Integer commentId,
            @RequestBody CommentDTO commentDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            commentDTO.setCommentId(commentId); // PathVariable의 commentId를 DTO에 설정
            commentService.updateComment(commentDTO);
            response.put("message", "댓글이 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put("message", "댓글을 찾을 수 없습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "댓글 수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable Integer commentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setCommentId(commentId);
            commentService.deleteComment(commentDTO);
            response.put("message", "댓글이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put("message", "댓글을 찾을 수 없습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
