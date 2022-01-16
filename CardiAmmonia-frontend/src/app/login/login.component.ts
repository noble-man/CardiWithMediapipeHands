import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { UserService } from '../services/user.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

	form: any = {
    	username: null,
    	password: null
  	};
	returnUrl: string = '/';
	loading = false;


	constructor(private route: ActivatedRoute,
		      private router: Router,
			  private userService: UserService
			  ) { }

	ngOnInit(): void {
    
		this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
	}

	onSubmit(): void {
	
    	const { username, password } = this.form;
    	
		this.loading = true;
		
		this.userService.loginUser(username, password).then((data)=>{
			this.router.navigate([this.returnUrl]);
		})
		.catch((error)=>{
			this.loading = false;
		});
  }

}