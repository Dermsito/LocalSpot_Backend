import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Ajusta esta URL si el servidor de tu compañero usa otro puerto (ej. http://localhost:3000)
  private API_URL = 'http://localhost:3000/api'; 

  constructor(private http: HttpClient) {}

  registrar(datosUsuario: any): Observable<any> {
    return this.http.post(`${this.API_URL}/registro`, datosUsuario);
  }

  login(datosLogin: any): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, datosLogin);
  }
}