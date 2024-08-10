package com.pfe.elearning.MeetingSchedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "meeting_schedules")
public class MeetingSchedule extends BaseEntity   {

    @Column(name = "meeting_title", nullable = false)
    private String meetingTitle;

    @Column(name = "duration", nullable = false)
    private Integer durationMinutes;

  //  @JsonFormat(pattern = "yyyy-MM-dd")
   // @Column(name = "date_time", nullable = false)
    private String dateTime;



    @Column(name = "meeting_url", nullable = false)
    private String meetingUrl;

    @Column(name = "meeting_description")
    private String meetingDescription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instructor_id", referencedColumnName = "id", nullable = false)
    private User instructor;

    public MeetingSchedule deepCopy() {
        MeetingSchedule copy = new MeetingSchedule();
        // Copy all properties from the original object to the copy
        copy.setMeetingTitle(this.meetingTitle);
        copy.setDurationMinutes(this.durationMinutes);
        copy.setDateTime(this.dateTime);
        copy.setMeetingUrl(this.meetingUrl);
        copy.setMeetingDescription(this.meetingDescription);
        copy.setCourse(this.course);
        copy.setLesson(this.lesson);
        copy.setInstructor(this.instructor);
        // Copy other properties as needed

        return copy;
    }
}
