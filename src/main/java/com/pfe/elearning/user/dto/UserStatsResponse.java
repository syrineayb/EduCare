package com.pfe.elearning.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatsResponse {
    private int countnbrUsers;
    private int countnbrActiveUsers;
    private int countnbrStudents;
    private int countnbrInstructors;
}
