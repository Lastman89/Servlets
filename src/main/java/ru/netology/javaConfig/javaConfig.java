package ru.netology.javaConfig;

import org.springframework.context.annotation.Bean;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

public class javaConfig {
    @Bean
    public PostController postController(PostService service) {
        return new PostController(service);
    }
    @Bean
    public PostService postService(PostRepository repository) {
        return new PostService(repository);
    }
    @Bean
    public PostRepository postRepository() {
        return new PostRepository();
    }
}
