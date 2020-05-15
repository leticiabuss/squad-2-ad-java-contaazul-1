package com.javawomen.errorcenter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javawomen.errorcenter.model.Role;
 

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

	//ver se tem que fazer uma query aki
	Optional<Role> findByRoleName(String roleName);	

}
