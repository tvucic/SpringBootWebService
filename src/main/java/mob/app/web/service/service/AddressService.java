package mob.app.web.service.service;

import java.util.List;

import mob.app.web.service.shared.dto.AddressDTO;

public interface AddressService {
  List<AddressDTO> getAddresses(String userId);

  AddressDTO getAddress(String addressId);
}