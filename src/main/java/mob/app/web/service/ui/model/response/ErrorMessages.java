package mob.app.web.service.ui.model.response;

public enum ErrorMessages {

  MISSING_REQUIRED_FIELD("Missing required field. Check out documentation for required fields."),
  RECORD_ALREADY_EXISTS("Record already exists"), INTERNAL_SERVER_ERROR("Internal Server error"),
  NO_RECORD_FOUND("Record with provided id not found."), AUTHENTICATION_FAILED("Authentication Failed"),
  COULD_NOT_UPDATE_RECORD("Could Not Update Record"), COULD_NOT_DELETE_RECORD("Could Not Delete Record"),
  EMAIL_ADDRESS_NOT_VERIFIED("Email Address Could Not Be Verified");

  private String errorMessage;

  private ErrorMessages(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * @return the errorMessage
   */

  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * 
   * @param errorMessage the error message to set
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

}
