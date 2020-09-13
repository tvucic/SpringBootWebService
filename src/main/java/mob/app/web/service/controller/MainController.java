package mob.app.web.service.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mob.app.web.service.exceptions.UserServiceException;
import mob.app.web.service.service.AddressService;
import mob.app.web.service.service.UserService;
import mob.app.web.service.shared.dto.AddressDTO;
import mob.app.web.service.shared.dto.UserDto;
import mob.app.web.service.ui.model.request.UserDetailsRequestModel;
import mob.app.web.service.ui.model.response.AddressesRest;
import mob.app.web.service.ui.model.response.ErrorMessages;
import mob.app.web.service.ui.model.response.OperationStatusModel;
import mob.app.web.service.ui.model.response.RequestOperationName;
import mob.app.web.service.ui.model.response.RequestOperationStatus;
import mob.app.web.service.ui.model.response.UserRest;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class MainController {

  @Autowired
  private UserService userService;

  @Autowired
  AddressService addressesService;

  @Autowired
  AddressService addressService;

  @GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })

  public UserRest getUser(@PathVariable String id) {
    UserRest returnValue = new UserRest();
    UserDto userDto = userService.getUserByUserId(id);
    BeanUtils.copyProperties(userDto, returnValue);
    return returnValue;
  }

  @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, produces = {
      MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })

  public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
    UserRest returnedValue = new UserRest();

    if (userDetails.getFirstName().isEmpty())
      throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

    // UserDto userDto = new UserDto();
    // BeanUtils.copyProperties(userDetails, userDto);

    ModelMapper modelMapper = new ModelMapper();
    UserDto userDto = modelMapper.map(userDetails, UserDto.class);

    UserDto createdUser = userService.createUser(userDto);
    // BeanUtils.copyProperties(createdUser, returnedValue);

    returnedValue = modelMapper.map(createdUser, UserRest.class);

    return returnedValue;
  }

  @PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
          MediaType.APPLICATION_XML_VALUE })
  public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
    UserRest returnedValue = new UserRest();

    UserDto userDto = new UserDto();

    BeanUtils.copyProperties(userDetails, userDto);
    UserDto updatedUser = userService.updateUser(id, userDto);

    BeanUtils.copyProperties(updatedUser, returnedValue);
    return returnedValue;
  }

  @DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })

  public OperationStatusModel deleteUser(@PathVariable String id) {

    OperationStatusModel returnValue = new OperationStatusModel();
    returnValue.setOperationName(RequestOperationName.DELETE.name());

    userService.deleteUser(id);
    returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
    return returnValue;

  }

  @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "limit", defaultValue = "25") int limit) {
    List<UserRest> returnValue = new ArrayList<>();

    List<UserDto> users = userService.getUsers(page, limit);

    for (UserDto userDto : users) {
      UserRest userModel = new UserRest();
      BeanUtils.copyProperties(userDto, userModel);
      returnValue.add(userModel);
    }

    return returnValue;
  }

  // http://localhost:8080/paragliding/users/ff6w6fs86sf8s/addresses
  @GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE })

  public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {

    List<AddressesRest> returnValue = new ArrayList<>();

    List<AddressDTO> addressesDTO = addressesService.getAddresses(id);

    if (addressesDTO != null && !addressesDTO.isEmpty()) {

      Type listType = new TypeToken<List<AddressesRest>>() {
      }.getType();
      returnValue = new ModelMapper().map(addressesDTO, listType);

      for (AddressesRest addressRest : returnValue) {
        Link selfLink = WebMvcLinkBuilder
            .linkTo(WebMvcLinkBuilder.methodOn(MainController.class).getUserAddress(id, addressRest.getAddressId()))
            .withSelfRel();

        addressRest.add(selfLink);
      }

    }

    Link userLink = WebMvcLinkBuilder.linkTo(MainController.class).slash(id).withRel("user");

    Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MainController.class).getUserAddresses(id))
        .withSelfRel();

    return CollectionModel.of(returnValue, userLink, selfLink);
  }

  @GetMapping(path = "/{id}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE })
  public EntityModel<AddressesRest> getUserAddress(@PathVariable("id") String userId, @PathVariable String addressId) {

    AddressDTO addressesDTO = addressService.getAddress(addressId);
    ModelMapper modelMapper = new ModelMapper();

    AddressesRest returnValue = modelMapper.map(addressesDTO, AddressesRest.class);

    // http:/localhost:8080/users/<userId>
    Link userLink = WebMvcLinkBuilder.linkTo(MainController.class).slash(userId).withRel("user");

    // http:/localhost:8080/users/<userId>/addresses
    Link userAddressesLink = WebMvcLinkBuilder
        .linkTo(WebMvcLinkBuilder.methodOn(MainController.class).getUserAddresses(userId)).withRel("addresses");
    // .slash(userId)
    // .slash("addresses")

    // http:/localhost:8080/users/<userId>/addresses/<addressesId>
    Link selfLink = WebMvcLinkBuilder
        .linkTo(WebMvcLinkBuilder.methodOn(MainController.class).getUserAddress(userId, addressId)).withSelfRel();
    // .slash(userId)
    // .slash("addresses")
    // slash(addressId)

    // returnValue.add(userLink);
    // returnValue.add(userAddressesLink);
    // returnValue.add(selfLink);

    EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));

    return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));

  }

  /*
   * http://localhost:8080/paragliding/users/email-verification?token=8hnr4hs7
   */
  @GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE })
  public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

    OperationStatusModel returnValue = new OperationStatusModel();

    returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

    boolean isVerified = userService.verifyEmailToken(token);

    if(isVerified){
      returnValue.setOperationName(RequestOperationStatus.SUCCESS.name());
    }else {
      returnValue.setOperationName(RequestOperationStatus.ERROR.name());
    }
    return returnValue;

  }

}