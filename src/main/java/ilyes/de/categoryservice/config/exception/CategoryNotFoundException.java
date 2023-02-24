package ilyes.de.categoryservice.config.exception;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends TechnicalException{

    public CategoryNotFoundException(long id, HttpStatus errorHttpStatus, String logType) {
        super(String.format("No category with id %s was found!",id),errorHttpStatus,logType);
    }

    public CategoryNotFoundException(long id, HttpStatus errorHttpStatus) {
        super(String.format("No category with id %s was found!",id),errorHttpStatus);
    }

    public CategoryNotFoundException(long id) {
        super(String.format("No category with id %s was found!",id));
    }
}
