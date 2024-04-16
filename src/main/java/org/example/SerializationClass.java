package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.StudentData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class SerializationClass {
    public static void main(String[] args) throws IOException {
        StudentData studentData = new StudentData();
        studentData.setFirstname("Vasya");
        studentData.setLastname("Vasilyev");
        studentData.setGrade(11);

        ObjectMapper objectMapper = new ObjectMapper();
//        System.out.println(objectMapper.writeValueAsString(studentData));
        Files.write(Paths.get("file1.txt"), Collections.singleton(objectMapper.writeValueAsString(studentData)));
        String name = Files.readString(Paths.get("file1.txt"));
        System.out.println(name);
        StudentData studentDataNew = objectMapper.readValue(name, StudentData.class);
        System.out.println(studentDataNew);
       /* try (ObjectOutputStream ob = new ObjectOutputStream(Files.newOutputStream(Paths.get("file.txt")))) {
            ob.writeObject(studentData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream oi = new ObjectInputStream(Files.newInputStream(Paths.get("file.txt")))) {
            StudentData studentDataFromSer = (StudentData) oi.readObject();
            System.out.println(studentDataFromSer);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
*/
    }
}
