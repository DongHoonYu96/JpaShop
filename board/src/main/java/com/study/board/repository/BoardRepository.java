package com.study.board.repository;

import com.study.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> { //<엔티티, key의타입>
    //JPAREPOSITY에는 findall등 구현되어있음
    //상속받아서 사용하면된다.

    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);
}
