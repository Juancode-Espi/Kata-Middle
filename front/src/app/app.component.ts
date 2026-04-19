import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ReleaseFormComponent } from './components/release-form/release-form.component';
import { ReleaseListComponent } from './components/release-list/release-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ReleaseFormComponent, ReleaseListComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Kata Releases';
}
