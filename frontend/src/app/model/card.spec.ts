import { Card } from './card';

describe('Card', () => {
  it('should have the correct properties', () => {
    const card: Card = {
      suit: 'hearts',
      value: 'A',
      points: 1
    };
    expect(card).toBeTruthy();
    expect(card.suit).toBe('hearts');
    expect(card.value).toBe('A');
    expect(card.points).toBe(1);
  });
});
