package com.stu.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.stu.app.config.Constants;
import com.stu.app.dto.Parent;
import com.stu.app.dto.SignInResponseDTO;
import com.stu.app.dto.SignupDTO;
import com.stu.app.dto.StudentDTO;
import com.stu.app.dto.UserCredentialsDTO;
import com.stu.app.dto.UserDTO;
import com.stu.app.exceptions.AccountsRTException;
import com.stu.app.model.Address;
import com.stu.app.model.Student;
import com.stu.app.model.StudentSubject;
import com.stu.app.model.Subject;
import com.stu.app.model.Users;
import com.stu.app.repository.AddressRepo;
import com.stu.app.repository.CourseRepo;
import com.stu.app.repository.StudentRepo;
import com.stu.app.repository.StudentSubjectRepo;
import com.stu.app.repository.SubjectRepo;
import com.stu.app.repository.UsersRepo;
import com.stu.app.security.TokenProvider;
import com.stu.app.util.AesUtil;

@Service
@Log4j2
public class AuthenticationService {
	@Autowired
	UsersRepo usersRepo;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	TokenProvider tokenProvider;
	@Autowired
	StudentRepo studentRepo;
	@Autowired
	StudentSubjectRepo studentSubjectRepo;
	
	@Autowired
	AddressRepo addressRepo;
	@Autowired
	CourseRepo courseRepo;
	@Autowired
	SubjectRepo subjectRepo;
	@Autowired
	StudentService stuService;
	@Autowired
	NotificationService notification;
	
