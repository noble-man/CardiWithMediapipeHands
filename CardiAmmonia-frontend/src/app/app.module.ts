import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { ProfileComponent } from './profile/profile.component';
import { authInterceptorProviders } from './utils/auth.interceptor';
import { errorInterceptorProviders } from './utils/error.interceptor';
import { ListUsersComponent } from './users/list-users.component';
import { AddUserComponent } from './users/add-user.component';
import { UpdateUserComponent } from './users/update-user.component';
import { ProductionComponent } from './production/production.component';
import { NavbarComponent } from './navbar/navbar.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { NewUserComponent } from './users/new-user/new-user.component';
import { AuthGuard } from './utils/auth.guard';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ScheduleComponent } from './schedule/schedule.component';
import { NotificationsComponent } from './notifications/notifications.component';
import { NotificationService } from './services/notification.service';


import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { BatchesComponent } from './batches/batches.component';
import { SubBatchesComponent } from './sub-batches/sub-batches.component';
import { MDBBootstrapModule } from 'angular-bootstrap-md';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { DataTablesModule } from "angular-datatables";
import { ProductionDataComponent } from './production-data/production-data.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ProfileComponent,
    ListUsersComponent,
    AddUserComponent,
    UpdateUserComponent,
    ProductionComponent,
    NavbarComponent,
    SidebarComponent,
    NewUserComponent,
    ScheduleComponent,
    NotificationsComponent,
    BatchesComponent,
    SubBatchesComponent,
    ProductionDataComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
 	TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: httpTranslateLoader,
        deps: [HttpClient]
      }
    }),
	FormsModule,
	ReactiveFormsModule,
	NgbModule,
	MDBBootstrapModule.forRoot(),
	BrowserAnimationsModule,
	DataTablesModule
  ],
  providers: [
	AuthGuard,
	NotificationService,
	authInterceptorProviders,
	errorInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }

// AOT compilation support
export function httpTranslateLoader(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

