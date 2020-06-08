package com.stu.app.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stu.app.model.ExamDetails;
import com.stu.app.model.ExamResults;
import com.stu.app.model.Student;

@Repository
public interface ExamResultsRepo extends JpaRepository<ExamResults, Integer> {
	
	@Query("from ExamResults where student.id = :studentId and examDetails.id=:examId")
	ExamResults findExamResults(@Param("studentId")Integer studentId, @Param("examId") Integer examId);

	@Query("from ExamResults where student.id = :studentId Order by examDetails.id")
	List<ExamResults> findExamResults(@Param("studentId")Integer studentId);

	ExamResults findByStudentAndExamDetails(Student student, ExamDetails examDetails);
	
	@Query("from ExamResults where examDetails.id =:examId Order by marks desc")
	List<ExamResults> getTopStuResults(@Param("examId") Integer examId, Pageable pageable);
	
	@Query("from ExamResults where student.id = :studentId and examDetails.subject.name like :subject Order by createDtm")
	List<ExamResults> findExamResultsBySubjectId(@Param("studentId")Integer studentId, @Param("subject") String subject);
	
}
