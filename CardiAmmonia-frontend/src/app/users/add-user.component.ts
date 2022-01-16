import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormArray, FormBuilder, Validators } from "@angular/forms";
import { EnhancedFormGroup, EnhancedFormControl, EnhancedFormArray } from '../utils/forms';
//import { User } from '../model/user';
import { UserService } from '../services/user.service';
import { Role } from '../model/role';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.scss']
})
export class AddUserComponent implements OnInit {

  isSuccessful = false;
  errorMessage = '';


  addUserForm:any = new FormGroup({
       username: new FormControl(),
       password: new FormControl(),
       roles: new FormControl()    });
  defaultRoles: [string] = ["ROLE_USER"];
  rolesList: Role[]=[] ;

	
  constructor(private userService: UserService) {   
    this.userService.getRolesList()
      .subscribe(
        data => {
	console.log(data);
            this.rolesList = data;
			this.addUserForm = EnhancedFormGroup.create({
				username: new EnhancedFormControl<string>('',Validators.required),
			    //email: new EnhancedFormControl<string>(),
			    password: new EnhancedFormControl<string>('',Validators.required),
				roles: this.buildRoles(this.rolesList)
			});
			console.log(this.roles.controls);

        },
        error => {
          console.log(error);
			
        });

  }
  buildRoles(rolesList: any[]):EnhancedFormArray<FormControl> {
	const arr = rolesList.map(role => {
      return new EnhancedFormControl<boolean>(this.defaultRoles.indexOf(role.roleId) !== -1|| false);
	})
	return new EnhancedFormArray<FormControl>(arr);
  }

  ngOnInit(): void {
  }

  get roles(): EnhancedFormArray<FormControl> {
	console.log("get roles");
    return this.addUserForm.get('roles') as EnhancedFormArray<FormControl>;
  };

  onSubmit(): void {
	const selectedRoles  = [];
	let i:number = 0;
	for (let index in this.addUserForm.value.roles){
		if (this.addUserForm.value.roles[index])
			selectedRoles.push(this.rolesList[i]);
		i++;
	}
	const newUser={'userId':-1,'username':this.addUserForm.value.username, 'password':this.addUserForm.value.password, 'role':selectedRoles[0], enabled:true};
    this.userService.register(newUser).subscribe(
      data => {
        console.log(data);
        this.isSuccessful = true;
//        this.isSignUpFailed = false;
      },
      err => {
        this.errorMessage = err.error.message;
//        this.isSignUpFailed = true;
      }
    );
  }
}