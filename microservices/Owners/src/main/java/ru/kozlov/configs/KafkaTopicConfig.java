package ru.kozlov.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic saveCatOwnerTopic() {
        return TopicBuilder.name("save_cat_owner")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deleteCatOwnerByIdTopic() {
        return TopicBuilder.name("delete_cat_owner_by_id")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic updateCatOwnerTopic() {
        return TopicBuilder.name("update_cat_owner")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic getCatOwnerByIdTopic() {
        return TopicBuilder.name("get_cat_owner_by_id")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic getAllCatOwnersTopic() {
        return TopicBuilder.name("get_all_cat_owners")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ownersReplyTopic() {
        return TopicBuilder.name("owners-reply-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic checkOwnerUserTopic() {
        return TopicBuilder.name("check_owner_user")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic addCatToOwnersTopic() {
        return TopicBuilder.name("add_cat_to_owners")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
