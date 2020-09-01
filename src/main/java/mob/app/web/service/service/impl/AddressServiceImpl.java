package mob.app.web.service.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mob.app.web.service.io.entity.AddressEntity;
import mob.app.web.service.io.entity.UserEntity;
import mob.app.web.service.io.repositories.AddressRepository;
import mob.app.web.service.io.repositories.UserRepository;
import mob.app.web.service.service.AddressService;
import mob.app.web.service.shared.dto.AddressDTO;

@Service
public class AddressServiceImpl implements AddressService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  AddressRepository addressRepository;

  @Override
  public List<AddressDTO> getAddresses(String userId) {

    List<AddressDTO> returnValue = new ArrayList<>();
    ModelMapper mapper = new ModelMapper();

    UserEntity userEntity = userRepository.findByUserId(userId);

    if (userEntity == null)
      return returnValue;

    Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);

    for (AddressEntity addressEntity : addresses) {
      returnValue.add(mapper.map(addressEntity, AddressDTO.class));
    }

    return returnValue;
  }

  @Override
  public AddressDTO getAddress(String addressId) {
    AddressDTO returnValue = null;
    AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

    if (addressEntity != null) {
      returnValue = new ModelMapper().map(addressEntity, AddressDTO.class);
    }
    return returnValue;
  }

}