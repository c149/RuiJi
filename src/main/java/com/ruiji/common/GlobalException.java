package com.ruiji.common;

//import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.FilerException;
import java.io.FileNotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class,Controller.class})
@ResponseBody
//@Slf4j
public class GlobalException{
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public  R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
//        log.info(ex.getMessage());
        String msg = "";
        if(ex.getMessage().contains("Duplicate")) {
            String s = ex.getMessage().split(" ")[2];
            msg = "重复";
        }else {
            msg = ex.getMessage();
        }
        return R.error(msg);
    }
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler1(CustomException ex) {
        return R.error(ex.getMessage());
    }
    @ExceptionHandler(FileNotFoundException.class)
    public R<String> exceptionHandler2(FileNotFoundException ex) {
        return R.error("path error!");
    }
}
