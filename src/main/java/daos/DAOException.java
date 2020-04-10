package daos;

public class DAOException extends Exception {

    private static final String DAO_EXCEPTION_MESSAGE = "DAO exception: ";
    private static final long serialVersionUID = 1L;

    public DAOException(Exception e) {
        super(DAO_EXCEPTION_MESSAGE, e);
    }

    public DAOException(String message) {
        super(DAO_EXCEPTION_MESSAGE + message);
    }

}
