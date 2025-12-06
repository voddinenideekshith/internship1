package com.example.springdemo.service;

import com.example.springdemo.domain.Task;
import com.example.springdemo.dto.TaskDto;
import com.example.springdemo.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        try {
            return repository.findAll();
        } catch (Exception e) {
            // Log error and fallback
            System.err.println("Redis cache error (listAll): " + e.getMessage());
            return repository.findAll();
        }
    }

    @Cacheable(value = "tasks", key = "#id")
    public Optional<Task> findById(Long id) {
        try {
            return repository.findById(id);
        } catch (Exception e) {
            System.err.println("Redis cache error (findById): " + e.getMessage());
            return repository.findById(id);
        }
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task create(TaskDto dto, String owner) throws Exception {
        try {
            String json = mapper.writeValueAsString(dto);
            String cid = ipfsService.add(json);
            Task t = new Task();
            t.setTitle(dto.getTitle());
            t.setDescription(dto.getDescription());
            t.setCid(cid);
            t.setStatus(dto.getStatus());
            t.setOwner(owner);
            return repository.save(t);
        } catch (Exception e) {
            System.err.println("Redis cache error (create): " + e.getMessage());
            throw e;
        }
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Optional<Task> update(Long id, TaskDto dto) throws Exception {
        try {
            return repository.findById(id).map(existing -> {
                try {
                    String json = mapper.writeValueAsString(dto);
                    String cid = ipfsService.add(json);
                    existing.setCid(cid);
                    existing.setTitle(dto.getTitle());
                    existing.setDescription(dto.getDescription());
                    existing.setStatus(dto.getStatus());
                    return repository.save(existing);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            System.err.println("Redis cache error (update): " + e.getMessage());
            throw e;
        }
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            System.err.println("Redis cache error (delete): " + e.getMessage());
            throw e;
        }
    }

    @Cacheable(value = "ipfs-tasks", key = "#cid")
    public Task getTask(String cid) throws Exception {
        try {
            String json = ipfsService.get(cid);
            return mapper.readValue(json, Task.class);
        } catch (Exception e) {
            System.err.println("Redis cache error (getTask): " + e.getMessage());
            throw e;
        }
    }
}
