package edu.nc.controller;


import edu.nc.common.GeneralSettings;
import edu.nc.dataaccess.wrapper.questiontask.QuestionResult;
import edu.nc.dataaccess.wrapper.questiontask.QuestionTaskAnswerWrapper;
import edu.nc.security.JwtUser;
import edu.nc.security.JwtUserDetails;
import edu.nc.service.QuestionTaskService;
import edu.nc.dataaccess.wrapper.questiontask.CreateQuestionTaskWrapper;
import edu.nc.dataaccess.wrapper.questiontask.QuestionTaskWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = GeneralSettings.QUESTION_TASK)
public class QuestionTaskController {

    private QuestionTaskService questionTaskService;

    @Autowired
    public QuestionTaskController(QuestionTaskService questionTaskService) {
        this.questionTaskService = questionTaskService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity addTask(@RequestBody CreateQuestionTaskWrapper wrapper){
        return questionTaskService.addTask(wrapper);
    }

    @RequestMapping(value = "/task/{id}", method = RequestMethod.GET)
    public ResponseEntity<QuestionTaskWrapper> get(@PathVariable Long id){
        return questionTaskService.get(id);
    }

    //TODO: create
    @RequestMapping(value = "/check/{id}")
    public ResponseEntity<QuestionResult> check(@PathVariable("id") Long id,
                                                @RequestBody QuestionTaskAnswerWrapper wrapper){
        return questionTaskService.check(id, wrapper);
    }

}
