package com.stu.app.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stu.app.config.Constants;
import com.stu.app.dto.ExamDTO;
import com.stu.app.dto.Parent;
import com.stu.app.dto.ResultsDTO;
import com.stu.app.dto.StudentDTO;
import com.stu.app.exceptions.AccountsRTException;
import com.stu.app.model.Address;
import com.stu.app.model.Course;
import com.stu.app.model.ExamDetails;
import com.stu.app.model.ExamResults;
import com.stu.app.model.Student;
import com.stu.app.model.StudentSubject;
import com.stu.app.model.Subject;
import com.stu.app.model.Users;
import com.stu.app.repository.AddressRepo;
import com.stu.app.repository.CourseRepo;
import com.stu.app.repository.ExamDetailsRepo;
import com.stu.app.repository.ExamResultsRepo;
import com.stu.app.repository.StudentRepo;
import com.stu.app.repository.StudentSubjectRepo;
import com.stu.app.repository.SubjectRepo;
import com.stu.app.repository.UsersRepo;
import com.stu.app.util.AesUtil;
import com.stu.app.util.AppResponse;


/**
 * @author Admin
 *
 */
@Service
@Log4j2
public class StudentService {
	@Autowired
	StudentRepo studentRepo;
	@Autowired
	UsersRepo usersRepo;
	@Autowired
	CourseRepo courseRepo;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	ExamDetailsRepo examDetailsRepo;

	@Autowired
	ExamResultsRepo examResultsRepo;
	@Autowired
	AddressRepo addressRepo;
	
	@Autowired
	StudentSubjectRepo studentSubjectRepo;
	
	@Autowired
	SubjectRepo subjectRepo;
	
	@Autowired
	NotificationService notification;
	@Autowired
	MsgService msgService;
	
