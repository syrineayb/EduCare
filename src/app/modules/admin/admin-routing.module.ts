import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainComponent} from "./pages/main/main.component";
import {authGuard} from "../app-common/services/guards/auth/auth.guard";
import {adminGuard} from "../app-common/services/guards/admin/admin.guard";
import {TopicManagementComponent} from "./pages/topic-management/topic-management.component";
import {ProfileComponent} from "../app-common/pages/profile/profile.component";
import {AccountManagementComponent} from "./pages/account-management/account-management.component";
import {SecurityProfileComponent} from "../app-common/pages/security-profile/security-profile.component";
import {HomeComponent} from "./pages/home/home.component";

const routes: Routes = [
  { path: '', component: MainComponent,
    canActivate: [authGuard, adminGuard], // Apply guards here if necessary
    children: [
      {
        path:'',component:HomeComponent
      },
      {
        path: 'topic-management', component:TopicManagementComponent
      },
      {
        path:'account-management',component:AccountManagementComponent
      },
      {
        path:'profile',component:ProfileComponent
      },
      {
        path:'account-settings',component:SecurityProfileComponent
      },

    ]

  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
