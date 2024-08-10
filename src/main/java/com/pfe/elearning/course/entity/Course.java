    package com.pfe.elearning.course.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.pfe.elearning.MeetingSchedule.MeetingSchedule;
    import com.pfe.elearning.common.BaseEntity;
    import com.pfe.elearning.lesson.entity.Lesson;
    import com.pfe.elearning.quiz.entity.Quiz;
    import com.pfe.elearning.topic.entity.Topic;
    import com.pfe.elearning.user.entity.User;
    import jakarta.persistence.*;
    import lombok.*;
    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.List;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "courses")
    public class Course extends BaseEntity {
        private String title;
        private String description;
        private String imageFile;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean free  ; // Indicates if the course is free (true) or paid (false)
        private Float price ; // Initialize price to a default value
        private String timeCommitment; //refers to the estimated amount of time a learner needs to dedicate to complete the course successfully.
        @Column(name = "publisher_username")
        private String publisherUsername;
        @JsonIgnore
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "publisher_id", referencedColumnName = "id")
        private User publisher;
        @ManyToOne
        @JoinColumn(name = "topic_id")
        private Topic topic;

        @JsonIgnore
        @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MeetingSchedule> meetingSchedules = new ArrayList<>();

        @JsonIgnore
        @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Lesson> lessons = new ArrayList<>();
        @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Quiz> quizzes;

        @ManyToMany
        @JoinTable(
                name = "course_candidate",
                joinColumns = @JoinColumn(name = "course_id"),
                inverseJoinColumns = @JoinColumn(name = "candidate_id")
        )
        private List<User> enrolledCandidates;

    }