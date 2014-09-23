package me.icymint.sloth.web.security;

import java.util.List;

import me.icymint.sloth.web.security.domain.User;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends
		PagingAndSortingRepository<User, String> {

	List<User> findByUsername(String name);

}