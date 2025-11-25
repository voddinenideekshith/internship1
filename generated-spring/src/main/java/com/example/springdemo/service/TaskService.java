package com.example.springdemo.service;

import com.example.springdemo.domain.Task;
import com.example.springdemo.dto.TaskDto;
import com.example.springdemo.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final IpfsService ipfsService;
    private final ObjectMapper mapper = new ObjectMapper();

    public TaskService(TaskRepository repository, IpfsService ipfsService) {
        this.repository = repository;
        this.ipfsService = ipfsService;
    }

    @Cacheable(value = "tasks", key = "'list'")
    public List<Task> listAll() {
        return repository.findAll();
    }

    @Cacheable(value = "tasks", key = "#id")
    public Optional<Task> findById(Long id) {
        return repository.findById(id);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task create(TaskDto dto, String owner) throws Exception {
        // convert dto to json
        String json = mapper.writeValueAsString(dto);
        String cid = ipfsService.uploadJson(json);
        Task t = new Task(cid, dto.getTitle(), dto.getDescription(), Instant.now(), owner);
        return repository.save(t);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Optional<Task> update(Long id, TaskDto dto) throws Exception {
        return repository.findById(id).map(existing -> {
            try {
                String json = mapper.writeValueAsString(dto);
                String cid = ipfsService.uploadJson(json);
                existing.setCid(cid);
                existing.setTitle(dto.getTitle());
                existing.setDescription(dto.getDescription());
                existing.setUpdatedAt(Instant.now());
                return repository.save(existing);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Cacheable(value = "ipfs-tasks", key = "#cid")
    public Task getTask(String cid) throws IOException {
        String json = ipfsService.fetchJson(cid);
        return mapper.readValue(json, Task.class);
    }
}
