package com.mydrugs.order.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydrugs.order.config.SQSProperties;
import com.mydrugs.order.model.Order;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SQSOrderEventPublisher implements EventPublisher<Order> {

    private final ObjectMapper objectMapper;
    private final SqsTemplate sqsTemplate;
    private final SQSProperties sqsProperties;

    public SQSOrderEventPublisher(ObjectMapper objectMapper,
                                  SqsTemplate sqsTemplate,
                                   SQSProperties sqsProperties) {
        this.objectMapper = objectMapper;
        this.sqsTemplate = sqsTemplate;
        this.sqsProperties = sqsProperties;
    }


    @Override
    public void publishEvent(Order order) {
        try {
            String messageBody = objectMapper.writeValueAsString(order);
            // TODO: We can use a group by the shop
            String messageGroupId = "teaMates";
           String deduplicationId = order.getCustomerId() + "-" + System.currentTimeMillis(); // unique per message.// unique per message.

            SendResult<String> result = sqsTemplate.send(to -> to
                    .payload(messageBody)
                    .queue(sqsProperties.getQueueName())
                    .headers(Map.of(
                            "message-group-productId", messageGroupId,
                            "message-deduplication-productId", deduplicationId,
                    ))
                    .delaySeconds(10)
            );

            log.info("Published order {} to SQS: {}", result.endpoint(), result.message());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order for SQS", e);
        }
    }
}
