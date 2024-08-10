import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing.module';
import {HttpClientModule} from "@angular/common/http";
import {RouterLink, RouterModule} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AppCommonModule} from "../app-common/app-common.module";

import {MainComponent} from "./pages/main/main.component";
import { TopicManagementComponent } from './pages/topic-management/topic-management.component';
import {AccountManagementComponent} from "./pages/account-management/account-management.component";
import {HomeComponent} from "./pages/home/home.component";
import {ChartModule} from "angular-highcharts";


@NgModule({
  declarations: [
    MainComponent,
    TopicManagementComponent,
    AccountManagementComponent,
    HomeComponent,
  ],
    imports: [
        CommonModule,
        AdminRoutingModule,
        AppCommonModule,
        FormsModule,
        RouterLink,
        RouterModule,
        HttpClientModule,
        ReactiveFormsModule,
        ChartModule,
    ]
})
export class AdminModule { }
