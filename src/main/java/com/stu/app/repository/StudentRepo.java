package com.stu.app.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stu.app.model.Student;
import com.stu.app.model.Users;
 
@Repository
public interface StudentRepo extends JpaRepository<Student, Integer> {
 
	List<Student> findAllByParent(Integer parentId);
	List<Student> findAllByParentAndStatus(Users parent, String status);
	@Query("from Student s where s.parent.id=:parentId and s.course.name like :courseName and (s.firstName like :name or s.lastName like :name) and status='ACTIVE'")
	List<Student> fiterStudents(@Param("parentId") Integer parentId, @Param("courseName") String courseName, @Param("name") String name);

	@Query("from Student s where s.course.name like :courseName and (s.firstName like :name or s.lastName like :name) and status='ACTIVE'")
	List<Student> getStudentsByCoursename(@Param("courseName") String courseName, @Param("name") String name);
	

}