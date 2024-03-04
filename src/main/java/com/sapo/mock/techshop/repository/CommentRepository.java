package com.sapo.mock.techshop.repository;

import com.sapo.mock.techshop.dto.response.CommentDTO;
import com.sapo.mock.techshop.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "Select m.comment_text from comment m ", nativeQuery = true)
    List<String> getAllComment();
}
