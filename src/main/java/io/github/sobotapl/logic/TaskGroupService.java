package io.github.sobotapl.logic;
import io.github.sobotapl.TaskConfigurationProperties;
import io.github.sobotapl.model.Task;
import io.github.sobotapl.model.TaskGroup;
import io.github.sobotapl.model.TaskGroupRepository;
import io.github.sobotapl.model.TaskRepository;
import io.github.sobotapl.model.projection.GroupReadModel;
import io.github.sobotapl.model.projection.GroupWriteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Scope
public class TaskGroupService {

    private TaskGroupRepository repository;
    private TaskRepository taskRepository;


    public TaskGroupService(final TaskGroupRepository repository, final TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    public GroupReadModel createGroup (GroupWriteModel source){
        TaskGroup result = repository.save(source.toGroup());
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll(){
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect((Collectors.toList()));
    }

    public void toggleGroup(int groupId){
       if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)){
           throw new IllegalStateException("Group has incomplite tasks");
       }
        TaskGroup result = repository.findById(groupId).orElseThrow(()->new IllegalArgumentException("TaskGroup with yoour Id not found"));
        result.setDone(!result.isDone());
    }

}
