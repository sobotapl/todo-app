package io.github.sobotapl.logic;

import io.github.sobotapl.TaskConfigurationProperties;
import io.github.sobotapl.model.*;
import io.github.sobotapl.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("should throw IllegalStatException when configurated to allow just 1 group and the other undone group exist")
    void createGroup_configurationOk_And_noProjects_throwsIllegalArgumentException() {
        //given
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(true);
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        //system under testing
        var toTest = new ProjectService(null, mockGroupRepository, mockConfig);

        //when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));
        //then
        assertThat(exception).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("one undone");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when configuration ok and no projects for given id")
    void createGroup_noMulitipleGroupsConfig_And_undoneGroupExists_throwsIllegalStateException() {
        //given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());

        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under testing
        var toTest = new ProjectService(mockRepository, null, mockConfig);

        //when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));
        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when configured to allow just 1 group  and no groups and no projects for given id")
    void createGroup_noMulitipleGroupsConfig_And_noUndoneGroupExists_noProjects_throwsIllegalStateException() {
        //given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        // and
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under testing
        var toTest = new ProjectService(mockRepository, null, mockConfig);

        //when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));
        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should create a new group from project")
    void createGroup_configurationOk_existingProject_createAndSavesGroup(){
        //given
        var today = LocalDate.now().atStartOfDay();
        //and
        var project = projectWith("ps",Set.of(-1,-2));
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.of(project));
        //and
        InMemoryGroupRepository inMemoryGroupRepo = inMemoryGroupRepository();
        int countBeforeCall = inMemoryGroupRepo.count();
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under test
        var toTest = new ProjectService(mockRepository, inMemoryGroupRepo, mockConfig);

        //when
        GroupReadModel result = toTest.createGroup(today, 1);

        //then
        assertThat(result.getDescription()).isEqualTo("ps");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks()).allMatch(task -> task.getDescription().equals("exmpl"));
        assertThat(countBeforeCall+1).isEqualTo(inMemoryGroupRepo.count());

    }
    private Project projectWith(String projectDescription, Set <Integer> daysToDeadline){
        Set<ProjectStep> steps = daysToDeadline.stream()
                .map(days -> {
                    var step = mock(ProjectStep.class);
                    when(step.getDescription()).thenReturn("exmpl");
                    when(step.getDaysToDeadline()).thenReturn(days);
                    return step;
                }).collect(Collectors.toSet());
        var result = mock(Project.class);
        when(result.getDescription()).thenReturn(projectDescription);
        when(result.getSteps()).thenReturn(steps);
        return result;
    }

    private TaskGroupRepository groupRepositoryReturning(boolean result) {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }

    private TaskConfigurationProperties configurationReturning(boolean result) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);
        //and
        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }

    private InMemoryGroupRepository inMemoryGroupRepository(){
        return new InMemoryGroupRepository();

    }

    private static  class InMemoryGroupRepository implements TaskGroupRepository{

    private int index = 0;
    private Map<Integer, TaskGroup> map = new HashMap<>();

    public int  count(){
        return map.values().size();
    }

    @Override
    public List<TaskGroup> findAll() {
        return new ArrayList<>(map.values()); //map.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<TaskGroup> findById(Integer id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public TaskGroup save(TaskGroup entity) {
        if(entity.getId() == 0){
            try {
                var field = TaskGroup.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(entity, ++index);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        map.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean existsByDoneIsFalseAndProject_Id(Integer projectId) {
        return map.values().stream().filter(group -> !group.isDone())
                .anyMatch(group -> group.getProject() != null && group.getProject().getId() == projectId);
    }
}
}