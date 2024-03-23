import { Injectable } from '@angular/core';

interface BetHistory {
  betType: string;
  amount: number;
  result: string;
  payout: number;
}

@Injectable({
  providedIn: 'root'
})
export class HistoryService {
  private betHistory: BetHistory[] = [];

  constructor() { }

  logBet(betType: string, amount: number, result: string, payout: number): void {
    this.betHistory.push({ betType, amount, result, payout });
  }

  getHistory(): BetHistory[] {
    return this.betHistory;
  }

}
