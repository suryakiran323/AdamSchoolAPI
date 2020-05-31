package com.stu.app.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stu.app.model.Users;
@Repository
public interface UsersRepo  extends JpaRepository<Users, Integer>{

	boolean existsByEmail(String email);

	Users findByEmail(String email);

	List<Users> findAllByType(String type);
	
	List<Users> findAllByTypeAndStatus(String type, String status);
	//Optional<Users> findById(Integer id);

	List<Users> findAllByStatus(String string);
	
	
}
