package fr.finanting.server.exception;

public class ThirdNoUserException extends FunctionalException {

    public ThirdNoUserException(final Integer id) {
        super("You are not the owner of the third with the id '" + id + "'" );
    }

}