	//String dummyPwd = "adams@123";
	private Users getParentObject(StudentDTO postDTO) {
		Users users = new Users();
		try {
			Address addr = new Address();
			if(postDTO!=null && postDTO.getParentInfo()!=null){
				BeanUtils.copyProperties(addr, postDTO.getParentInfo());
				BeanUtils.copyProperties(users, postDTO.getParentInfo());
				addressRepo.save(addr);
				users.setAddress(addr);
			}
			
			users.setType(Constants.User.PARENT); //Constants.User.FACULTY
			users.setPassword(passwordEncoder.encode(AesUtil.random(6)));
			users.setStatus(Constants.Status.PENDING.toString());
			users.setCreateDtm(new Date());
		} catch (Exception e) {
			log.error(e);
		}
		return users;

	}
	public Object createStudent(@Valid StudentDTO postDTO,
			HttpServletRequest request, String status) {
		log.info("starign create student");
		boolean created = false;
		try {
			Student student = getStudentObject(postDTO);
			if (postDTO.getCourseId() != null) {
				Course course = courseRepo.findById(postDTO.getCourseId())
						.get();
				if (course == null) {
					log.error("course record not found with courseID:"
							+ postDTO.getCourseId());
					return Constants.Response.ERROR;
				}
				student.setCourse(course);
			}
			if (postDTO.getParentId() != null) {
				Users parent = usersRepo.findById(postDTO.getParentId()).get();
				if (parent == null) {
					log.error("parent record not found with parentid:"
							+ postDTO.getParentId());
					return Constants.Response.ERROR;
				}
				student.setParent(parent);
			}else if (postDTO.getParentInfo() != null) {
				Users parent = usersRepo.findByEmail(postDTO.getParentInfo().getEmail());
				if(parent ==null ){
					parent  = getParentObject(postDTO);
					parent.setStatus(status);
					usersRepo.save(parent);
					created = true;
				}
				student.setParent(parent);				
			}else{
				log.error("parent data not found");
				return Constants.Response.ERROR;
			}
			student.setStatus(status); 
			studentRepo.save(student);
			//adding subjects to parent
			if(postDTO.getSubjects()!=null && postDTO.getSubjects().size()>0){
				Set<StudentSubject> studentSubs = new HashSet<>();
				postDTO.getSubjects().forEach(s->{
					Subject subject = subjectRepo.findByName(s);
					
					StudentSubject ss = new StudentSubject();
					ss.setStudent(student);
					ss.setSubject(subject);
					ss.setStatus(status);
					studentSubjectRepo.save(ss);
					studentSubs.add(ss);
				});
				student.setStudentSubjects(studentSubs);
			}			
			//sending email
			if(created){
				notification.sendToParentMail(student);
			}else{
				notification.sendToParentStudentAdded(student);
			}
			return Constants.Response.OK;
		} catch (Exception e) {
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

	private StudentDTO getStudentObject(Student stu) {
		StudentDTO stuDto = new StudentDTO();
		try {
			BeanUtils.copyProperties(stuDto, stu);
			if (stu.getParent() != null) {
				stuDto.setParentId(stu.getParent().getId());
				Parent parent = new Parent();
				BeanUtils.copyProperties(parent, stu.getParent());
				stuDto.setParentInfo(parent);
				
			}
			if (stu.getCourse() != null) {
				stuDto.setCourseId(stu.getCourse().getId());
				stuDto.setCourseName(stu.getCourse().getName());
			}	
			List<StudentSubject> studentSubjects = studentSubjectRepo.findByStudent(stu);
			
			if(studentSubjects !=null && studentSubjects.size()>0){
				List<String> subs = new ArrayList<>();
				studentSubjects.forEach(s->{
					subs.add(s.getSubject().getName());
				});
				stuDto.setSubjects(subs);
			}
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
			if (postDTO.getParentId() != null) {
				Users parent = usersRepo.findById(postDTO.getParentId()).get();
				if (parent == null) {
					log.error("parent record not found with parentid:"
							+ postDTO.getParentId());
					return Constants.Response.ERROR;
				}
				student.setParent(parent);
			}
			if (postDTO.getCourseId() != null) {
				Course course = courseRepo.findById(postDTO.getCourseId())
						.get();
				if (course == null) {
					log.error("course record not found with courseID:"
							+ postDTO.getCourseId());
					return Constants.Response.ERROR;
				}
				student.setCourse(course);
			}
			student.setUpdateDtm(new Date());
			//adding subjects to parent
			if(postDTO.getSubjects()!=null && postDTO.getSubjects().size()>0){
				Set<StudentSubject> studentSubs = new HashSet<>();
				postDTO.getSubjects().forEach(s->{
					Subject subject = subjectRepo.findByName(s);
					
					StudentSubject ss = new StudentSubject();
					ss.setStudent(student);
					ss.setSubject(subject);
					//studentSubjectRepo.save(ss);
					studentSubs.add(ss);
				});
				student.setStudentSubjects(studentSubs);
			}
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

	public List<StudentDTO> getStudents(Integer parentId, String courseName,
			String name, HttpServletRequest request) {
		Users parent = usersRepo.findById(parentId).get();
		List<Student> students = studentRepo.findAllByParentAndStatus(parent, Constants.Status.ACTIVE.toString());
		List<StudentDTO> dtos = new ArrayList<StudentDTO>();
		students.forEach(s -> {
			dtos.add(getStudentObject(s));

		});
		return dtos;
	}
	
	public List<StudentDTO> getStudents(String courseName, String name, HttpServletRequest request) {
		
		List<StudentDTO> dtos = new ArrayList<StudentDTO>();
		if(request.getAttribute("userType").equals(Constants.User.FACULTY.toString())){
			log.info("userType::"+request.getAttribute("userType"));
			Users faculty = usersRepo.findById(Integer.parseInt(request.getAttribute("userId").toString())).get();
			List<Student> students = studentRepo.getStudentsByCoursename(faculty.getSubject().getId(), courseName, name);
			students.forEach(s -> {
				dtos.add(getStudentObject(s));
			});
		}else{
			List<Student> students = studentRepo.getStudentsByCoursename(courseName, name);
			students.forEach(s -> {
				dtos.add(getStudentObject(s));
			});
		}
		return dtos;
	}
	public Object examCreate(@Valid ExamDTO examDTO, HttpServletRequest request) {
		// duplicate check
		ExamDetails existingDetails = examDetailsRepo
				.findExamDetailBynameClassId(examDTO.getName(),
						examDTO.getClassId(), examDTO.getConductDtm());
		if (existingDetails != null) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.RecordExists.toString());
		}
		Course course = courseRepo.findById(examDTO.getClassId()).get();
		if (course == null)
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.ERROR.toString());
		Integer uid = (Integer)request.getAttribute("userId");
		Users faculty = usersRepo.findById(uid).get();
		ExamDetails newexam = new ExamDetails();
		try {
			BeanUtils.copyProperties(newexam, examDTO);
			newexam.setCourse(course);
			newexam.setCreateDtm(new Date());
			newexam.setFaculty(faculty);
			newexam.setSubject(faculty.getSubject());
			examDetailsRepo.save(newexam);

			return Constants.Response.OK;
		} catch (IllegalAccessException e) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.ERROR.toString());
		} catch (InvocationTargetException e) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.ERROR.toString());
		}

	}

	public Object examUpdate(@Valid ExamDTO examDTO, HttpServletRequest request) {
		// duplicate check
		ExamDetails existingDetails = examDetailsRepo
				.findExamDetailBynameClassId(examDTO.getName(),
						examDTO.getClassId(), examDTO.getConductDtm());
		if (existingDetails != null
				&& examDTO.getKey() != existingDetails.getId()) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.RecordExists.toString());
		}
		Course course = courseRepo.findById(examDTO.getClassId()).get();
		if (course == null)
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.ERROR.toString());

		ExamDetails newexam = examDetailsRepo.findById(examDTO.getKey()).get();
		Integer uid = (Integer)request.getAttribute("userId");
		Users faculty = usersRepo.findById(uid).get();
		try {
			BeanUtils.copyProperties(newexam, examDTO);
			newexam.setCourse(course);
			newexam.setCreateDtm(new Date());
			newexam.setFaculty(faculty);
			newexam.setSubject(faculty.getSubject());
			examDetailsRepo.save(newexam);
			return Constants.Response.OK;
		} catch (IllegalAccessException e) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.ERROR.toString());
		} catch (InvocationTargetException e) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.ERROR.toString());
		}
	}

	private ExamDTO getExamDTO(ExamDetails newexam) {
		try {
			ExamDTO examDTO = new ExamDTO();
			BeanUtils.copyProperties(examDTO, newexam);
			examDTO.setKey(newexam.getId());
			if (newexam.getCourse() != null) {
				examDTO.setClassId(newexam.getCourse().getId());
				examDTO.setClassName(newexam.getCourse().getName());
			}
			if(newexam.getSubject() != null){
				examDTO.setSubject(newexam.getSubject().getName());
			}
			examDTO.setCreateDtm(null);
			
			return examDTO;
		} catch (IllegalAccessException e) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.ERROR.toString());
		} catch (Exception e) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.ERROR.toString());
		}
	}

	public Object getExamDetails(Integer id, HttpServletRequest request) {
		Optional<ExamDetails> newexam = examDetailsRepo.findById(id);
		if (!newexam.isPresent()) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.ERROR.toString());
		}
		return getExamDTO(newexam.get());
	}

	public Object getExamByClassId(Integer classid, HttpServletRequest request) {
		Integer uid = (Integer)request.getAttribute("userId");
		String utype = (String)request.getAttribute("userType");
		//Users faculty = usersRepo.findById(uid).get();
		List<ExamDetails> exams;
		if(utype.equals(Constants.User.FACULTY)){
			exams = examDetailsRepo.getExamsByCourseId(classid, uid);
		}else{
			exams = examDetailsRepo.getExamsByCourseId(classid);
		}
		List<ExamDTO> list = new ArrayList<>();
		exams.forEach(e -> {
			list.add(getExamDTO(e));
		});
		return list;
	}

	public Object addStudentMarks(@Valid ResultsDTO marksDTO, HttpServletRequest request) {
		Optional<Student> stu = studentRepo.findById(marksDTO.getStudentId());
		if(!request.getAttribute("userType").equals(Constants.User.FACULTY.toString())){
			log.info("Loggedin userType::"+request.getAttribute("userType"));			
		}
		if(!stu.isPresent()){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.UserDoesNotExists.toString());
		}
		Student student = stu.get();
		Optional<ExamDetails> exam = examDetailsRepo.findById(marksDTO.getExamId());
		
		if(!exam.isPresent())
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"Exam/Test does not exists");
		ExamDetails examDetails = exam.get();
		boolean hasExam = false;
		List<StudentSubject> studentSubjects = studentSubjectRepo.findByStudent(student);
		for (StudentSubject studentSubject : studentSubjects) {
			if(studentSubject.getSubject().getId() == examDetails.getSubject().getId())
				hasExam = true;
		}
		if(!hasExam){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"Invalid exam for the student.");
		}
		ExamResults resultsExits = examResultsRepo.findByStudentAndExamDetails(student, examDetails);
		if(resultsExits!=null){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"Marks already exists for the student, contact Admin to edit marks for any descripency");
		}
		try{
			Users faculty = usersRepo.findById(Integer.parseInt(request.getAttribute("userId").toString())).get();
			ExamResults results = new ExamResults();
	
			results.setStudent(student);
			results.setExamDetails(examDetails);
			results.setCreateDtm(new Date());
			results.setSubject(faculty.getSubject());
			results.setMarks(marksDTO.getMarks());
			examResultsRepo.save(results);
			if(StringUtils.isNoneBlank(marksDTO.getFeedback())){
			//adding feedback to parent
				msgService.addMessges(faculty, student.getParent(), student, marksDTO.getFeedback());
			}
			return AppResponse.SUCCESS;
		}catch(Exception e){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					e.getMessage().toString());
		}		
	}

	public Object getStudentMarks(Integer studentId, Integer examId, String subject, HttpServletRequest request) {
		//Student stu = studentRepo.findById(studentId).get();
		List<ExamResults> marks = null;
		if(examId!=null && examId>0){
			ExamResults marksObj = examResultsRepo.findExamResults(studentId, examId);
			if(marksObj!=null){
				return getExamResultDTO(marksObj);
			}
			return "No Results found";
		}else{
			List<ResultsDTO> resutls = new ArrayList<ResultsDTO>();
			marks = examResultsRepo.findExamResultsBySubjectId(studentId, subject+"%");
			if(marks.size()>0){
				marks.forEach(s->{
					resutls.add(getExamResultDTO(s));
				});
			}
			return resutls;
		}		
	}

	private ResultsDTO getExamResultDTO(ExamResults marksObj) {
		ResultsDTO results = new ResultsDTO();
			results.setExamName(marksObj.getExamDetails().getName());
			results.setStudentName(marksObj.getStudent().getFirstName() + " " + marksObj.getStudent().getLastName());
			results.setConductDtm(marksObj.getExamDetails().getConductDtm());
			results.setSubjectId(marksObj.getSubject().getId());
			results.setMarks(marksObj.getMarks());
		return results;
	}

	public Object updateStudentMarks(@Valid ResultsDTO marksDTO, HttpServletRequest request) {
		Optional<ExamResults> resultOpt = examResultsRepo.findById(marksDTO.getKey());
		if(!resultOpt.isPresent()){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"Invalid update request");
		}
		ExamResults results = resultOpt.get();
		Optional<Student> stu = studentRepo.findById(marksDTO.getStudentId());
		if(!stu.isPresent()){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.UserDoesNotExists.toString());
		}
		Student student = stu.get();
		Optional<ExamDetails> exam = examDetailsRepo.findById(marksDTO.getExamId());
		if(!exam.isPresent())
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"Exam/Test does not exists");
		ExamResults resultsExits = examResultsRepo.findByStudentAndExamDetails(stu.get(), exam.get());
		if(resultsExits!=null && resultsExits.getId() != results.getId()){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"Marks already exists for the student, contact Admin to edit marks for any descripency");
		}
		Users faculty = usersRepo.findById(Integer.parseInt(request.getAttribute("userId").toString())).get();
		try{			
			results.setStudent(student);
			results.setExamDetails(exam.get());
			results.setCreateDtm(new Date());
			results.setSubject(faculty.getSubject());
			results.setMarks(marksDTO.getMarks());
			examResultsRepo.save(results);		
			return AppResponse.SUCCESS;
		}catch(Exception e){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					e.getMessage().toString());
		}		
	}
	public Object getStuRank(Integer id, Integer examId, HttpServletRequest request) {
			List<ResultsDTO> resutls = new ArrayList<>();	
		List<ExamResults> marks = null;
			if(examId!=null && examId>0){
				marks = examResultsRepo.getTopStuResults(examId, PageRequest.of(0, 60));
				Integer myrank = 0;
				if(marks!=null && marks.size()>0){
					for (ExamResults s : marks) {
						myrank++;
						if(resutls.size()==6){
							break;
						}
						ResultsDTO dto = new ResultsDTO();
						dto.setExamName(s.getExamDetails().getName());
						dto.setStudentName(s.getStudent().getFirstName() + " " + s.getStudent().getLastName());
						dto.setRank(myrank);
						dto.setMarks(s.getMarks());
						if(resutls.size()<5 || s.getStudent().getId() == id){
							resutls.add(dto);							
						}else{
							continue;
						}
					}
				}				
				return resutls;
			}
			return AppResponse.FAIL;
	}
	

}
