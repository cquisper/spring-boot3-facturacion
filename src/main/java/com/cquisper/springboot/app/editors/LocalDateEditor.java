package com.cquisper.springboot.app.editors;

import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if(text != null && !text.isEmpty()){
            setValue(LocalDate.parse(text));
        }
    }
}
