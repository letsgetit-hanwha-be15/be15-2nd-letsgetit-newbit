package com.newbit.coffeeletter.repository;

import com.newbit.coffeeletter.domain.chat.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
} 