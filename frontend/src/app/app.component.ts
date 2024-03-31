import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {ToastModule} from "primeng/toast";
import {NavbarComponent} from "./components/navbar/navbar.component";
import {ConfirmDialogModule} from "primeng/confirmdialog";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  standalone: true,
  imports: [RouterOutlet, ToastModule, NavbarComponent, ConfirmDialogModule],
})
export class AppComponent {}
