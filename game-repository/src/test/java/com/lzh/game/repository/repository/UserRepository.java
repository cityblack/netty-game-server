package com.lzh.game.repository.repository;

import com.lzh.game.repository.entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, String> {
}
