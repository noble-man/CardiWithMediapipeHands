import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError, Subject } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User } from '../model/user';
import { Role } from '../model/role';
import { TokenStorageService } from './token-storage.service';
import { NotificationService } from './notification.service';
import * as CONSTANTS from '../utils/globals';

const API_URL = 'http://localhost:8080/api';
const httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }
@Injectable({
  providedIn: 'root'
})
export class UserService {
  currentUser:User = {userId:0, password:"", username:"", role:{roleId:"ROLE_VISITOR",description:"", permissions:[{permissionId:"MANAGE_PROFILE",description:''}]}, enabled:true};
  constructor(private httpClient: HttpClient, 
			  private tokenStorage: TokenStorageService, 
			  private notificationService: NotificationService) { }


  private async login(username: string, password: string): Promise<any>{
    return await this.httpClient.post(CONSTANTS.AUTH_API + 'signin', {
      username,
      password
    }, httpOptions).toPromise();
  }

  loginUser(username:string, password:string):Promise<any>{
	const prom = this.login(username, password);
	    prom.then((data)=>{
		        this.tokenStorage.saveToken(data.accessToken);
		        this.tokenStorage.saveUser(data);
				this.currentUser = data; 
				//console.log(this.currentUser);
				this.notificationService.success("Welcome "+data.username, true);
				return true;
			})
			.catch((error)=>{
				//const err = {type:'erroe', text:'Login error. Try again!'}					
				this.notificationService.info("Login error. Try again!", false);
				return false;
			});
        return prom;
  }
  logoutUser(): void {
    this.tokenStorage.signOut();
  	this.currentUser= {userId:0, password:"", username:"", role:{roleId:"ROLE_VISITOR",description:"",permissions:[{permissionId:"MANAGE_PROFILE",description:''}]}, enabled:true};

  }

  isLoggedIn():boolean{
		return this.currentUser.userId!=0;
  }
  getCurrentUser():User{
		return this.currentUser;
  }
  hasPermission(permission: string):boolean{
	let found:boolean = false;
	this.currentUser.role.permissions.forEach((perm)=>{
		if (perm.permissionId===permission)
			found = true;
	})
	return found;
  }
//User mgmt
  getById(id:Number): Observable<any> {
    return this.httpClient.get<User>(API_URL + '/users/' + id)
    .pipe(
      catchError(this.errorHandler)
    )
  }

  getAll(): Observable<any> {
    return this.httpClient.get<User[]>(API_URL + '/users/')
    .pipe(
      catchError(this.errorHandler)
    )
  }

  updateUser(id:Number, user:User): Observable<any> {
    return this.httpClient.put<User>(API_URL + '/users/' + id, JSON.stringify(user), httpOptions)
    .pipe(
      catchError(this.errorHandler)
    )
  }

  deleteUser(id:Number){
    return this.httpClient.delete<User>(API_URL + '/users/' + id, httpOptions)
    .pipe(
      catchError(this.errorHandler)
    )
  }

  createUser(user:User): Observable<any> {
    return this.httpClient.post<User>(API_URL + '/users/', JSON.stringify(user), httpOptions)
    .pipe(
      catchError(this.errorHandler)
    )
  }  

  register(user:User): Observable<any> {
	console.log(user);
    return this.httpClient.post(CONSTANTS.AUTH_API + 'registeruser', 
      user
    , httpOptions);
  }

  getRolesList(): Observable<any> {
    return this.httpClient.get<Role[]>(API_URL + '/users/roles')
    .pipe(
      catchError(this.errorHandler)
    )
  }

  
//utils
  errorHandler(error:any) {
     let errorMessage = '';
     if(error.error instanceof ErrorEvent) {
       // Get client-side error
       errorMessage = error.error.message;
     } else {
       // Get server-side error
       errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
     }
	 this.notificationService.error(errorMessage);
     return throwError(errorMessage);
  }

  private _listeners = new Subject<any>();
  listen(): Observable<any>{
	return this._listeners.asObservable();
  }
  filter (filterBy: String){
	this._listeners.next(filterBy);
  }
 

}
