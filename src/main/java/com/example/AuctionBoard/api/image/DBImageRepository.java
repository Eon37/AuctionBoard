package com.example.AuctionBoard.api.image;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBImageRepository extends CrudRepository<DBImage, Long> {
}
