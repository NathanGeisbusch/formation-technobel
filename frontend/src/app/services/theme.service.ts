import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  public readonly darkTheme = new BehaviorSubject<boolean>(false);

  public constructor() {
    const theme = localStorage.getItem('darkTheme');
    this.switchTheme(theme === 'true');
  }

  public switchTheme(dark = false) {
    const body = document.body;
    const link = document.getElementById('app-theme') as HTMLLinkElement;
    if(link) {
      link.href = dark ? "theme-dark.css" : "theme-light.css";
    }
    if(body) {
      body.classList.remove('theme-dark', 'theme-light');
      body.classList.add(dark ? 'theme-dark' : 'theme-light');
    }
    this.darkTheme.next(dark);
    localStorage.setItem('darkTheme', String(dark));
  }
}
