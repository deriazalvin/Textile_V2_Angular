import { Component, OnInit } from '@angular/core';
interface Indicateur {
  libelle : string;
  valeur : number;
  icone : string;
}
@Component({
  standalone: false,
  selector: 'app-dashboard',
  styleUrl: './dashboard.scss',
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit{
  indicateurs : Indicateur [] = [];
  ngOnInit(): void {
    
  }

}
