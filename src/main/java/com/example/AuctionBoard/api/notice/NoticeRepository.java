package com.example.AuctionBoard.api.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends PagingAndSortingRepository<Notice, Long>, CrudRepository<Notice, Long> {
    Page<Notice> findByActiveIsTrue(Pageable pageable);
}
