package com.example.ctsbe.controller;

import com.example.ctsbe.dto.group.GroupUpdateDTO;
import com.example.ctsbe.dto.project.ProjectAddDTO;
import com.example.ctsbe.dto.project.ProjectDTO;
import com.example.ctsbe.dto.staffProject.StaffInProjectDTO;
import com.example.ctsbe.dto.staffProject.StaffProjectAddDTO;
import com.example.ctsbe.dto.staffProject.StaffProjectDTO;
import com.example.ctsbe.entity.Project;
import com.example.ctsbe.entity.StaffProject;
import com.example.ctsbe.mapper.ProjectMapper;
import com.example.ctsbe.mapper.StaffProjectMapper;
import com.example.ctsbe.service.AccountService;
import com.example.ctsbe.service.ProjectService;
import com.example.ctsbe.service.StaffProjectService;
import com.example.ctsbe.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "*")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private StaffProjectService staffProjectService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/getAllProject")
    public ResponseEntity<Map<String, Object>> getAllProject(@RequestParam(defaultValue = "1") int page
            , @RequestParam(defaultValue = "3") int size
            , @RequestParam(required = false) String name) {
        try {
            List<Project> list = new ArrayList<>();
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Project> projectPage;
            if (name == null) {
                projectPage = projectService.getAllProject(pageable);
            } else {
                projectPage = projectService.getProjectByNameContain(name,pageable);
            }
            list = projectPage.getContent();
            List<ProjectDTO> listDto = list.stream().
                    map(ProjectMapper::convertEntityToDto).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("list", listDto);
            response.put("currentPage", projectPage.getNumber());
            response.put("allProducts", projectPage.getTotalElements());
            response.put("allPages", projectPage.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addProject")
    public ResponseEntity<?> addGroup(@Valid @RequestBody ProjectAddDTO dto){
        try{
            projectService.addProject(dto);
            return new ResponseEntity<>("Add project "+dto.getProjectName()+" successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/editProject/{id}")
    public ResponseEntity<?> editProject(@PathVariable("id") int id,@RequestBody ProjectAddDTO dto){
        try{
            projectService.editProject(id,dto);
            return new ResponseEntity<>("Update project successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/changeProjectStatus/{id}/{status}")
    public ResponseEntity<?> changeProjectStatus(@PathVariable("id") int id,@PathVariable("status") int status){
        try{
            projectService.changeProjectStatus(id,status);
            return new ResponseEntity<>("Update project status successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addStaffToProject")
    //@RolesAllowed("ROLE_PROJECT MANAGER")
    public ResponseEntity<?> addStaffToProject(@RequestBody StaffProjectAddDTO dto) {
        try {
            //int staffId = getIdFromToken();
            //int tokenRoleId = accountService.getAccountById(staffId).getRole().getId();
            staffProjectService.addStaffToProject(dto);
            return new ResponseEntity<>("Add staff to the project successfully", HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/removeStaffFromProject")
    public ResponseEntity<?> removeStaffFromProject(@RequestBody StaffProjectAddDTO dto) {
        try {
            staffProjectService.removeStaffFromProject(dto);
            return new ResponseEntity<>("Remove successfully", HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllStaffInProject")
    public ResponseEntity<Map<String, Object>> getAllProject(@RequestParam(defaultValue = "1") int page
            , @RequestParam(defaultValue = "3") int size
            , @RequestParam int projectId) {
        try{
            List<StaffProject> list = new ArrayList<>();
            Pageable pageable = PageRequest.of(page - 1, size);
            Project project = projectService.getProjectById(projectId);
            Page<StaffProject> projectPage = staffProjectService.getAllStaffInProject(project,pageable);
            list = projectPage.getContent();
            List<StaffInProjectDTO> listDto = list.stream().
                    map(StaffProjectMapper::convertEntityToDto).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("list", listDto);
            response.put("currentPage", projectPage.getNumber());
            response.put("allProducts", projectPage.getTotalElements());
            response.put("allPages", projectPage.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public int getIdFromToken(){
        final String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
        }
        int id = jwtTokenUtil.getIdFromToken(jwtToken);
        return id;
    }
}
