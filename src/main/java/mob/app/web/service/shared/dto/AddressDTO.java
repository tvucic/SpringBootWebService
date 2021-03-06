package mob.app.web.service.shared.dto;

public class AddressDTO {

  private long id;
  private String addressId;
  private String city;
  private String country;
  private String postalCode;
  private String streetName;
  private UserDto userDetails;
  private String type;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getStreetName() {
    return streetName;
  }

  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

  public UserDto getUserDetails() {
    return userDetails;
  }

  public void setUserDetails(UserDto userDetails) {
    this.userDetails = userDetails;
  }

  public String getAddressId() {
    return addressId;
  }

  public void setAddressId(String addressId) {
    this.addressId = addressId;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}