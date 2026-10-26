package com.manish.pragyan.modules.rag.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.openai.models.ChatModel;

@Configuration
public class AIConfig {

    @Autowired
    JdbcChatMemoryRepository jdbcChatMemoryRepository;

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(10)
                .build();
    }
	
	@Bean
	public ChatClient chatClientConfig(ChatClient.Builder chatClientBuilder) {
		return chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory()).build())
				.defaultSystem("You are an helpful assistant which answer the question only from the context. if you don't have answer directly say i don,t have answer.")
				.build();
	}

}
