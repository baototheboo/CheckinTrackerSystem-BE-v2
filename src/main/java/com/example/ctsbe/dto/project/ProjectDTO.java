package com.example.ctsbe.dto.project;

import com.example.ctsbe.dto.staff.StaffDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {
    private Integer id;
    private String projectName;
    private String projectManagerName;
    private Integer pmId;
    private String group;
    private Integer groupId;
    private String status;
    //private List<StaffDTO> listStaff;
}
