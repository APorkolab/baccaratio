import { Card } from '../card';

export interface GameResponse {
  result: string;
  playerCards: Card[];
  bankerCards: Card[];
  playerChips: number;
  message: string;
}
