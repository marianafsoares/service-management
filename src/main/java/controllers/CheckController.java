package controllers;

import models.CheckInfo;
import services.CheckService;

public class CheckController {

    private final CheckService checkService;

    public CheckController(CheckService checkService) {
        this.checkService = checkService;
    }

    public CheckInfo findByNumber(String number) throws Exception {
        return checkService.findByNumber(number);
    }
}

