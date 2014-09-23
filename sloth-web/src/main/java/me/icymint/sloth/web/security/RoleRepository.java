package me.icymint.sloth.web.security;

import me.icymint.sloth.web.security.domain.Role;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface RoleRepository extends
		PagingAndSortingRepository<Role, String> {

}