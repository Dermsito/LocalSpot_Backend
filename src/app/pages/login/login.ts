import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  loginData = {
    correo: '',
    password: ''
  };

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.authService.login(this.loginData).subscribe({
      next: (res: any) => {
        console.log('Inicio de sesión exitoso:', res);
        if (res.token) {
          localStorage.setItem('token', res.token);
        }
        alert('Bienvenido');
      },
      error: (err: any) => {
        console.error('Error en el login:', err);
        alert('Credenciales incorrectas o error en el servidor');
      }
    });
  }
}