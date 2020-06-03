package com.stu.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.beanutils.BeanUtils;
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

import antlr.StringUtils;

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
import com.stu.app.model.Users;
import com.stu.app.repository.AddressRepo;
import com.stu.app.repository.CourseRepo;
import com.stu.app.repository.StudentRepo;
import com.stu.app.repository.UsersRepo;
import com.stu.app.security.TokenProvider;
import com.stu.app.security.UserRepoService;
import com.stu.app.util.AesUtil;
import com.stu.app.util.AppResponse;

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
	AddressRepo addressRepo;
	@Autowired
	CourseRepo courseRepo;
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
			users.setPassword(passwordEncoder.encode(postDTO.getSecretkey()));
			
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
				//send Invitation mial to faculty from administrator
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
		if(type == null){
			dbUsers = usersRepo.findAllByStatus(Constants.Status.ACTIVE.toString());
		}else{
			dbUsers = usersRepo.findAllByTypeAndStatus(type, Constants.Status.ACTIVE.toString());
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
		if(user != null && user.getType().equals(Constants.User.FACULTY)){
			String password = AesUtil.random(6);
			user.setStatus(Constants.Status.ACTIVE.toString());
			if(org.apache.commons.lang3.StringUtils.isBlank(user.getPassword())){
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
		
		List<Users> parents = usersRepo.findAllByStatus(Constants.Status.PENDING.toString());
		List<StudentDTO> enroments = new ArrayList<>();
		parents.forEach(p->{
			enroments.add(getSignupDTO(p));
		});
		return enroments;
	}
	
	private StudentDTO getSignupDTO(Users parent) {
		Parent users = new Parent();
		StudentDTO student = new StudentDTO();

		try {
			BeanUtils.copyProperties(users, parent.getAddress());
			BeanUtils.copyProperties(users, parent);
			users.setKey(parent.getId());
			List<Student> stus =  studentRepo.findAllByParentAndStatus(parent, Constants.Status.PENDING.toString());
			if(stus.size()>0){
				BeanUtils.copyProperties(student, stus.get(0));
			}
			student.setParentInfo(users);
			return student;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	public Object acceptEnrolment(Integer parentId, String status) {
		
		Users user =  usersRepo.findById(parentId).get();
		if(user != null){
			user.setStatus(status);
			List<Student> stus = studentRepo.findAllByParentAndStatus(user, Constants.Status.PENDING.toString());
			stus.forEach(s->{
				s.setStatus(Constants.Status.ACTIVE.toString());
				studentRepo.save(s);
			});
			String pwd = AesUtil.random(6);
			user.setPassword(passwordEncoder.encode(pwd));
			usersRepo.save(user);
			
			notification.sendCredentials(user, pwd);
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
