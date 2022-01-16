import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../model/user';
import { FormGroup, FormControl, Validators } from "@angular/forms";
import { EnhancedFormGroup, EnhancedFormControl, EnhancedFormArray } from '../utils/forms';
import { Role } from '../model/role';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'app-update-user',
  templateUrl: './update-user.component.html',
  styleUrls: ['./update-user.component.scss']
})
export class UpdateUserComponent implements OnInit {

  currentUser: User|null = null;
  message: string = "";
  addUserForm:any = new FormGroup({
       username: new FormControl(),
       password: new FormControl(),
       roles: new FormControl()    });
  defaultRoles: [string] = ["ROLE_USER"];
  rolesList: Role[]=[] ;

  constructor(
	private userService: UserService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit(): void {
  	const currentUser$ = this.userService.getById(Number(this.route.snapshot.paramMap.get('id')));
	const rolesList$ = this.userService.getRolesList();

	combineLatest(currentUser$, rolesList$, (currentUser, rolesList) => ({currentUser, rolesList}))
	.subscribe(pair=>{
			this.currentUser = pair.currentUser;
			this.rolesList = pair.rolesList;
			if (this.currentUser){
				this.addUserForm = EnhancedFormGroup.create({
					username: new EnhancedFormControl<string>(this.currentUser.username,Validators.required),
				    //email: new EnhancedFormControl<string>(),
				    password: new EnhancedFormControl<string>('',Validators.required),
					//roles: this.buildRoles(this.rolesList)
				});
			}
		
		},
		error => {
	          console.log(error);
     });
  }

/*  buildRoles(rolesList: any[]):EnhancedFormArray<FormControl> {
	const arr = rolesList.map(role => {
		console.log(role.roleId);
		if (this.currentUser)
		console.log( );
      return new EnhancedFormControl<boolean>((this.currentUser && this.currentUser.roles.find(e=>e.roleId===role.roleId))|| false);
	})
	return new EnhancedFormArray<FormControl>(arr);
  }
*/
  get roles(): EnhancedFormArray<FormControl> {
	console.log("get roles");
    return this.addUserForm.get('roles') as EnhancedFormArray<FormControl>;
  };


  updateUser(): void {
	if (this.currentUser){
		
		const selectedRoles  = [];
		let i:number = 0;
		for (let index in this.addUserForm.value.roles){
			if (this.addUserForm.value.roles[index])
				selectedRoles.push(this.rolesList[i]);
			i++;
		}
		//this.currentUser.roles = selectedRoles;
		this.currentUser.role = selectedRoles[0];
		this.currentUser.password = this.addUserForm.value.password;
	    this.userService.updateUser(this.currentUser.userId, this.currentUser)
	      .subscribe(
	        response => {
	          console.log(response);
	          this.message = 'The user was updated successfully!';
	        },
	        error => {
	          console.log(error);
	        });
	}
  }

  deleteUser(): void {
	 if (this.currentUser){
	    this.userService.deleteUser(this.currentUser.userId)
	      .subscribe(
	        response => {
	          console.log(response);
	          this.router.navigate(['/users']);
	        },
	        error => {
	          console.log(error);
	        });
	 }
  }

}
