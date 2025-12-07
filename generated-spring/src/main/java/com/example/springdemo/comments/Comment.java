package com.example.springdemo.comments;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("comments")
public class Comment {
    @Id
    private String id;
    private String taskId;
    private String author;
    private String content;
    private Instant createdAt = Instant.now();

    public Comment() {}

    public Comment(String taskId, String author, String content) {
        this.taskId = taskId;
        this.author = author;
        this.content = content;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
