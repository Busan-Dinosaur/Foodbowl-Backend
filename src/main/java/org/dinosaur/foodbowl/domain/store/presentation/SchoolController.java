package org.dinosaur.foodbowl.domain.store.presentation;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.application.SchoolService;
import org.dinosaur.foodbowl.domain.store.dto.response.SchoolsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/schools")
@RestController
public class SchoolController implements SchoolControllerDocs {

    private final SchoolService schoolService;

    @GetMapping
    public ResponseEntity<SchoolsResponse> getSchools() {
        SchoolsResponse response = schoolService.getSchools();
        return ResponseEntity.ok(response);
    }
}
