package com.stu.app.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stu.app.model.Student;
import com.stu.app.model.StudentSubject;

@Repository
public interface StudentSubjectRepo extends JpaRepository<StudentSubject, Integer>{

	List<StudentSubject> findAllByStatus(String string);
	@Query(nativeQuery=true, value="SELECT s.id as id, GROUP_CONCAT(sub.name SEPARATOR ',') as subjects FROM Student s LEFT JOIN student_subject ss ON ss.studentid = s.id"
			+" LEFT JOIN `subject` sub ON sub.id = ss.subjectid  WHERE ss.status='PENDING' group by s.id")
	List<Map<String, Object>> findpendingSubjects();
	
	List<StudentSubject> findByStudent(Student student);
	//List<StudentSubject> findAllByStudent(Student stu);
	
}
 