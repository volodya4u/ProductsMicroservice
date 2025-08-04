package com.appsdeveloperblog.ws.products.service;

import com.appsdeveloperblog.ws.core.ProductCreatedEvent;
import com.appsdeveloperblog.ws.products.rest.CreateProductRestModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel productRestModel) throws Exception {

        String productId = UUID.randomUUID().toString();

        // TODO: Persist Product Details info database table before publishing an Event

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId,
                productRestModel.getTitle(),
                productRestModel.getPrice(),
                productRestModel.getQuantity());

//        // asynchronous execution
//        CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
//                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);
//
//        future.whenComplete((result, exception) -> {
//
//            if (exception != null) {
//                log.error("***** Error while sending product created event: {}", exception.getMessage());
//            } else {
//                log.info("***** Successfully send product created event: {}", result.getRecordMetadata());
//            }
//        });

        log.info("Before publishing a ProductCreatedEvent");

        ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord<>(
                "product-created-events-topic",
                productId,
                productCreatedEvent);
        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, ProductCreatedEvent> result =
                kafkaTemplate.send(record).get();

        log.info("Partition: {}", result.getRecordMetadata().partition());
        log.info("Topic: {}", result.getRecordMetadata().topic());
        log.info("Offset: {}", result.getRecordMetadata().offset());

        log.info("***** Returning product id");

        return productId;
    }
}
