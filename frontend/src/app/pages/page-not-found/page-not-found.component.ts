import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {ButtonModule} from "primeng/button";

@Component({
  selector: 'app-page-not-found',
  templateUrl: './page-not-found.component.html',
  styleUrl: './page-not-found.component.scss',
  standalone: true,
  imports: [
    ButtonModule
  ]
})
export class PageNotFoundComponent {
  public constructor(private readonly _router: Router) {}

  protected goHome() {
    this._router.navigate(['']).then();
  }
}
