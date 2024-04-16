package org.example;

import org.example.dto.StudentData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping("/student")
    public String helloStudent() {
        return "Hello, student!!!";
    }

    @RequestMapping("/greetings/{name}")
    public String greetingsMyName(@PathVariable("name") String name) {
        return "Hello, " + name;
    }

    @RequestMapping("/submit")
    public String giveMeFeedBackAboutGrade(@RequestBody StudentData studentData) {
        return "Your grade = " + studentData.getGrade();
    }
}
