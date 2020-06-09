package com.stu.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stu.app.model.ExamDetails;

@Repository
public interface ExamDetailsRepo extends JpaRepository<ExamDetails, Integer> {
	
	@Query("from ExamDetails where course.id =:classId and name=:name and conductDtm = :cdtm")
	ExamDetails findExamDetailBynameClassId(@Param("name") String name, @Param("classId") Integer classId,
			@Param("cdtm")Date conductDtm);
	
	@Query("from ExamDetails where course.id =:id and faculty.id=:uid")
	List<ExamDetails> getExamsByCourseId(@Param("id") Integer classId, @Param("uid") Integer uid);
	
	@Query("from ExamDetails where course.id =:id")
	List<ExamDetails> getExamsByCourseId(@Param("id") Integer classId);

}
