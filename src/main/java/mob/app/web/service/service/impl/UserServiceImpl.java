package mob.app.web.service.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import mob.app.web.service.exceptions.UserServiceException;
import mob.app.web.service.io.entity.UserEntity;
import mob.app.web.service.io.repositories.UserRepository;
import mob.app.web.service.service.UserService;
import mob.app.web.service.shared.Utils;
import mob.app.web.service.shared.dto.AddressDTO;
import mob.app.web.service.shared.dto.UserDto;
import mob.app.web.service.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  Utils utils;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public UserDto createUser(UserDto user) {

    // First let's find out if user already exists in a datatabase
    if (userRepository.findByEmail(user.getEmail()) != null) {
      throw new RuntimeException("Record already exists!");
    }

    for (int i = 0; i < user.getAddresses().size(); i++) {
      AddressDTO address = user.getAddresses().get(i);
      address.setUserDetails(user);
      address.setAddressId(utils.generateAddressId(30));
      user.getAddresses().set(i, address);
    }

    /*
     * Fields in user must match fields in userEntity
     */
    // BeanUtils.copyProperties(user, userEntity);

    ModelMapper modelMapper = new ModelMapper();
    UserEntity userEntity = modelMapper.map(user, UserEntity.class);

    userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

    // Setting secured public user id
    String publicUserId = utils.generateUserId(30);

    userEntity.setUserId(publicUserId);

    UserEntity storedUserDetails = userRepository.save(userEntity);

    // BeanUtils.copyProperties(storedUserDetails, returnedValue);
    UserDto returnedValue = modelMapper.map(storedUserDetails, UserDto.class);

    return returnedValue;
  }

  @Override
  public UserDto getUser(String email) {
    UserEntity userEntity = userRepository.findByEmail(email);
    if (userEntity == null)
      throw new UsernameNotFoundException(email);
    UserDto returnedValue = new UserDto();
    BeanUtils.copyProperties(userEntity, returnedValue);
    return returnedValue;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity userEntity = userRepository.findByEmail(email);
    if (userEntity == null)
      throw new UsernameNotFoundException(email);
    return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
  }

  @Override
  public UserDto getUserByUserId(String userId) {
    UserDto userDto = new UserDto();
    UserEntity userEntity = userRepository.findByUserId(userId);
    if (userEntity == null)
      throw new UsernameNotFoundException("User with userid:" + userId + " not found");
    BeanUtils.copyProperties(userEntity, userDto);
    return userDto;
  }

  @Override
  public UserDto updateUser(String id, UserDto user) {
    UserDto returnValue = new UserDto();
    UserEntity userEntity = userRepository.findByUserId(id);
    if (userEntity == null)
      throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

    userEntity.setFirstName(user.getFirstName());
    userEntity.setLastName(user.getLastName());

    UserEntity updatedUser = userRepository.save(userEntity);
    BeanUtils.copyProperties(updatedUser, returnValue);
    return returnValue;
  }

  @Override
  public void deleteUser(String id) {
    UserEntity userEntity = userRepository.findByUserId(id);
    if (userEntity == null)
      throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

    userRepository.delete(userEntity);

  }

  @Override
  public List<UserDto> getUsers(int page, int limit) {

    List<UserDto> returnValue = new ArrayList<>();

    // if(page > 0)
    // page = page -1;

    Pageable pagableRequest = PageRequest.of(page, limit);

    Page<UserEntity> userPage = userRepository.findAll(pagableRequest);

    List<UserEntity> users = userPage.getContent();

    for (UserEntity userEntity : users) {
      UserDto userDto = new UserDto();
      BeanUtils.copyProperties(userEntity, userDto);
      returnValue.add(userDto);
    }

    return returnValue;
  }

@Override
public boolean verifyEmailToken(String token) {

  boolean returnValue = false;
		
		// find user by token
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
		
		if(userEntity != null) {
			boolean hasTokenExpired = Utils.hasTokenExpired(token);
			if(!hasTokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnValue = true;
			}
		}
		return returnValue;
}

}