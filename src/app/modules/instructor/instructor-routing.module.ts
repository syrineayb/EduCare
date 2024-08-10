import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainComponent} from "./pages/main/main.component";
import {ProfileComponent} from "../app-common/pages/profile/profile.component";
import {authGuard} from "../app-common/services/guards/auth/auth.guard";
import {instructorGuard} from "../app-common/services/guards/instructor/instructor.guard";
import {SecurityProfileComponent} from "../app-common/pages/security-profile/security-profile.component";
import {CreateCourseComponent} from "./pages/create-course/create-course.component";
import {CourseManagementComponent} from "./pages/course-management/course-management.component";
import {ListQuestionofQuizComponent} from "./pages/list-questionof-quiz/list-questionof-quiz.component";
import {QuizManagementComponent} from "./pages/quiz-management/quiz-management.component";
import {LessonManagementComponent} from "./pages/lesson-management/lesson-management.component";
import {AddLessonComponent} from "./pages/add-lesson/add-lesson.component";
import {HomeComponent} from "./pages/home/home.component";
import {VideoConferenceComponent} from "./pages/video-conference/video-conference.component";
import {CreateMeetingScheduleComponent} from "./pages/create-meeting-schedule/create-meeting-schedule.component";
import {
  MeetingScheduleManagementComponent
} from "./pages/meeting-schedule-management/meeting-schedule-management.component";
import {ForumComponent} from "./pages/forum/forum.component";
import {ScheduleComponent} from "./pages/schedule/schedule.component";

const routes: Routes = [
  {path: '', component: MainComponent,
    canActivate: [authGuard, instructorGuard], // Apply guards here if necessary
    children: [
      {
        path:'',component: HomeComponent
      },
      {
        path: 'profile', component: ProfileComponent,
      },
      {
        path:'account-settings',component: SecurityProfileComponent
      },
      {
        path:'course-management',component: CourseManagementComponent
      },
      {
        path:'create-course',component: CreateCourseComponent
      },
      { path: 'lesson-management/add/:courseId', component: AddLessonComponent }
      ,{
        path:'lesson-management',component: LessonManagementComponent
      },
      {
        path:'quiz-management',component: QuizManagementComponent
      },
      {
        path:'quiz-questions/:quizId',component:ListQuestionofQuizComponent
      },
      { path: 'video-conference', component: VideoConferenceComponent
      },
    /*  { path: 'create-meeting-schedule', component: CreateMeetingScheduleComponent
      },

     */
      { path: 'create-meeting-schedule', component: CreateMeetingScheduleComponent
      },
      { path: 'meeting-schedule-management', component: ScheduleComponent
      },
   /*   { path: 'meeting-schedule-management', component: MeetingScheduleManagementComponent
      },

    */
      { path: 'forum', component: ForumComponent
      },

    ]
  },

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InstructorRoutingModule { }
