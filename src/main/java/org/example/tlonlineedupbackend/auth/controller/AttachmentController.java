package org.example.tlonlineedupbackend.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.tlonlineedupbackend.auth.entity.Attachment;
import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.service.AttachmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tasks/{taskId}/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<?> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {

        Attachment attachment = attachmentService.uploadAttachment(taskId, file, user);
        return ResponseEntity.ok(attachment);
    }

    @GetMapping
    public List<Attachment> getAttachments(@PathVariable Long taskId) {
        return attachmentService.getTaskAttachments(taskId);
    }
}
