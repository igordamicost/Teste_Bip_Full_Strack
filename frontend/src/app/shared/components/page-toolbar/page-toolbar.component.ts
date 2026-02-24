import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-page-toolbar',
  standalone: true,
  imports: [MatButtonModule, RouterLink],
  templateUrl: './page-toolbar.component.html',
  styleUrl: './page-toolbar.component.scss',
})
export class PageToolbarComponent {
  @Input() title = '';
  @Input() showNew = false;
  @Input() showTransfer = false;
  @Input() showBack = false;
  @Input() backRoute: string | unknown[] = '/';
}
