package com.spbutu.gia.core.web.testing;

import com.spbutu.gia.core.application.dto.testing.*;
import com.spbutu.gia.core.application.service.testing.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/testing")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/tests")
    public ResponseEntity<List<TestDto>> getAllTests() {
        return ResponseEntity.ok(testService.getAllTests());
    }

    @GetMapping("/tests/{id}")
    public ResponseEntity<TestDto> getTestById(@PathVariable UUID id) {
        return ResponseEntity.ok(testService.getTestById(id));
    }

    @PostMapping("/tests")
    public ResponseEntity<TestDto> createTest(@RequestBody CreateTestRequest request) {
        return ResponseEntity.ok(testService.createTest(request, UUID.fromString("40bf8054-bb90-4279-903e-e2faf58d1fcd")));
    }

    @DeleteMapping("/tests/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable UUID id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tests/student")
    public ResponseEntity<List<TestDto>> getTestsForStudent() {
        return ResponseEntity.ok(testService.getAllTests());
    }

    @PostMapping("/tests/{testId}/start")
    public ResponseEntity<TestAttemptDto> startTest(@PathVariable UUID testId) {
        return ResponseEntity.ok(testService.startTest(UUID.fromString("cfd09e26-9f56-42b6-9bd8-d0cd79c88eb0"), testId));
    }

    @PostMapping("/tests/submit")
    public ResponseEntity<TestResultDto> submitTest(@RequestBody SubmitTestRequest request) {
        return ResponseEntity.ok(testService.submitTest(UUID.fromString("cfd09e26-9f56-42b6-9bd8-d0cd79c88eb0"), request));
    }

    @GetMapping("/attempts/my")
    public ResponseEntity<List<TestAttemptDto>> getMyAttempts() {
        return ResponseEntity.ok(testService.getStudentAttempts(UUID.fromString("cfd09e26-9f56-42b6-9bd8-d0cd79c88eb0")));
    }

    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<TestResultDto> getAttemptResult(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(testService.getAttemptResult(attemptId));
    }
}
