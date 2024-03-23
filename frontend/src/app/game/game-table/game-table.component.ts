import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Card } from '../../model/card';
import { BetPanelComponent } from '../bet-panel/bet-panel.component';
import { PlayerStatusComponent } from '../player-status/player-status.component';

@Component({
  selector: 'app-game-table',
  templateUrl: './game-table.component.html',
  styleUrls: ['./game-table.component.scss']
})
export class GameTableComponent implements OnInit {
  playerCards: Card[] = [];
  bankerCards: Card[] = [];
  @ViewChild(BetPanelComponent) betPanelComponent!: BetPanelComponent;
  @ViewChild(PlayerStatusComponent) playerStatusComponent!: PlayerStatusComponent;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.getCards();
  }

  getCards(): void {
    this.http.get<{ playerCards: Card[], bankerCards: Card[] }>('http://localhost:8080/baccarat/cards')
      .subscribe(response => {
        this.playerCards = response.playerCards;
        this.bankerCards = response.bankerCards;
      }, error => {
        console.error('There was an error retrieving the cards from the backend', error);
      });
  }

  getCardImage(card: Card): string {
    if (!card || !card['value'] || !card.suit) {
      return '../../../assets/cards/back.png';
    }

    let value = card['value'].toLowerCase();
    if (value === 'a') {
      value = 'ace';
    } else if (value === 'k') {
      value = 'king';
    } else if (value === 'q') {
      value = 'queen';
    } else if (value === 'j') {
      value = 'jack';
    } else if (value === '10') {
      value = '10'; // Ez lehet, hogy szükségtelen, ha a "10" már jól érkezik
    }

    const suit = card.suit.toLowerCase();
    let suitName = suit; // Ha a suit nevek megegyeznek az elérési úton használtakkal, nem szükséges a switch case

    // Visszaadja a megfelelő képfájl elérési útját
    return `../../../assets/cards/${value}_of_${suitName}.png`;
  }

}
