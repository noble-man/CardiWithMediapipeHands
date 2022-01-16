import {Component} from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder} from '@angular/forms';  

import {NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { UserService } from 'src/app/services/user.service';
import { Role } from 'src/app/model/role';
import { NotificationService } from 'src/app/services/notification.service';
import { User } from 'src/app/model/user';
import { MustMatch } from 'src/app/utils/must-match.validator';

@Component({
  selector: 'app-new-user',
  templateUrl: './new-user.component.html'
})
export class NewUserComponent {
  rolesList: Role[]=[] ;
  form: FormGroup = new FormGroup({});

  /*form = new FormGroup({  
    username: new FormControl('', Validators.required),  
    password: new FormControl('', Validators.required),  
    confirmedPassword: new FormControl('', Validators.required),  
    enabled: new FormControl('', Validators.required),
    roleId: new FormControl('', Validators.required)  
  });
*/  

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder,
			  private userService: UserService, 
			  private notificationService: NotificationService) {
	    this.userService.getRolesList()
      .subscribe(
        data => {
            this.rolesList = data;
        },
        error => {
			notificationService.error(error);
        });
  }

  ngOnInit(){
	this.form = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', [Validators.required, Validators.minLength(8)]],
            confirmPassword: ['', Validators.required],
            enabled: ['', Validators.required],
            role: ['', Validators.required]
        }, {
            validator: MustMatch('password', 'confirmPassword')
        });
  }
    
  get f(){  
    return this.form.controls;  
  }  
    
  submit(){
	
	const form = this.form.value;
  	const newUser:User = {userId:0, username:form.username, password:form.password, enabled:form.enabled,
			   role: form.role };
    this.userService.register(newUser).subscribe(
      data => {
        this.notificationService.success("The user "+newUser.username+" is created successfuly");
		this.userService.filter('Refresh');
		this.form.reset();
//        this.isSignUpFailed = false;
      },
      err => {
		this.notificationService.error(err.error.message);
          //this.errorMessage = err.error.message;
		  //this.isSignUpFailed = true;
      }
    );
  }



  open(content: any) {
    this.modalService.open(content, {ariaLabelledBy: 'new-user-title'}).result.then((result) => {
		if (result==='Save')
			this.submit();
    }, (reason) => {
      //this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

/*  private getDismissReason(reason: any): string {
	console.log('I am here');
    if (reason === ModalDismissReasons.ESC) {
		console.log('ESC');
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
		console.log('Backdrop');
      return 'by clicking on a backdrop';
    } else {
	  console.log(reason);
      return `with: ${reason}`;
    }
  }
*/


}