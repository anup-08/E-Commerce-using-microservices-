package com.example.inventoryService.service;

import com.example.inventoryService.dto.InventoryRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventListener {

    private final InventoryService service;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory-topic" ,groupId = "inventory-service-group")
    public void handleFulfillOrder(Object message) throws JsonProcessingException {
        System.out.println("Event received for inventory..");

        Object payload = message;

        if (message instanceof org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record) {
            payload = record.value(); // extract actual message
        }

        String json = objectMapper.writeValueAsString(payload);
        List<InventoryRequest> requests = objectMapper.readValue(
                json, new TypeReference<List<InventoryRequest>>() {}
        );
        service.fulfillStock(requests);
    }
}
