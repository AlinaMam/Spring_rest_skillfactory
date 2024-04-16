package org.example;

import io.swagger.annotations.ApiOperation;
import org.example.dto.StudentData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping("/student")
    @ApiOperation(value = "Приветствие студента")
    public String helloStudent() {
        return "Hello, student!!!";
    }

    @RequestMapping("/greetings/{name}")
    @ApiOperation(value = "Приветствие студента по имени")
    public String greetingsMyName(@PathVariable("name") String name) {
        return "Hello, " + name;
    }

    @RequestMapping("/submit")
    @ApiOperation(value = "Обратная связь по оценке студента")
    public String giveMeFeedBackAboutGrade(@RequestBody StudentData studentData) {
        return "Your grade = " + studentData.getGrade();
    }
}
