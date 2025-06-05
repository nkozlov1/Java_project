package ru.kozlov.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic repliesTopic() {
        return TopicBuilder.name("replies")
                .partitions(1)
                .replicas(1)
                .build();
    }

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
    public NewTopic deleteAllCatOwnersTopic() {
        return TopicBuilder.name("delete_all_cat_owners")
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
    public NewTopic addCatToOwnersTopic() {
        return TopicBuilder.name("add_cat_to_owners")
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
    public NewTopic ownersReplyTopic() {
        return TopicBuilder.name("owners-reply-topic")
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