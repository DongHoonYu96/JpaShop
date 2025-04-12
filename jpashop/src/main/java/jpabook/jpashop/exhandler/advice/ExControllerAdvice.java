package jpabook.jpashop.exhandler.advice;

import jpabook.jpashop.exhandler.ErrorCode;
import jpabook.jpashop.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {
     @ExceptionHandler
     public ResponseEntity<ErrorResult> methodArgumentNotValidExHandle (MethodArgumentNotValidException e) {
         log.error("error : {}", e.getAllErrors());
         ErrorResult errorResult = makeErrorResponseFrom(e.getBindingResult());
         return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
     }

    private ErrorResult makeErrorResponseFrom(BindingResult bindingResult){
        String code = "";
        String description = "";

        //에러가 있다면
        if(bindingResult.hasErrors()){
            //DTO에 설정한 meaasge값을 가져온다
            description = bindingResult.getFieldError().getDefaultMessage();

            //DTO에 유효성체크를 걸어놓은 어노테이션명을 가져온다.
            String bindResultCode = bindingResult.getFieldError().getCode();

            if(bindResultCode == null){
                return new ErrorResult(ErrorCode.FAIL.getCode(), ErrorCode.FAIL.getMessage());
            }

            switch (bindResultCode){
                case "NotNull":
                case "NotEmpty":
                case "NotBlank":
                    code = ErrorCode.NOT_NULL.getCode();
                    description = ErrorCode.NOT_NULL.getMessage();
                    break;
                case "Min":
                    code = ErrorCode.MIN_VALUE.getCode();
                    description = ErrorCode.MIN_VALUE.getMessage();
                    break;
                case "Range":
                    code = ErrorCode.RANGE.getCode();
                    description = ErrorCode.RANGE.getMessage();
                    break;
            }
        }

        return new ErrorResult(code, description);
    }
}
