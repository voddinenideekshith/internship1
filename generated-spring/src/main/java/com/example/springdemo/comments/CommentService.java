package com.example.springdemo.comments;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository repository;

    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    public Comment addComment(CommentDTO dto) {
        Comment c = new Comment(dto.getTaskId(), dto.getAuthor(), dto.getContent());
        return repository.save(c);
    }

    public List<Comment> listByTask(String taskId) {
        return repository.findByTaskId(taskId);
    }
}
