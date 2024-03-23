import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Card } from '../../model/card';
import { BetPanelComponent } from '../bet-panel/bet-panel.component';
import { PlayerStatusComponent } from '../player-status/player-status.component';
import { Subscription } from 'rxjs';
import { GameService } from '../game.service';

@Component({
  selector: 'app-game-table',
  templateUrl: './game-table.component.html',
  styleUrls: ['./game-table.component.scss']
})
export class GameTableComponent implements OnInit, OnDestroy {
  playerCards: Card[] = [];
  bankerCards: Card[] = [];
  private subscriptions = new Subscription();
  @ViewChild(BetPanelComponent) betPanelComponent!: BetPanelComponent;
  @ViewChild(PlayerStatusComponent) playerStatusComponent!: PlayerStatusComponent;

  constructor(private http: HttpClient, private gameService: GameService) { }

  ngOnDestroy() {
    // Leiratkozás, hogy megakadályozzuk a memória szivárgásokat.
    this.subscriptions.unsubscribe();
  }

  ngOnInit() {
    this.subscriptions.add(this.gameService.playerCards$.subscribe(cards => {
      this.playerCards = cards;
    }));
    this.subscriptions.add(this.gameService.bankerCards$.subscribe(cards => {
      this.bankerCards = cards;
    }));

    if (this.betPanelComponent) {
      this.betPanelComponent.betPlaced.subscribe(({ type, amount }) => {
        console.log(`Fogadás megtörtént: ${type} összeggel: ${amount}`);
        this.getCards();
      });
    }
  }

  ngAfterViewInit() {
    this.betPanelComponent.betPlaced.subscribe(({ type, amount }) => {
      console.log(`Fogadás megtörtént: ${type} összeggel: ${amount}`);
      this.getCards();
    });
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
      value = '10';
    }

    const suit = card.suit.toLowerCase();
    let suitName = suit;
    return `../../../assets/cards/${value}_of_${suitName}.png`;
  }

}
