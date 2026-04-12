import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';

import { LandingComponent } from './auth/landing/landing.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';

import { ApplicantDashboardComponent } from './dashboard/applicant-dashboard/applicant-dashboard.component';
import { RecruiterDashboardComponent } from './dashboard/recruiter-dashboard/recruiter-dashboard.component';

import { JobListComponent } from './jobs/job-list/job-list.component';
import { JobDetailComponent } from './jobs/job-detail/job-detail.component';
import { JobCreateComponent } from './jobs/job-create/job-create.component';

import { ApplicantProfileComponent } from './applicant/applicant-profile/applicant-profile.component';
import { MyApplicationsComponent } from './applicant/my-applications/my-applications.component';
import { McqTestComponent } from './mcq/mcq-test/mcq-test.component';

import { RecruiterApplicantsComponent } from './recruiter/recruiter-applicants/recruiter-applicants.component';
import { McqManageComponent } from './recruiter/mcq-manage/mcq-manage.component';

import { AuthGuard } from './shared/guards/auth.guard';
import { AuthInterceptor } from './shared/interceptors/auth.interceptor';
import { AuthService } from './shared/services/auth.service';
import { ApiService } from './shared/services/api.service';

const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },

  // Applicant
  { path: 'dashboard', component: ApplicantDashboardComponent, canActivate: [AuthGuard] },
  { path: 'jobs', component: JobListComponent, canActivate: [AuthGuard] },
  { path: 'jobs/:id', component: JobDetailComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ApplicantProfileComponent, canActivate: [AuthGuard] },
  { path: 'my-applications', component: MyApplicationsComponent, canActivate: [AuthGuard] },
  { path: 'mcq/:jobId', component: McqTestComponent, canActivate: [AuthGuard] },

  // Recruiter
  { path: 'recruiter/dashboard', component: RecruiterDashboardComponent, canActivate: [AuthGuard] },
  { path: 'recruiter/jobs/create', component: JobCreateComponent, canActivate: [AuthGuard] },
  { path: 'recruiter/applicants/:jobId', component: RecruiterApplicantsComponent, canActivate: [AuthGuard] },
  { path: 'recruiter/mcq/:jobId', component: McqManageComponent, canActivate: [AuthGuard] },

  { path: '**', redirectTo: '' }
];

@NgModule({
  declarations: [
    AppComponent, NavbarComponent, SidebarComponent,
    LandingComponent, LoginComponent, RegisterComponent, ForgotPasswordComponent,
    ApplicantDashboardComponent, RecruiterDashboardComponent,
    JobListComponent, JobDetailComponent, JobCreateComponent,
    ApplicantProfileComponent, MyApplicationsComponent,
    McqTestComponent,
    RecruiterApplicantsComponent, McqManageComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [
    AuthService, ApiService, AuthGuard,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
