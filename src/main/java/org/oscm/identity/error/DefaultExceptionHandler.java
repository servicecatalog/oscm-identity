package org.oscm.identity.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class DefaultExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ModelAndView handleDefaultException(Exception exc){

        logger.error(exc.getMessage(), exc);

        ModelAndView view = new ModelAndView();
        view.addObject("errorMessage", exc.getMessage());
        view.setViewName("error");

        return view;
    }

}
