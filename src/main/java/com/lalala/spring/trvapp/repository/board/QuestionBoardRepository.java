package com.lalala.spring.trvapp.repository.board;

import com.lalala.spring.trvapp.entity.board.QuestionBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBoardRepository extends JpaRepository<QuestionBoard, Long> {

    List<QuestionBoard> findByIsDeletedIsFalse();
}
