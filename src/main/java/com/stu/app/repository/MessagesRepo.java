package com.stu.app.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stu.app.model.Messages;

@Repository
public interface MessagesRepo extends JpaRepository<Messages, Integer>{


	@Query("from Messages where (fromUserId.id= :userId or toUserId=:userId) order by createDtm desc")
	List<Messages> findAllByToUserId(@Param("userId") Integer userId, Pageable pageable);

	@Query("from Messages where (fromUserId.id= :userId or toUserId=:userId) and unread=:unread order by createDtm desc")
	List<Messages> findAllByToUserId(@Param("userId") Integer userId,@Param("unread") Boolean read, Pageable pageable);



}
