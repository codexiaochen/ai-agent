package com.chen.aiagent.controller;

import com.chen.aiagent.advisor.MyLoggerAdvisor;
import com.chen.aiagent.utils.FileBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ollamaController {

    @Resource
    private ChatModel deepseekR1ChatModel;

    //创建chatClient与大模型交互，两种方式   第一种
    private final ChatClient chatClient;

    public ollamaController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("你是一个{java}高手")
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new FileBasedChatMemory("D:\\demo\\memory")),
                        new MyLoggerAdvisor()
                )
                .build();
    }
    //第二种
//    ChatClient chatClient = ChatClient.builder(deepseekR1ChatModel)
//            .defaultSystem("你是一个java编程高手")
//            .build();

    @PostMapping("/chatModel")
    public String getMessage1(String message) {
        ChatResponse response = deepseekR1ChatModel.call(new Prompt(message));
        String result = response.getResult().getOutput().getText();
        return result;
    }


    @PostMapping("/chatClient")
    public String getMessage2(String message, String lenguege) {

//        // ChatClient支持多种响应格式
//        // 1. 返回 ChatResponse 对象（包含元数据如 token 使用量）
//        ChatResponse chatResponse = chatClient.prompt()
//                .user("Tell me a joke")
//                .call()
//                .chatResponse();
//
//        // 2. 返回实体对象（自动将 AI 输出映射为 Java 对象）
//        // 2.1 返回单个实体
//        record ActorFilms(String actor, List<String> movies) {
//        }
//        ActorFilms actorFilms = chatClient.prompt()
//                .user("Generate the filmography for a random actor.")
//                .call()
//                .entity(ActorFilms.class);
//
//        // 2.2 返回泛型集合
//        List<ActorFilms> multipleActors = chatClient.prompt()
//                .user("Generate filmography for Tom Hanks and Bill Murray.")
//                .call()
//                .entity(new ParameterizedTypeReference<List<ActorFilms>>() {
//                });
//
//        // 3. 流式返回（适用于打字机效果）
//        Flux<String> streamResponse = chatClient.prompt()
//                .user("Tell me a story")
//                .stream()
//                .content();
//
//        // 也可以流式返回ChatResponse
//        Flux<ChatResponse> streamWithMetadata = chatClient.prompt()
//                .user("Tell me a story")
//                .stream()
//                .chatResponse();
//        // 定义默认系统提示词
//        ChatClient chatClient = ChatClient.builder(chatModel)
//                .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
//                .build();
//
//        // 对话时动态更改系统提示词的变量
//        chatClient.prompt()
//                .system(sp -> sp.param("voice", voice))
//                .user(message)
//                .call()
//                .content());


        //返回字符串
        String result = chatClient
                .prompt()
                .system(promptSystemSpec -> promptSystemSpec.param("java", lenguege))
                .user(message)
                .call()
                .content();
        return result;
    }

    @PostMapping("/chatModel/test")
    public String getMessage3(String message) {
//        chatClient.prompt().advisors(MessageChatMemoryAdvisor.builder(new InMemoryChatMemory());
        return "";
    }

    /**
     * AI 基础对话（支持多轮对话记忆）
     * @param message
     * @param chatId
     * @return
     */
    @PostMapping("/chatModel/adnisor")
    public Flux<String> doChat(String message, String chatId) {
        Flux<String> content = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();

//        log.info("content: {}", content);
        return content;
    }

}
