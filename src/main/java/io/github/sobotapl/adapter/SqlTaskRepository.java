package io.github.sobotapl.adapter;

import io.github.sobotapl.model.Task;
import io.github.sobotapl.model.TaskRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface SqlTaskRepository extends TaskRepository, JpaRepository<Task, Integer> {
//    @Override
//    @RestResource(exported = false)
//    void deleteById(Integer integer);
//
//    @Override
//    @RestResource(exported = false)
//    void delete(Task task);

    //@RestResource(path = "done", rel = "done")


    @Override
    @Query(nativeQuery = true, value = "select count(*) >0 from tasks where id=:id")
    boolean existsById(@Param("id")Integer id);

    @Override
    boolean existsByDoneIsFalseAndGroup_Id(Integer groupId);
}
