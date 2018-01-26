package edu.nc.controller;


import edu.nc.common.GeneralSettings;
import edu.nc.dataaccess.wrapper.taskprogress.TaskInfoWrapper;
import edu.nc.service.TaskProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = GeneralSettings.TASK_PROGRESS)
public class TaskProgressController {

    private TaskProgressService taskProgressService;

    @Autowired
    public TaskProgressController(TaskProgressService taskProgressService) {
        this.taskProgressService = taskProgressService;
    }


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<TaskInfoWrapper[]> getAvailableAssignments(){
       return taskProgressService.getAvailableAssignments();
    }

}
