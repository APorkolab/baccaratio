import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Baccaratio';
  showModal: boolean = false;

  showAuthorModal(): void {
    this.showModal = true;
  }

  hideModal(): void {
    this.showModal = false;
  }
}
