import {APP_INITIALIZER, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { NotfoundComponent } from './pages/notfound/notfound.component';
import {AppInitializerService} from "./modules/app-common/services/app_init/app-initializer.service";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import { CarouselComponent } from './components/carousel/carousel.component';
import {CarouselModule} from "ngx-bootstrap/carousel";
import { ContactUsComponent } from './components/contact-us/contact-us.component';
import {AuthenticationService} from "./services/auth/authentication.service";
import { LessonCardComponent } from './components/lesson-card/lesson-card.component';
import {AppCommonModule} from "./modules/app-common/app-common.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ToastrModule} from "ngx-toastr";
import {TopicCardComponent} from "./components/topic-card/topic-card.component";
import {InstructorCardComponent} from "./components/instructor-card/instructor-card.component";
import {CourseCardComponent} from "./components/course-card/course-card.component";
import {FooterComponent} from "./components/footer/footer.component";
import {HttpTokenInterceptor} from "./modules/app-common/services/interceptor/http-token.interceptor";
import {ChartModule} from "angular-highcharts";
import {CommonModule} from "@angular/common";
import {VgCoreModule} from "@videogular/ngx-videogular/core";
import {VgControlsModule} from "@videogular/ngx-videogular/controls";
import {VgOverlayPlayModule} from "@videogular/ngx-videogular/overlay-play";
import {VgBufferingModule} from "@videogular/ngx-videogular/buffering";
import {PdfViewerModule} from "ng2-pdf-viewer";
import {MemberFooterComponent} from "./modules/app-common/components/member-footer/member-footer.component";
import { CoursesByTopicComponent } from './components/courses-by-topic/courses-by-topic.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';


export function createCustomTranslationLoader(http: HttpClient) {
  return new TranslateHttpLoader(http, 'i18n/', '.json');
}
export function initializeApp(appInitializer: AppInitializerService) {
  return () => appInitializer.initializeApp();
}
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    NavbarComponent,
    NotfoundComponent,
    CarouselComponent,
    TopicCardComponent,
    ContactUsComponent,
    LessonCardComponent,
    InstructorCardComponent,
    CourseCardComponent,
    FooterComponent,
    CoursesByTopicComponent,
    ForgotPasswordComponent,

    //ProfileSidebarComponent,
    // ProfileNavbarComponent


  ],
  imports: [

    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    CarouselModule.forRoot(),
    AppCommonModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({timeOut: 2000}),
    ChartModule,
    CommonModule,
    VgCoreModule,
    VgControlsModule,
    VgOverlayPlayModule,
    VgBufferingModule,
    PdfViewerModule



    // NgbToast,


  ],
  providers: [AuthenticationService,

    HttpClient,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpTokenInterceptor,
      multi: true // means that we have multiple interceptors
    }
    ],
  /* dd */
    exports: [
      //  MemberFooterComponent,
        // ProfileSidebarComponent
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }
