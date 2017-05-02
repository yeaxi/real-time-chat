package ua.dudka.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.dudka.domain.Chat;

public interface ChatChannelRepository extends MongoRepository<Chat, String> {
}
