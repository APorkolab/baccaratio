import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GameTableComponent } from './game-table/game-table.component';
import { BetPanelComponent } from './bet-panel/bet-panel.component';
import { BetHistoryComponent } from './bet-history/bet-history.component';
import { PlayerStatusComponent } from './player-status/player-status.component';



@NgModule({
  declarations: [
    GameTableComponent,
    BetPanelComponent,
    BetHistoryComponent,
    PlayerStatusComponent
  ],
  imports: [
    CommonModule
  ]
})
export class GameModule { }
