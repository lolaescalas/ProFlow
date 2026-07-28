package com.proflow.proflow.service;

import com.proflow.proflow.dto.ProjectRequest;
import com.proflow.proflow.dto.ProjectResponse;
import com.proflow.proflow.model.postgres.Project;
import com.proflow.proflow.repository.postgres.ProjectRepository;
import com.proflow.proflow.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse create(ProjectRequest request, String ownerEmail) {
        var owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .members(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(projectRepository.save(project));
    }

    public List<ProjectResponse> getMyProjects(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Project> owned = projectRepository.findByOwnerId(user.getId());
        List<Project> member = projectRepository.findByMembersId(user.getId());

        List<Project> all = new ArrayList<>(owned);
        member.stream()
                .filter(p -> all.stream().noneMatch(o -> o.getId().equals(p.getId())))
                .forEach(all::add);

        return all.stream().map(this::toResponse).toList();
    }

    public ProjectResponse getById(Long id) {
        return projectRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public ProjectResponse addMember(Long projectId, Long userId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (project.getMembers().stream().noneMatch(m -> m.getId().equals(userId)))
            project.getMembers().add(user);

        return toResponse(projectRepository.save(project));
    }

    private ProjectResponse toResponse(Project p) {
        ProjectResponse r = new ProjectResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setOwnerName(p.getOwner().getName());
        r.setMemberNames(p.getMembers().stream().map(m -> m.getName()).toList());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}