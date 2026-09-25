import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-preferencias',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './preferencias.html',
  styleUrl: './preferencias.css'
})
export class PreferenciasComponent {
  // Lista de categorías que el usuario puede seleccionar
  categorias = [
    { id: 'restaurantes', nombre: 'Restaurantes', icono: '🍽️', seleccionada: false },
    { id: 'cafes', nombre: 'Cafeterías & Postres', icono: '☕', seleccionada: false },
    { id: 'bares', nombre: 'Bares & Vida Nocturna', icono: '🍸', seleccionada: false },
    { id: 'entretenimiento', nombre: 'Entretenimiento', icono: '🎬', seleccionada: false },
    { id: 'deportes', nombre: 'Deportes & Fitness', icono: '⚽', seleccionada: false },
    { id: 'compras', nombre: 'Tiendas & Compras', icono: '🛍️', seleccionada: false }
  ];

  constructor(private router: Router) {}

  // Cambiar estado de selección al hacer clic
  toggleCategoria(cat: any) {
    cat.seleccionada = !cat.seleccionada;
  }

  // Guardar preferencias elegidas
  guardarPreferencias() {
    const seleccionadas = this.categorias
      .filter(c => c.seleccionada)
      .map(c => c.id);

    console.log('Preferencias seleccionadas:', seleccionadas);
    alert('¡Preferencias guardadas con éxito!');
    
    // Aquí redirigiremos al buscador de lugares (RF4)
    // this.router.navigate(['/lugares']);
  }
}