package com.pfe.elearning.lesson.entity;

import com.pfe.elearning.MeetingSchedule.MeetingSchedule;
import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.course.entity.Course;

import com.pfe.elearning.forum.entity.Forum;
import com.pfe.elearning.resource.entity.Resourcee;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lessons")
public class Lesson extends BaseEntity {
    private String title;


    private String description;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<Resourcee> resources;



    @OneToMany(mappedBy = "lesson")
    private List<MeetingSchedule> meetingSchedules = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.REMOVE, optional = false)
    private Forum forum;

    /*@OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resource> resources;
     */
}
