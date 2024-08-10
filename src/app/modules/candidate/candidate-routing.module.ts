import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {authGuard} from "../app-common/services/guards/auth/auth.guard";
import {candidateGuard} from "../app-common/services/guards/candidate/candidate.guard";
import {ProfileComponent} from "../app-common/pages/profile/profile.component";
import {MainComponent} from "./pages/main/main.component";
import {CandidateHomeComponent} from "./pages/home/candidate-home.component";
import {TopicListComponent} from "./pages/topic-list/topic-list.component";
import {CourseListComponent} from "./pages/course-list/course-list.component";
import {SecurityProfileComponent} from "../app-common/pages/security-profile/security-profile.component";
import {QuizComponent} from "./pages/quiz/quiz.component";
import {ListQestionforquizComponent} from "./pages/list-qestionforquiz/list-qestionforquiz.component";
import {MyCourseComponent} from "./pages/my-course/my-course.component";
import {ListAllCourseComponent} from "./pages/list-all-course/list-all-course.component";
import {CourseLessonsComponent} from "./pages/course-lessons/course-lessons.component";
import {MyScheduledMeetingsComponent} from "./pages/my-scheduled-meetings/my-scheduled-meetings.component";
import {ResourcesComponent} from "./pages/resources/resources.component";

import {ListQuizzesForCourseComponent} from "./pages/list-quizzes-for-course/list-quizzes-for-course.component";
import {SeeCorrectAnswerComponent} from "./pages/see-correct-answer/see-correct-answer.component";

import {ForumComponent} from "./pages/forum/forum.component";


const routes: Routes = [
  {
    path: '',
    component: MainComponent,
    canActivate: [authGuard, candidateGuard], // Apply guards here if necessary
    children: [
      {
        path: 'profile', component: ProfileComponent,
      },
      {
        path: '', component: CandidateHomeComponent,
      },
      {
        path: 'topics', component: TopicListComponent,
      },
      {
        path: 'courses', component: CourseListComponent,
      },
      {
        path: 'my-course', component: MyCourseComponent,
      },
      {
        path: 'account-settings', component: SecurityProfileComponent
      },
      {
        path: 'quizs', component: QuizComponent
      },
      {
        path: 'list-questionof-quiz/:quizId', component: ListQestionforquizComponent
      }
      ,
      {
        path: 'listQuizzes/:courseId', component: ListQuizzesForCourseComponent
      },
      {path: 'seeCorrectAnswers/:quizId', component: SeeCorrectAnswerComponent}, // Assuming you have this component

      {
        path: 'course-catalog', component: ListAllCourseComponent
      },
      {
        path: 'my-course/lessons/:courseId', component: CourseLessonsComponent
      },
      {

       // path: 'my-course/lessons/resources/:LessonId', component: ResourcesComponent

        path:'my-course/lessons/:courseId/resources/:LessonId',component:ResourcesComponent

      },
      {
        path: 'my-meeting-schedule', component: MyScheduledMeetingsComponent
      },



      {
        path:'forum',component:ForumComponent
      },
      { path: 'my-course/lessons/forum/:lessonId', component: ForumComponent },

    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CandidateRoutingModule {
}
