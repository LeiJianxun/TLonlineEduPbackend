package org.example.tlonlineedupbackend.auth.service;

import org.example.tlonlineedupbackend.auth.entity.Attachment;
import org.example.tlonlineedupbackend.auth.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    Attachment uploadAttachment(Long taskId, MultipartFile file, User uploader);
    List<Attachment> getTaskAttachments(Long taskId);
    void deleteAttachment(Long attachmentId);
}
