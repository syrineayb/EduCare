import {NgModule} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {MyuppercasePipe} from './services/pipes/myuppercase.pipe';
import {RouterLink, RouterModule} from "@angular/router";
import {HttpClientModule} from "@angular/common/http";
import {NotificationSettingsComponent} from "./pages/notification-settings/notification-settings.component";
import {SecurityProfileComponent} from "./pages/security-profile/security-profile.component";
import {HeaderComponent} from './components/header/header.component';
import {SideBarComponent} from './components/side-bar/side-bar.component';
import {ProfileComponent} from "./pages/profile/profile.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MemberFooterComponent} from "./components/member-footer/member-footer.component";
import {ProgressBarComponent} from './components/progress-bar/progress-bar.component';

//import {MemberFooterComponent} from "./components/footer/member-footer.component";

@NgModule({
  declarations: [
    MyuppercasePipe,
    NotificationSettingsComponent,
    SecurityProfileComponent,
    ProfileComponent,
    NotificationSettingsComponent,
    HeaderComponent,
    SideBarComponent,
    MemberFooterComponent,
    ProgressBarComponent
    // MemberFooterComponent,

  ],
  exports: [
    MyuppercasePipe,
    HeaderComponent,
    SideBarComponent,
    ProfileComponent,
    MemberFooterComponent,
    ProgressBarComponent,

    //  MemberFooterComponent,


  ],
  imports: [

    CommonModule,
    RouterLink,
    RouterModule,
    HttpClientModule,
    NgOptimizedImage,
    ReactiveFormsModule,
    FormsModule,


  ],
  providers: [],

})
export class AppCommonModule {
}
