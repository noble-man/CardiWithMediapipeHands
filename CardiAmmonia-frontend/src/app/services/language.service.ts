import { Injectable } from '@angular/core';
import { Observable, throwError, Subject } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { NotificationService } from './notification.service';


const API_URL = 'http://localhost:8080/api';
const httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
 }

@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  constructor(private httpClient: HttpClient, private notificationService: NotificationService) { }

  getLanguage(): Observable<string> {
		console.log("getLanguage was called!");
    	return this.httpClient.get<string>(API_URL + '/lang/')
			.pipe(
      		catchError(this.errorHandler)
    		)
  }

  getDefaultLanguage(): Observable<string> {
		
    	return this.httpClient.get<string>(API_URL + '/lang_default/')
			.pipe(
      		catchError(this.errorHandler)
    		)
  }

  getAllAvailableLanguages(): Observable<string[]> {
		
    	return this.httpClient.get<string[]>(API_URL + '/lang_all/')
			.pipe(
      		catchError(this.errorHandler)
    		)
  }

  errorHandler(error:any) {
     let errorMessage = '';
     if(error.error instanceof ErrorEvent) {
       // Get client-side error
       	errorMessage = error.error.message;
		console.log("c'est un client-side error");
     } else {
       // Get server-side error
       	errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
		console.log("c'est un server-side error: " + errorMessage);
     }

     return throwError(errorMessage);
  }
}
