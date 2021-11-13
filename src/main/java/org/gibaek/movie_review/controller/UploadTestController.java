package org.gibaek.movie_review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UploadTestController {
    @GetMapping("uploadEx")
    public String uploadEx() {
        return "uploadEx";
    }

    @GetMapping("index")
    public String index() {
        return "views/index";
    }
}
