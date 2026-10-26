package com.yourjavaguy.mcpclient.assistant;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AssistantController {

    private final CourseAssistant courseAssistant;

    public AssistantController(CourseAssistant courseAssistant) {
        this.courseAssistant = courseAssistant;
    }

    @PostMapping("/ask")
    public ResponseEntity<AskResponse> ask(@Valid @RequestBody AskRequest request) {
        var answer = courseAssistant.ask(request.question());
        return ResponseEntity.ok(new AskResponse(answer));
    }

    @GetMapping("/tools")
    public ResponseEntity<List<String>> tools() {
        return ResponseEntity.ok(courseAssistant.availableToolNames());
    }
}
