package com.stu.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stu.app.config.Constants;
import com.stu.app.dto.ExamDTO;
import com.stu.app.dto.ResultsDTO;
import com.stu.app.dto.StudentDTO;
import com.stu.app.exceptions.AccountsRTException;
import com.stu.app.service.StudentService;
import com.stu.app.util.AppResponse;
import com.stu.app.util.Validations;

/**
 * @author Admin
 *
 */
@RestController
@RequestMapping("/api/stu/")
public class StudentController {

	@Autowired
	Validations validations;

	@Autowired
	StudentService studentService;

	/**
	 * @param postDTO
	 * @param request
	 * @return
	 */
	@PostMapping("create")
	public ResponseEntity<AppResponse> createStudent(
			@Valid @RequestBody StudentDTO postDTO, HttpServletRequest request) {
		validations.validateStudentObj(postDTO);

		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				studentService.createStudent(postDTO, request, Constants.Status.ACTIVE.toString())), HttpStatus.OK);
	}

	/**
	 * @param postDTO
	 * @param request
	 * @return
	 */
	@PutMapping("student")
	public ResponseEntity<AppResponse> updateStudent(
			@Valid @RequestBody StudentDTO postDTO, HttpServletRequest request) {
		validations.validateStudentObj(postDTO);
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				studentService.updateStudent(postDTO, request)), HttpStatus.OK);
	}

	/**
	 * @param request
	 * @return
	 */
	@GetMapping("student/{userId}")
	public ResponseEntity<AppResponse> getStudentId(
			@PathVariable("userId") Integer id, HttpServletRequest request) {
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				studentService.getStudentById(id, request)), HttpStatus.OK);
	}

	/**
	 * @param parentId
	 * @param courseName
	 * @param name
	 * @param request
	 * @return
	 */
	@GetMapping("students/{parentId}")
	public ResponseEntity<AppResponse> getStudents(
			@PathVariable("parentId") Integer parentId,
			@RequestParam(required = false, name = "clourseName") String courseName,
			@RequestParam(required = false, name = "name") String name,
			HttpServletRequest request) {
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				studentService.getStudents(parentId, courseName + '%',
						name + '%', request)), HttpStatus.OK);
	}
	
	/**
	 * @param parentId
	 * @param courseName
	 * @param name
	 * @param request
	 * @return
	 */
	@GetMapping("studentsbyparent")
	public ResponseEntity<AppResponse> getStudentsByParentLogin(
			@RequestParam(required = false, name = "clourseName") String courseName,
			@RequestParam(required = false, name = "name") String name,
			HttpServletRequest request) {
		Integer userId = (Integer)request.getAttribute("userId");
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				studentService.getStudents(userId, courseName + '%',
						name + '%', request)), HttpStatus.OK);
	}

	/**
	 * @param courseName
	 * @param name
	 * @param request
	 * @return
	 */
	@GetMapping("students")
	public ResponseEntity<AppResponse> getStudents(
			@RequestParam(required = false, name = "course") String courseName,
			@RequestParam(required = false, name = "name", defaultValue="") String name,
			HttpServletRequest request) {
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				studentService.getStudents(courseName + '%',
						name + '%', request)), HttpStatus.OK);
	}
	
	// Exam CRUD operations from here

	/**
	 * @param examDTO
	 * @param request
	 * @return
	 */
	@PostMapping("exam")
	public ResponseEntity<AppResponse> examCreate(
			@Valid @RequestBody ExamDTO examDTO, HttpServletRequest request) {
		try {
			validations.validateExamObj(examDTO);
			return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
					studentService.examCreate(examDTO, request)), HttpStatus.OK);
		} catch (AccountsRTException ex) {
			return new ResponseEntity<>(new AppResponse(AppResponse.FAIL,
					ex.getMessage()), ex.getHttpStatus());
		}
	}

	@PutMapping("exam")
	public ResponseEntity<AppResponse> examUpdate(
			@Valid @RequestBody ExamDTO examDTO, HttpServletRequest request) {
		try {
			validations.validateExamObj(examDTO);
			return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
					studentService.examUpdate(examDTO, request)), HttpStatus.OK);
		} catch (AccountsRTException ex) {
			return new ResponseEntity<>(new AppResponse(AppResponse.FAIL,
					ex.getMessage()), ex.getHttpStatus());
		}
	}

	@GetMapping("exam/{id}")
	public ResponseEntity<AppResponse> getExam(@PathVariable("id") Integer id,
			HttpServletRequest request) {
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				studentService.getExamDetails(id, request)), HttpStatus.OK);
	}

	@GetMapping("exambycls/{classid}")
	public ResponseEntity<AppResponse> getExams(
			@PathVariable("classid") Integer id, HttpServletRequest request) {
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				studentService.getExamByClassId(id, request)), HttpStatus.OK);
	}

	// Exam Results CRUD operations from here

	@PostMapping("stumarks")
	public ResponseEntity<AppResponse> addStudentMarks(
			@Valid @RequestBody ResultsDTO marksDTO, HttpServletRequest request) {
		try {
			validations.validateResultsDTO(marksDTO);
			return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
					studentService.addStudentMarks(marksDTO, request)), HttpStatus.OK);
		} catch (AccountsRTException ex) {
			return new ResponseEntity<>(new AppResponse(AppResponse.FAIL,
					ex.getMessage()), ex.getHttpStatus());
		}
	}

	@PutMapping("stumarks") 
	public ResponseEntity<AppResponse> updateStudentMarks(
			@Valid @RequestBody ResultsDTO marksDTO, HttpServletRequest request) {
		try {
			validations.validateResultsDTO(marksDTO);
			return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
					studentService.updateStudentMarks(marksDTO, request)), HttpStatus.OK);
		} catch (AccountsRTException ex) {
			return new ResponseEntity<>(new AppResponse(AppResponse.FAIL,
					ex.getMessage()), ex.getHttpStatus());
		}
	}
	
	@GetMapping("stumarks/{stuid}")
	public ResponseEntity<AppResponse> getStuMarks(@PathVariable("stuid") Integer id, @RequestParam(name ="examId", required=false)Integer examId , HttpServletRequest request) {
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				studentService.getStudentMarks(id, examId, request)), HttpStatus.OK);
	}
	
	@GetMapping("sturank/{stuid}/{examid}")
	public ResponseEntity<AppResponse> getStuRank(@PathVariable("stuid") Integer id ,@PathVariable("examid") Integer examId , HttpServletRequest request) {
		return new ResponseEntity<>(new AppResponse(AppResponse.SUCCESS,
				studentService.getStuRank(id, examId, request)), HttpStatus.OK);
	}
}
