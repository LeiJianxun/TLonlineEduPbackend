package org.example.tlonlineedupbackend.auth.controller.LoginController;

import org.example.tlonlineedupbackend.auth.entity.School;
import org.example.tlonlineedupbackend.auth.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schools")
public class SchoolController {
    @Autowired
    private SchoolService schoolService;

    @GetMapping("/search")
    public ResponseEntity<Page<School>> searchSchools(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(schoolService.searchSchools(keyword, pageable));
    }
}
