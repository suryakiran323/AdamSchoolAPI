package com.stu.app.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.stu.app.config.Constants;
import com.stu.app.dto.StudentDTO;
import com.stu.app.exceptions.AccountsRTException;
import com.stu.app.model.Course;
import com.stu.app.model.Student;
import com.stu.app.model.Users;
import com.stu.app.repository.CourseRepo;
import com.stu.app.repository.StudentRepo;
import com.stu.app.repository.UsersRepo;

@Service
@Log4j2
public class StudentService {
	@Autowired
	StudentRepo studentRepo;
	@Autowired
	UsersRepo usersRepo;
	@Autowired
	CourseRepo courseRepo;
	
	public Object createStudent(@Valid StudentDTO postDTO,
			HttpServletRequest request) {
		log.info("starign create student");
		
		try{
			Student student = getStudentObject(postDTO);
			if(postDTO.getParentId()!=null){
				Users parent = usersRepo.findById(postDTO.getParentId()).get();
				if(parent==null){
					log.error("parent record not found with parentid:" + postDTO.getParentId());
					return Constants.Response.ERROR;
				}
				student.setParent(parent);
			}
			if(postDTO.getCourseId() !=null){
				Course course = courseRepo.findById(postDTO.getCourseId()).get();
				if(course == null){
					log.error("course record not found with courseID:" + postDTO.getCourseId());
					return Constants.Response.ERROR;
				}
				student.setCourse(course);
			}
			student.setStatus(Constants.Status.ACTIVE.getValue());
			studentRepo.save(student);
			return Constants.Response.OK;		
		}catch(Exception e){
			log.error(e);
			return Constants.Response.ERROR;
		}
	}

	private Student getStudentObject(StudentDTO postDTO) {
		Student stu = new Student();
		try {
			BeanUtils.copyProperties(stu, postDTO);
			stu.setCreateDtm(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stu;

	}

	private StudentDTO getStudentObject(Student  stu) {
		StudentDTO stuDto = new StudentDTO();
		try {
			BeanUtils.copyProperties(stuDto, stu);
			if(stu.getParent()!=null){
				stuDto.setParentId(stu.getParent().getId());
				stuDto.setParentName(stu.getParent().getFirstName() + " " + stu.getParent().getLastName());
			}
			if(stu.getCourse()!=null){
				stuDto.setCourseId(stu.getCourse().getId());
				stuDto.setCourseName(stu.getCourse().getName());
			}
			stuDto.setStatusStr(stu.getStatus()== 1? Constants.Status.ACTIVE.toString():Constants.Status.INACTIVE.toString()  );
			stuDto.setKey(stu.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stuDto;

	}
	
	public Object updateStudent(@Valid StudentDTO postDTO,
			HttpServletRequest request) {
		Student student = studentRepo.findById(postDTO.getKey()).get();
		if (student == null) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.UserDoesNotExists.toString());
		}
		try {
			BeanUtils.copyProperties(student, postDTO);
			if(postDTO.getParentId()!=null){
				Users parent = usersRepo.findById(postDTO.getParentId()).get();
				if(parent==null){
					log.error("parent record not found with parentid:" + postDTO.getParentId());
					return Constants.Response.ERROR;
				}
				student.setParent(parent);
			}
			if(postDTO.getCourseId() !=null){
				Course course = courseRepo.findById(postDTO.getCourseId()).get();
				if(course == null){
					log.error("course record not found with courseID:" + postDTO.getCourseId());
					return Constants.Response.ERROR;
				}
				student.setCourse(course);
			}
			student.setUpdateDtm(new Date());
			
			studentRepo.save(student);
		} catch (IllegalAccessException e) {
			log.error(e);
		} catch (InvocationTargetException e) {
			log.error(e);
		}		
		return Constants.Response.OK;
	}

	public StudentDTO getStudentById(Integer id, HttpServletRequest request) {
		Student student = studentRepo.findById(id).get();
		if (student == null) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.UserDoesNotExists.toString());
		}
		return getStudentObject(student);
	}

	public List<StudentDTO> getStudents(Integer parentId, String courseName, String name, HttpServletRequest request) {

		List<Student> students = studentRepo.findAllByParentId(parentId);
		List<StudentDTO> dtos = new ArrayList<StudentDTO> ();
		
		students.forEach(s ->{
			dtos.add(getStudentObject(s));
			
		});
		return dtos;
	}
	
}
