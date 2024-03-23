import { Component } from '@angular/core';

@Component({
  selector: 'app-player-status',
  templateUrl: './player-status.component.html',
  styleUrls: ['./player-status.component.scss']
})
export class PlayerStatusComponent {
  balance: number = 1000; // A játékos egyenlege
  totalBet: number = 50; // A játékos által megtenni kívánt össztét

  constructor() { }

  // a betöltési és frissítési logika implementálására
}
