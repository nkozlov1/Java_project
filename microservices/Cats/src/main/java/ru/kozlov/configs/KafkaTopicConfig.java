package ru.kozlov.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic saveCatTopic() {
        return TopicBuilder.name("save_cat")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deleteCatByIdTopic() {
        return TopicBuilder.name("delete_cat_by_id")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic updateCatTopic() {
        return TopicBuilder.name("update_cat")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic getCatByIdTopic() {
        return TopicBuilder.name("get_cat_by_id")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic getAllCatsTopic() {
        return TopicBuilder.name("get_all_cats")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic catsReplyTopic() {
        return TopicBuilder.name("cats-reply-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic checkCatOwnerTopic() {
        return TopicBuilder.name("check_cat_owner")
                .partitions(1)
                .replicas(1)
                .build();
    }
}