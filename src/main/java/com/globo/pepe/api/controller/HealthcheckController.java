package com.globo.pepe.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthcheckController {

    @GetMapping(value = "/healthcheck")
    public String healthcheck() { return "WORKING"; }
}
