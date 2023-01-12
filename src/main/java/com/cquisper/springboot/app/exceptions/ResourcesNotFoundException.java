package com.cquisper.springboot.app.exceptions;

public class ResourcesNotFoundException extends RuntimeException {

    public ResourcesNotFoundException(String msg) {
        super(msg);
    }
}
