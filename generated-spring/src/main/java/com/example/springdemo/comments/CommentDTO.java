package com.example.springdemo.comments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentDTO {
    @NotBlank
    private String taskId;
    @NotBlank
    @Size(max = 100)
    private String author;
    @NotBlank
    @Size(max = 2000)
    private String content;

    public CommentDTO() {}

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
