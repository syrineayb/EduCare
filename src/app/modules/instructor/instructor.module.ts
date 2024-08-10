import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InstructorRoutingModule } from './instructor-routing.module';
import {AppCommonModule} from "../app-common/app-common.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MainComponent} from "./pages/main/main.component";
import { CourseManagementComponent } from './pages/course-management/course-management.component';
import { CreateCourseComponent } from './pages/create-course/create-course.component';
import {QuizManagementComponent} from "./pages/quiz-management/quiz-management.component";
import {ListQuestionofQuizComponent} from "./pages/list-questionof-quiz/list-questionof-quiz.component";
import { LessonManagementComponent } from './pages/lesson-management/lesson-management.component';
import { AddLessonComponent } from './pages/add-lesson/add-lesson.component';
import {VgCoreModule} from "@videogular/ngx-videogular/core";
import {VgOverlayPlayModule} from "@videogular/ngx-videogular/overlay-play";
import {VgBufferingModule} from "@videogular/ngx-videogular/buffering";
import {VgControlsModule} from "@videogular/ngx-videogular/controls";
import {PdfViewerModule} from "ng2-pdf-viewer";
import {HomeComponent} from "./pages/home/home.component";
import {ChartModule} from "angular-highcharts";
import {VideoConferenceComponent} from "./pages/video-conference/video-conference.component";
import { CreateMeetingScheduleComponent } from './pages/create-meeting-schedule/create-meeting-schedule.component';
import { MeetingScheduleManagementComponent } from './pages/meeting-schedule-management/meeting-schedule-management.component';
import { ForumComponent } from './pages/forum/forum.component';
import { ScheduleComponent } from './pages/schedule/schedule.component';
import {FullCalendarModule} from "@fullcalendar/angular";

@NgModule({
  declarations: [
    MainComponent,
    CourseManagementComponent,
    CreateCourseComponent,
    QuizManagementComponent,
    ListQuestionofQuizComponent,
    LessonManagementComponent,
    AddLessonComponent,
    HomeComponent,
    VideoConferenceComponent,
    CreateMeetingScheduleComponent,
    MeetingScheduleManagementComponent,
    ForumComponent,
    ScheduleComponent

  ],
  exports: [

  ],
  imports: [
    CommonModule,
    InstructorRoutingModule,
    AppCommonModule,
    FormsModule,
    ReactiveFormsModule,
    VgCoreModule,
    VgOverlayPlayModule,
    VgBufferingModule,
    VgControlsModule,
    PdfViewerModule,
    ChartModule,
    FullCalendarModule // Add FullCalendarModule to imports

  ]
})
export class InstructorModule { }
