package mob.app.web.service.exceptions;

public class UserServiceException extends RuntimeException {

  private static final long serialVersionUID = -6010944928023880618L;

  public UserServiceException(String msg) {
    super(msg);
  }

}