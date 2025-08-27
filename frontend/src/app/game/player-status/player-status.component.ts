import { ChangeDetectorRef, Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { GameService } from '../game.service';
import { AuthService } from 'src/app/auth/auth.service';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-player-status',
  templateUrl: './player-status.component.html',
  styleUrls: ['./player-status.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class PlayerStatusComponent implements OnInit, OnDestroy {
  balance: number = 1000;
  playerName: string = 'Player';

  private balanceSubscription: Subscription | undefined;

  constructor(
    private gameService: GameService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    // Fetch initial game state which includes player info
    this.gameService.getGameState().subscribe(state => {
        this.balance = state.playerChips;
        // The player name will be handled differently, perhaps from a user profile service later.
        // For now, we can get it from the principal on the backend, but the GameState doesn't include it.
        // Let's keep it simple.
    });

    this.balanceSubscription = this.gameService.balance$.subscribe(newBalance => {
      this.balance = newBalance;
      this.cdr.detectChanges();
    });
  }

  ngOnDestroy(): void {
    this.balanceSubscription?.unsubscribe();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  resetGame(): void {
    this.gameService.resetGame().subscribe(() => {
      // The game service observable will update the balance.
      // A toastr message could be added here later to confirm.
      console.log('Game reset requested.');
    });
  }
}
