package com.stu.app.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stu.app.model.Messages;

@Repository
public interface MessagesRepo extends JpaRepository<Messages, Integer>{


	@Query("from Messages where ((fromUserId.id= :userId and toUserId.id=:touserId) or (fromUserId.id= :touserId and toUserId=:userId)) order by createDtm")
	List<Messages> findAllByToUserId(@Param("touserId") Integer touserId, @Param("userId") Integer userId, Pageable pageable);

	@Query("from Messages where ((fromUserId.id= :userId and toUserId.id=:touserId) or (fromUserId.id= :touserId and toUserId=:userId)) and unread=:unread order by createDtm")
	List<Messages> findAllByToUserId(@Param("touserId") Integer touserId, @Param("userId") Integer userId, @Param("unread") Boolean read, Pageable pageable);

	@Query("from Messages where student.id=:stuid order by createDtm desc")
	List<Messages> getAllStudentFeedbacks(@Param("stuid") Integer stuid, Pageable pageable);



}
