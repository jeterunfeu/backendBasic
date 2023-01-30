package com.srlab.basic.serverside.utils;

import org.springframework.validation.Errors;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Helper {

    public static LinkedList<LinkedHashMap<String, String>> refineErrors(Errors errors) {
        LinkedList errorList = new LinkedList<LinkedHashMap<String, String>>();
        errors.getFieldErrors().forEach(e-> {
            LinkedHashMap<String, String> error = new LinkedHashMap<>();
            error.put("field", e.getField());
            error.put("message", e.getDefaultMessage());
            errorList.push(error);
        });
        return errorList;
    }
}
