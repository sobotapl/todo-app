package io.github.sobotapl.controller;

import io.github.sobotapl.model.Task;
import io.github.sobotapl.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerE2ETest {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    TaskRepository repo;

    @Test
    void httpGet_returnsAllTasks(){
        //given
        int initial = repo.findAll().size();
        repo.save(new Task("foo", LocalDateTime.now()));
        repo.save(new Task("bar", LocalDateTime.now()));

        //when
        Task[] result = restTemplate.getForObject("http://localhost:" + port + "/tasks", Task[].class);

        //then
        assertThat(result).hasSize(initial+2);

    }
    @Test
    void http_createNewTask(){
        //given
        int initial = repo.findAll().size();
        Task postTask = new Task("testTaskPost", LocalDateTime.now());
        repo.save(postTask);
        //when
        int result = restTemplate.postForObject("http://localhost:" + port + "/tasks",postTask, Task.class).getId();
        //then
        assertThat(result).isEqualTo(initial+1);
        assertThat(postTask).hasFieldOrPropertyWithValue("description", "testTaskPost");

    }





}