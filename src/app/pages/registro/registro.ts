import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './registro.html',
  styleUrl: './registro.css'
})
export class RegistroComponent {
  usuario = {
    nombre: '',
    correo: '',
    password: '',
    confirmarPassword: ''
  };

  constructor(private authService: AuthService, private router: Router) {}

  onRegistro(event?: Event) {
    if (event) {
      event.preventDefault();
    }

    if (this.usuario.password !== this.usuario.confirmarPassword) {
      alert('Las contraseñas no coinciden');
      return;
    }

    this.authService.registrar(this.usuario).subscribe({
      next: (res: any) => {
        console.log('Usuario registrado con éxito:', res);
        alert('Registro exitoso');
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        console.error('Error al registrar usuario:', err);
        alert('Error en el registro');
      }
    });
  }
}