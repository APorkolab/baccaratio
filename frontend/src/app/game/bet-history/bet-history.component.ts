import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GameService } from '../game.service';
import { Bet } from '../../model/bet';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-bet-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bet-history.component.html',
  styleUrls: ['./bet-history.component.scss'],
})
export class BetHistoryComponent implements OnInit {
  betHistory: Bet[] = [];
  private subscription: Subscription = new Subscription();

  constructor(private gameService: GameService) {}

  ngOnInit(): void {
    this.subscription.add(
      this.gameService.betHistory$.subscribe((history) => {
        this.betHistory = history;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
