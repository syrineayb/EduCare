package com.pfe.elearning.resource.entity;

import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resources")
public class Resourcee extends BaseEntity {


    private String videoFile;
    private String pdfFile;
    private String imageFile;


    @ManyToOne
    @JoinColumn(name = "lesson_Id", nullable = false)
    private Lesson lesson;



}
