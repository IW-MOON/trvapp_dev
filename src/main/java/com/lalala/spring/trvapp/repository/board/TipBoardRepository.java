package com.lalala.spring.trvapp.repository.board;

import com.lalala.spring.trvapp.entity.board.TipBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipBoardRepository extends JpaRepository<TipBoard, Long> {

    List<TipBoard> findByIsDeletedIsFalse();
}
