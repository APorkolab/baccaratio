import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
//import { RegisterPlayersComponent } from './register-players/register-players.component';
import { GameTableComponent } from './game/game-table/game-table.component';

const routes: Routes = [
  {
    path: '',
    component: GameTableComponent,
  },
  // {
  //   path: 'start',
  //   component: GameTableComponent,
  // },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }