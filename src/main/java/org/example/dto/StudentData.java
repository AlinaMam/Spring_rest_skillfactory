package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class StudentData implements Serializable {
    @JsonProperty("firstname")
    @ApiModelProperty(value = "Имя студента")
    private String firstname;
    @ApiModelProperty(value = "Фамилия студента")
    @JsonProperty("lastname")
    private String lastname;
    @ApiModelProperty(value = "Оценка студента")
    @JsonProperty("grade")
    private int grade;
}
