package org.example.train;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Train train = new Train();
        train.setSerialNumber(1234200L);
        train.setDestinationCity("Moscow");
        train.setMaxSpeed(50);
        train.setTime(200);
        train.setCapacity(300);
        System.out.println(objectMapper.writeValueAsString(train));
    }
}
