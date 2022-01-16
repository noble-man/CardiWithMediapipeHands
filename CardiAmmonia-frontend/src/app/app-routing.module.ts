import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ProfileComponent } from './profile/profile.component';
import { ListUsersComponent } from './users/list-users.component';
import { AddUserComponent } from './users/add-user.component';
import { UpdateUserComponent } from './users/update-user.component';
import { ProductionComponent } from './production/production.component';
import { ScheduleComponent } from './schedule/schedule.component';
import { BatchesComponent } from './batches/batches.component';
import { SubBatchesComponent } from './sub-batches/sub-batches.component';
import { ProductionDataComponent } from './production-data/production-data.component';

import { AuthGuard } from './utils/auth.guard';

const routes: Routes = [

  	{ path: 'login', component: LoginComponent },
	{ path: 'production/:id', component: ProductionComponent , canActivate: [AuthGuard] },
	{ path: 'schedule', component: ScheduleComponent , canActivate: [AuthGuard] },
	{ path: 'batches', component: BatchesComponent , canActivate: [AuthGuard] },
	{ path: 'sub-batches', component: SubBatchesComponent , canActivate: [AuthGuard] },
	{ path: 'production-data', component: ProductionDataComponent , canActivate: [AuthGuard] },
	{ path: 'profile', component: ProfileComponent , canActivate: [AuthGuard] },
	{ path: 'users', component: ListUsersComponent , canActivate: [AuthGuard] },
	{ path: 'users/edit/:id', component: UpdateUserComponent , canActivate: [AuthGuard] },
	{ path: 'users/add', component: AddUserComponent , canActivate: [AuthGuard] },
	{ path: 'home', component: ScheduleComponent , canActivate: [AuthGuard] },
	{ path: '', redirectTo: 'home', pathMatch: 'full' , canActivate: [AuthGuard] },
	{ path: '**', redirectTo: '' }

];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true}) ],
  declarations: [
     // this component wants to have access to built-in directives
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
