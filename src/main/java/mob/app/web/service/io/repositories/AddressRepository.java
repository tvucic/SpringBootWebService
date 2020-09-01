package mob.app.web.service.io.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mob.app.web.service.io.entity.AddressEntity;
import mob.app.web.service.io.entity.UserEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

  List<AddressEntity> findAllByUserDetails(UserEntity userEntity);

  AddressEntity findByAddressId(String addressId);

}