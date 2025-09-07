package org.example.tlonlineedupbackend.auth.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tlonlineedupbackend.auth.security.CustomUserDetails;
import org.example.tlonlineedupbackend.auth.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // 修正导入
import org.example.tlonlineedupbackend.auth.entity.Attachment;
import org.example.tlonlineedupbackend.auth.entity.CourseTask;
import org.example.tlonlineedupbackend.auth.service.CourseTaskService;
import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.exception.FileStorageException; // 添加异常导入
import org.example.tlonlineedupbackend.auth.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final CourseTaskService courseTaskService;

    @Autowired
    private HttpServletRequest request;

    @Value("${file.storage.location}")
    private String storageLocation;

    @Override
    public Attachment uploadAttachment(Long taskId, MultipartFile file, User uploader) {
        CourseTask task = courseTaskService.getTaskById(taskId);

        String storedName = UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
        Path uploadPath = Paths.get(storageLocation);

        HttpSession session = request.getSession();
        CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("user");
        User user = userDetails.toUser();

        try {
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), uploadPath.resolve(storedName));

            Attachment attachment = new Attachment();
            attachment.setOriginalName(file.getOriginalFilename());
            attachment.setStoredName(storedName);
            attachment.setFilePath(uploadPath.toString());
            attachment.setFileSize(file.getSize());
            attachment.setUploadTime(LocalDateTime.now());
            attachment.setCourseTask(task);
            attachment.setUploader(user);

            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new FileStorageException("文件存储失败: " + e.getMessage());
        }
    }

    @Override
    public List<Attachment> getTaskAttachments(Long taskId) {
        return List.of();
    }

    @Override
    public void deleteAttachment(Long attachmentId) {

    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}