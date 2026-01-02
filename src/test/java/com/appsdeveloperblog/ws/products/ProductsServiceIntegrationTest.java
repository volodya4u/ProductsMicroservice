package com.appsdeveloperblog.ws.products;

import com.appsdeveloperblog.ws.products.rest.CreateProductRestModel;
import com.appsdeveloperblog.ws.products.service.ProductService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test") // application-test.properties
@EmbeddedKafka(partitions=3, count=3, controlledShutdown=true)
@SpringBootTest(properties="spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class ProductsServiceIntegrationTest {

    private ProductService productService;
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    private Environment environment;

    public ProductsServiceIntegrationTest(ProductService productService,
                                          EmbeddedKafkaBroker embeddedKafkaBroker,
                                          Environment environment) {
        this.productService = productService;
        this.embeddedKafkaBroker = embeddedKafkaBroker;
        this.environment = environment;
    }

    @Test
    void testCreateProduct_whenGivenValidProductDetails_successfulSendsKafkaMessage() throws Exception {

        // Arrange
        String title = "iPhone 11";
        BigDecimal price = new BigDecimal(600);
        Integer quantity = 1;

        CreateProductRestModel createProductRestModel = new CreateProductRestModel();
        createProductRestModel.setTitle(title);
        createProductRestModel.setPrice(price);
        createProductRestModel.setQuantity(quantity);

        // Act
        productService.createProduct(createProductRestModel);


        // Assert
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"),
                JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset"));
    }
}
