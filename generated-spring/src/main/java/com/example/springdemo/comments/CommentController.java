package com.example.springdemo.comments;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Comment> add(@Validated @RequestBody CommentDTO dto) {
        Comment created = service.addComment(dto);
        return ResponseEntity.created(URI.create("/api/comments/" + created.getId())).body(created);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Comment>> listForTask(@PathVariable String taskId) {
        return ResponseEntity.ok(service.listByTask(taskId));
    }
}
