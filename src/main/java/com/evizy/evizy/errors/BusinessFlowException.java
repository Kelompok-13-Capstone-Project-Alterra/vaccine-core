package com.evizy.evizy.errors;

import com.evizy.evizy.constant.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class BusinessFlowException extends RuntimeException{
    private HttpStatus httpStatus;
    private String code;
    private String message;
}
