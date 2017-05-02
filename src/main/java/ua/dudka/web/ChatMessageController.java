package ua.dudka.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.dudka.domain.Chat;
import ua.dudka.domain.Message;
import ua.dudka.repository.ChatChannelRepository;

import java.util.Date;
import java.util.List;


@Controller
public class ChatMessageController {

    private final ChatChannelRepository chatChannelRepository;

    public ChatMessageController(ChatChannelRepository chatChannelRepository) {
        this.chatChannelRepository = chatChannelRepository;
    }

    @RequestMapping("/chat/{id}/login")
    public String loginPage(@PathVariable String id, ModelMap modelMap) {
        modelMap.put("id", id);
        return "login";
    }

    @RequestMapping("/chat/{id}")
    public String chatPage(@PathVariable String id, ModelMap modelMap) {
        modelMap.put("id", id);
        return "chat";
    }


    @RequestMapping(value = "/create-chat", method = RequestMethod.POST)
    public ResponseEntity<String> createChat() {
        Chat chat = chatChannelRepository.save(new Chat());
        return new ResponseEntity<>(chat.getId(), HttpStatus.OK);
    }


    @MessageMapping("/newMessage/{id}")
    @SendTo("/chat/{id}")
    public MessageDTO addMessage(@DestinationVariable String id, @RequestBody Message message) {

        Chat chat = chatChannelRepository.findOne(id);

        validateRequest(chat);

        Message chatMessage = new Message(message.getText(), message.getAuthor(), new Date());
        chat.getMessages().add(chatMessage);

        chatChannelRepository.save(chat);
        return new MessageDTO(chat.getMessages().toString());
    }

    private void validateRequest(Chat chat) {
        if (chat == null) {
            throw new RuntimeException("chat with id not found!");
        }
    }

    @RequestMapping(value = "/chat/{id}/messages", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessagesByChat(@PathVariable String id) {
        Chat chat = chatChannelRepository.findOne(id);
        validateRequest(chat);
        return new ResponseEntity<>(chat.getMessages(), HttpStatus.OK);
    }
}