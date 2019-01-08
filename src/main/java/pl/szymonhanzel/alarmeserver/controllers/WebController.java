package pl.szymonhanzel.alarmeserver.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


@Controller
public class WebController {

    @RequestMapping(
    method = RequestMethod.GET,
    produces = "application/json")
    @GetMapping("/")
    @ResponseBody
    public String helloWorld(){
        String firstLine = "Server is running.\r\n";
        String secondLine = new Date().toString()+"\r\n";
        String thirdLine = "Szymon Hanzel AlarMe Server 2019\r\n";
        return firstLine+secondLine +thirdLine;
    }
}
