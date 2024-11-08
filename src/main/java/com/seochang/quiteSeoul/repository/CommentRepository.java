package com.seochang.quiteSeoul.repository;

import com.seochang.quiteSeoul.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c WHERE c.place.placeName = :placeName")
    List<Comment> findCommentsByPlaceName(String placeName);

    @Query("SELECT c FROM Comment c WHERE c.member.nickname = :nickname")
    List<Comment> findCommentsByNickname(String nickname);
}
