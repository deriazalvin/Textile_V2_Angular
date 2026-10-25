import { Component } from '@angular/core';

interface Client{
  id:number;
  nom: string;
  telephone : string;
  email: string;
  adresse: string;
  ville : string;
  commandes?: number;
  status?: string;
}

@Component({
  standalone: false,
  selector: 'app-carte',
  styleUrl: './carte.scss',
  templateUrl: './carte.html',
})
export class Carte {
  client : Client = {
    id :1,
    nom:'HTcity',
    telephone : '+261 36 05 195 23',
    email: 'deriazalv@gmail.com',
    adresse : 'Ivandry',
    ville:'Antananarivo'
  };
  modeEdition = false;
  basculerEdition(): void {
    this.modeEdition = !this.modeEdition;
  }
  enregistrer():void {
    console.log('Client enregistré :', this.client);
    this.modeEdition= false ;
    
  }
}
