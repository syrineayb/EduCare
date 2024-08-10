import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CandidateRoutingModule } from './candidate-routing.module';
import { AppCommonModule } from "../app-common/app-common.module";
import { FormsModule } from "@angular/forms";
import { MainComponent } from "./pages/main/main.component";
import { CandidateHomeComponent } from './pages/home/candidate-home.component';
import { TopicListComponent } from './pages/topic-list/topic-list.component';
import { CourseListComponent } from './pages/course-list/course-list.component';
import {ListQestionforquizComponent} from "./pages/list-qestionforquiz/list-qestionforquiz.component";
import {QuizComponent} from "./pages/quiz/quiz.component";
import { MyCourseComponent } from './pages/my-course/my-course.component';
import { ListAllCourseComponent } from './pages/list-all-course/list-all-course.component';
import { CourseLessonsComponent } from './pages/course-lessons/course-lessons.component';
import { MyScheduledMeetingsComponent } from './pages/my-scheduled-meetings/my-scheduled-meetings.component';
import {NgxPaginationModule} from "ngx-pagination";

import { ResourcesComponent } from './pages/resources/resources.component';
import {VgBufferingModule} from "@videogular/ngx-videogular/buffering";
import {VgControlsModule} from "@videogular/ngx-videogular/controls";
import {VgCoreModule} from "@videogular/ngx-videogular/core";
import {VgOverlayPlayModule} from "@videogular/ngx-videogular/overlay-play";
import {PdfViewerModule} from "ng2-pdf-viewer";

import { ListQuizzesForCourseComponent } from './pages/list-quizzes-for-course/list-quizzes-for-course.component';
import { SeeCorrectAnswerComponent } from './pages/see-correct-answer/see-correct-answer.component';

import { ForumComponent } from './pages/forum/forum.component';
import {FullCalendarModule} from "@fullcalendar/angular";


@NgModule({
  declarations: [
    MainComponent,
    CandidateHomeComponent,
    TopicListComponent,
    CourseListComponent,
    QuizComponent,
    ListQestionforquizComponent,
    MyCourseComponent,
    ListAllCourseComponent,
    CourseLessonsComponent,
    MyScheduledMeetingsComponent,
    ResourcesComponent,

    ListQuizzesForCourseComponent,
    SeeCorrectAnswerComponent,

    ForumComponent,

  ],
  exports: [
    CourseListComponent,

  ],
    imports: [
        CommonModule,
        CandidateRoutingModule,
        AppCommonModule,
        FormsModule,
        VgControlsModule,
        PdfViewerModule,
        NgxPaginationModule,
        VgBufferingModule,
        VgControlsModule,
        VgCoreModule,
        VgOverlayPlayModule,
        PdfViewerModule,
        FullCalendarModule,
    ]
})
export class CandidateModule { }
