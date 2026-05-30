package org.example.service.config;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.security.OAuthTokenManager;
import org.example.service.TaskService;

public class TaskConfig {

    private final OAuthTokenManager tokenManager;
    @Getter
    private Tasks task;

    public TaskConfig(OAuthTokenManager tokenManager) {
        this.tokenManager = tokenManager;
        initTasks();
    }

    @SneakyThrows
    private void initTasks() {
        task = TaskService.getTasksService(tokenManager);
    }
}
