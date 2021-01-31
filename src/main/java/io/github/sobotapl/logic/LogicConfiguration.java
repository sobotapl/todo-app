package io.github.sobotapl.logic;

import io.github.sobotapl.TaskConfigurationProperties;
import io.github.sobotapl.model.ProjectRepository;
import io.github.sobotapl.model.TaskGroupRepository;
import io.github.sobotapl.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogicConfiguration {

   @Bean
   ProjectService projectService (ProjectRepository repository, TaskGroupRepository taskGroupRepository, TaskGroupService taskGroupService, TaskConfigurationProperties config){
        return new ProjectService (repository, taskGroupRepository, taskGroupService, config);
    }

    @Bean
    TaskGroupService taskGroupService (TaskGroupRepository taskGroupRepository, TaskRepository taskRepository){
       return new TaskGroupService (taskGroupRepository, taskRepository);
    }
}