	public SignInResponseDTO userSignin(
			@Valid UserCredentialsDTO userCredentialsDTO,
			HttpServletRequest request) {
		log.info("#*****AuthenticationService.userSignin()*****start*****#");

		SignInResponseDTO signinResponseDTO = new SignInResponseDTO();
		Users user = null;
		if (userCredentialsDTO.getUsername() != null) {
			String emailId = userCredentialsDTO.getUsername();

			user = usersRepo.existsByEmail(emailId) ? usersRepo
					.findByEmail(emailId) : null;
			if (user == null)
				throw new AccountsRTException(HttpStatus.BAD_REQUEST,
						"EMAIL_NOT_EXIST");
		}
		Authentication authentication;
		String password = userCredentialsDTO.getPassword();

		if (passwordEncoder.matches(password, user.getPassword())) {
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user
							.getEmail(), password));
		} else {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST, "WRONG_PASSWORD");
		}
		// storing in redis
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwtToken = getToken(authentication, user);

		signinResponseDTO.setAccessToken(jwtToken);
		signinResponseDTO.setTokenType("Bearer");
		signinResponseDTO.setUserType(user.getType());
		return signinResponseDTO;
	}

	/**
	 * @param authentication
	 *            - to get the user principal
	 * @param userId
	 *            - is used as key to store the user token
	 * @return token as response
	 */
	public String getToken(Authentication authentication, Users user) {
		log.info("#*****authenticationservice.getToken*****start*****#");
		String jwtToken = null;
		try {
			jwtToken = tokenProvider.generateToken(authentication, user);
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return jwtToken;
	}


	private Users getUserObject(SignupDTO postDTO) {
		Users users = new Users();
		try {
			Address addr = new Address();
			BeanUtils.copyProperties(addr, postDTO);
			BeanUtils.copyProperties(users, postDTO);
			addressRepo.save(addr);
			users.setAddress(addr);
			
			users.setType(postDTO.getUsertype()); //Constants.User.FACULTY
			if(StringUtils.isNoneBlank(postDTO.getSecretkey())){
				users.setPassword(passwordEncoder.encode(postDTO.getSecretkey()));
			}
			Subject subject = subjectRepo.findByName(postDTO.getFacultySubject());
			users.setSubject(subject);
			users.setCreateDtm(new Date());
		} catch (Exception e) {
			log.error(e);
		}
		return users;

	}

	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object createUser(@Valid SignupDTO postDTO,
			HttpServletRequest request) {
		Users user = usersRepo.findByEmail(postDTO.getEmail());
		if (user != null) {
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					Constants.Response.UserExists.toString());
		}
		try{
			boolean sendInvite = false;
			if(postDTO.getSecretkey()==null){
				sendInvite = true;
				//postDTO.setSecretkey("adams@123");
			}
			Users users = getUserObject(postDTO);
			usersRepo.save(users);
			if(sendInvite){
				//send Invitation mail to faculty from administrator
				notification.sendFacultyMail(users);
			}else{
				// send confirmation mail verification as from signup
				notification.sendFacultyMail(users);
			}
			return Constants.Response.OK;
		
		}catch(Exception e){
			log.error(e);
			return Constants.Response.ERROR;
		}
	}

	/**
	 * @param request
	 * @return
	 */
	public List<UserDTO> getUsers(String type, HttpServletRequest request) {
		List<UserDTO> users = new ArrayList<>();
		List<Users> dbUsers = null;
		String subjectName = request.getParameter("subject");
		if(StringUtils.isBlank(subjectName)){
			dbUsers = usersRepo.findAllByTypeAndStatus(type, Constants.Status.ACTIVE.toString());
		}else{
			Subject subject = subjectRepo.findByName(subjectName);
			dbUsers = usersRepo.findAllByTypeAndSubjectAndStatus(type, subject, Constants.Status.ACTIVE.toString());
		}
		dbUsers.forEach(a->{
			users.add(getUserDTO(a));
		});
		return users;
	}

	private UserDTO getUserDTO(Users a) {
		UserDTO udto = new UserDTO();
		
		try{
			BeanUtils.copyProperties(udto, a);
			BeanUtils.copyProperties(udto, a.getAddress());
			udto.setKey(a.getId());
			udto.setType(null);
			if(a.getSubject()!=null)
				udto.setSubject(a.getSubject().getName());
		}catch(Exception e){
			log.error(e);
		}
		return udto;
	}

	public Object updateUser(@Valid UserDTO fData,
			HttpServletRequest request) {
		if(fData.getKey()!=null && fData.getKey() != 0){
			Users f =  usersRepo.findById(fData.getKey()).get();
			if( f==null)
				throw new AccountsRTException(HttpStatus.BAD_REQUEST,
						"UserDoesNotExists");
			Address addr = f.getAddress();
			try{
				fData.setType(f.getType());
				BeanUtils.copyProperties(f, fData);
				BeanUtils.copyProperties(addr, fData);
				addressRepo.save(addr);
				f.setUpdateDtm(new Date());
				
				usersRepo.save(f);
			}catch(Exception e){
				log.error(e);
			}
		}else{
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"InvalidData");
		}
		return Constants.Response.OK;
	}

	public Object getUserById(Integer uid, HttpServletRequest request) {
		if(uid ==null || uid == 0){
			uid = (Integer)request.getAttribute("userId");
		}
		if(uid !=null && uid != 0){
			Users user =  usersRepo.findById(uid).get();
			if( user==null)
					throw new AccountsRTException(HttpStatus.BAD_REQUEST,
							"UserDoesNotExists");
			UserDTO dto = getUserDTO(user);
			return dto;
		}else{
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"InvalidData");
		}
	}

	public String activeUser(Integer token, HttpServletRequest request) {
		Users user =  usersRepo.findById(token).get();
		if(user != null){
			String password = AesUtil.random(6);
			user.setStatus(Constants.Status.ACTIVE.toString());
			if(StringUtils.isBlank(user.getPassword())){
				user.setPassword(passwordEncoder.encode(password));
			}else{
				password = "[hidden]";
			}
			usersRepo.save(user);
			notification.sendCredentials(user, password);
			return "<h2>Your account is activated successfuly, check your mail for credentials</h2>";
		}
		return "<h1>Invalid Token or link got expired.</h1>";
	}

	public Object parentEnrole(@Valid StudentDTO postDTO, HttpServletRequest request) {
		return stuService.createStudent(postDTO, request, Constants.Status.PENDING.toString());
	}

	public Object getEnrolements() {
		
		//List<Users> parents = usersRepo.findAllByStatus(Constants.Status.PENDING.toString());
		List<Map<String, Object>> studentSub = studentSubjectRepo.findpendingSubjects();//(Constants.Status.PENDING.toString());
		List<StudentDTO> enroments = new ArrayList<>();
		studentSub.forEach(m->{
			Student student = studentRepo.findById((Integer)m.get("id")).get();
			enroments.add(getSignupDTO(student, m.get("subjects").toString()));
		});
		return enroments;
	}

	private StudentDTO getSignupDTO(Student stu, String subjects) {
		Parent users = new Parent();
		StudentDTO student = new StudentDTO();

		try {
			//Student stu = stuSubject.getStudent();
			//BeanUtils.copyProperties(users, parent.getParent().getAddress());
			BeanUtils.copyProperties(users, stu.getParent());
			users.setKey(stu.getParent().getId());
			BeanUtils.copyProperties(student, stu);
			student.setParentInfo(users);
			student.setKey(stu.getId());
			student.setSubject(subjects);
			student.setCourseName(stu.getCourse().getName());
			return student;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}
	public Object acceptEnrolment(Integer stuId) {
		Student student = studentRepo.findById(stuId).get();
		
		Users user =  student.getParent();
		if(user != null){
			
			student.setStatus(Constants.Status.ACTIVE.toString());
			studentRepo.save(student);
			List<StudentSubject> studentSubjects = studentSubjectRepo.findByStudent(student);
			
			studentSubjects.forEach(ss->{
				ss.setStatus(Constants.Status.ACTIVE.toString());;
				studentSubjectRepo.save(ss);
			});
			//activating parent if its in pending state
			if(user.getStatus().equals(Constants.Status.PENDING.toString())){
				user.setStatus(Constants.Status.ACTIVE.toString());
				String pwd = AesUtil.random(6);
				user.setPassword(passwordEncoder.encode(pwd));
				usersRepo.save(user);
				
				notification.sendCredentials(user, pwd);
			}
			return Constants.Response.OK;
		}
		return Constants.Response.ERROR;
	}
	

	public Object forgotPassword(String email) {
		Users user = usersRepo.findByEmail(email);
		if(user == null){
			throw new AccountsRTException(HttpStatus.BAD_REQUEST,
					"UserDoesNotExists");
		}
		String password = AesUtil.random(6);
		user.setPassword(passwordEncoder.encode(password));
		notification.sendResetPassword(email, user.getFirstName(),  password);
		usersRepo.save(user);
		return "Check your Email, email sent with password";
	}
	
}
