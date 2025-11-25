package com.example.springdemo.controller;

import com.example.springdemo.domain.Task;
import com.example.springdemo.dto.TaskDto;
import com.example.springdemo.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String list(Model model) {
        List<Task> tasks = taskService.listAll();
        model.addAttribute("tasks", tasks);
        return "tasks/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("task", new com.example.springdemo.dto.TaskDto());
        return "tasks/form";
    }

    @PostMapping
    public String createFromForm(@ModelAttribute com.example.springdemo.dto.TaskDto dto, 
                                 @AuthenticationPrincipal OAuth2User principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            String owner = principal != null ? principal.getAttribute("login") : "anonymous";
            Task created = taskService.create(dto, owner);
            redirectAttributes.addFlashAttribute("message", 
                "Task created successfully! CID: " + created.getCid());
            return "redirect:/tasks";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to create task: " + e.getMessage());
            return "redirect:/tasks/new";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return taskService.findById(id)
                .map(task -> {
                    TaskDto dto = new TaskDto();
                    dto.setTitle(task.getTitle());
                    dto.setDescription(task.getDescription());
                    model.addAttribute("task", dto);
                    model.addAttribute("taskId", id);
                    model.addAttribute("cid", task.getCid());
                    return "tasks/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Task not found");
                    return "redirect:/tasks";
                });
    }

    @PostMapping("/{id}/update")
    public String updateFromForm(@PathVariable Long id,
                                 @ModelAttribute TaskDto dto,
                                 RedirectAttributes redirectAttributes) {
        try {
            return taskService.update(id, dto)
                    .map(updated -> {
                        redirectAttributes.addFlashAttribute("message", 
                            "Task updated successfully! New CID: " + updated.getCid());
                        return "redirect:/tasks";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("error", "Task not found");
                        return "redirect:/tasks";
                    });
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to update task: " + e.getMessage());
            return "redirect:/tasks/edit/" + id;
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteFromForm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            taskService.delete(id);
            redirectAttributes.addFlashAttribute("message", 
                "Task deleted successfully. IPFS data remains immutable.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to delete task: " + e.getMessage());
        }
        return "redirect:/tasks";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Task> listApi() {
        return taskService.listAll();
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createApi(@RequestBody TaskDto dto, @AuthenticationPrincipal OAuth2User principal) throws Exception {
        String owner = principal != null ? principal.getAttribute("login") : "anonymous";
        Task created = taskService.create(dto, owner);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/api/ipfs/{cid}")
    @ResponseBody
    public ResponseEntity<Task> getTaskByCid(@PathVariable String cid) throws Exception {
        Task task = taskService.getTask(cid);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> updateApi(@PathVariable Long id, @RequestBody TaskDto dto) throws Exception {
        return taskService.update(id, dto)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteApi(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Global Exception Handler for UI
    @ExceptionHandler(Exception.class)
    public String handleUiException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", 
            "An error occurred: " + e.getMessage());
        return "redirect:/tasks";
    }

    // Error Response DTO for API
    static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
