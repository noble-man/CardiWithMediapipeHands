import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError } from 'rxjs/operators';


const API_URL = 'http://localhost:8080/api';
const httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }
@Injectable({
  providedIn: 'root'
})
export class PointAndClickService {

  constructor(private httpClient: HttpClient) { }
  /*
   sendCoords(): Observable<any> {
	console.log("Send Coords was called!");
    return this.httpClient.get(API_URL + '/cursor/', httpOptions)
    .pipe(
      catchError(this.errorHandler)
    	)
  	}
  	*/
  	
  	sendCoords(x:Number, y:Number): Observable<any> {
		console.log("Send Coords was called!");
    	return this.httpClient.get(API_URL + '/cursor/'  + x + '/' + y, httpOptions)
    	.pipe(
      		catchError(this.errorHandler)
    	)
  	}
  	
  	sendClick(x:Number, y:Number): Observable<any> {
		console.log("Send Coords was called!");
    	return this.httpClient.get(API_URL + '/click/'  + x + '/' + y, httpOptions)
    	.pipe(
      		catchError(this.errorHandler)
    	)
  	}
  	
  	sendCoordinates(x:Number, y:Number): Observable<any> {
		console.log("Send Coordinates was called!");
		let myRep =  
	     this.httpClient.put<Number>(API_URL + '/cursor/' + x + '/' + y, httpOptions)
	    .pipe(
	      catchError(this.errorHandler)
	    )
	    console.log("myRep: " + myRep);
	    return myRep;
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
