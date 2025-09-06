import { Player } from './player';

describe('Player', () => {
  it('should have the correct properties', () => {
    const player: Player = {
      chips: 1000,
      name: 'Player 1'
    };
    expect(player).toBeTruthy();
    expect(player.chips).toBe(1000);
    expect(player.name).toBe('Player 1');
  });
});
