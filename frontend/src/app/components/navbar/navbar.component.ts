import {Component, OnDestroy} from '@angular/core';
import {MenuItem} from 'primeng/api';
import {NavItem} from "./navbar.items";
import {ThemeService} from "../../services/theme.service";
import {AuthenticatedUser, AuthService} from "../../services/auth.service";
import {MenubarModule} from "primeng/menubar";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {AuthFormType, DialogAuthComponent} from "../dialog-auth/dialog-auth.component";
import {Subscription} from "rxjs";
import {DialogCreateParserComponent} from "../dialog-create-parser/dialog-create-parser.component";
import {DialogCreateGeneratorComponent} from "../dialog-create-generator/dialog-create-generator.component";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
  standalone: true,
  imports: [
    NgIf,
    MenubarModule,
    NgOptimizedImage,
    DialogAuthComponent,
    DialogCreateParserComponent,
    DialogCreateGeneratorComponent,
  ]
})
export class NavbarComponent implements OnDestroy {
  protected darkTheme: boolean = false;
  protected navItems: NavItem[] = [];
  protected user: AuthenticatedUser|null = null;
  protected formType: AuthFormType = null;
  protected showDialogCreateParser: boolean = false;
  protected showDialogCreateGenerator: boolean = false;
  private readonly _user$?: Subscription;
  private readonly _formType$?: Subscription;
  private readonly _darkTheme$?: Subscription;

  public constructor(
    private readonly _themeService: ThemeService,
    private readonly _authService: AuthService,
  ) {
    this._user$ = this._authService.authenticatedUser.subscribe((value) => {
      this.navItems = value === null ? this.notConnectedNavItems : this.connectedNavItems;
    });
    this._formType$ = this._authService.authDialogFormType.subscribe((value) => {
      this.formType = value;
    });
    this._darkTheme$ = this._themeService.darkTheme.subscribe((darkTheme) => {
      this.switchTheme(darkTheme);
    });
  }

  public ngOnDestroy() {
    if(this._user$) this._user$.unsubscribe();
    if(this._formType$) this._formType$.unsubscribe();
    if(this._darkTheme$) this._darkTheme$.unsubscribe();
  }

  private get themeIcon(): string {
    return this.darkTheme ? 'moon' : 'sun';
  }

  protected switchTheme(darkTheme: boolean) {
    this.darkTheme = darkTheme;
    this.notConnectedNavItems.find(i => i.id === 'theme')!.setIcon(this.themeIcon);
    this.connectedNavItems.find(i => i.id === 'theme')!.setIcon(this.themeIcon);
  }

  protected toggleTheme() {
    this.darkTheme = !this.darkTheme;
    this.notConnectedNavItems.find(i => i.id === 'theme')!.setIcon(this.themeIcon);
    this.connectedNavItems.find(i => i.id === 'theme')!.setIcon(this.themeIcon);
    this._themeService.switchTheme(this.darkTheme);
  }

  protected onFormTypeChange($event: AuthFormType) {
    this.formType = $event;
  }

  private notConnectedNavItems: NavItem[] = [
    new NavItem('parsers_public', 'Parsers', '/parsers', 'file-import'),
    new NavItem('generators_public', 'Generators', '/generators', 'file-export'),
    new NavItem('sign_in', 'Sign in', null).setClass('right')
      .setAction(e => this.formType = e.item!.id as any),
    new NavItem('sign_up', 'Sign up').setClass('right')
      .setAction(e => this.formType = e.item!.id as any),
    new NavItem('theme', 'Theme', null, 'sun').setClass('right icon')
      .setAction(e => this.toggleTheme()),
  ];

  private connectedNavItems: NavItem[] = [
    new NavItem(null, 'Parsers', null, 'file-import').children(
      new NavItem('parsers_public', 'Search', '/parsers', 'search'),
      new NavItem('parsers_bookmarked', 'Bookmarked', '/parsers/bookmarked', 'bookmark'),
      new NavItem('parsers_own', 'My parsers', '/parsers/own', 'folder-open'),
      new NavItem('parsers_new', 'Create a parser', null, 'plus')
        .setAction(_ => this.showDialogCreateParser = true),
    ),
    new NavItem(null, 'Generators', null, 'file-export').children(
      new NavItem('generators_public', 'Search', '/generators', 'search'),
      new NavItem('generators_bookmarked', 'Bookmarked', '/generators/bookmarked', 'bookmark'),
      new NavItem('generators_own', 'My generators', '/generators/own', 'folder-open'),
      new NavItem('sessions', 'My sessions', '/sessions', 'box'),
      new NavItem('generators_new', 'Create a generator', null, 'plus')
        .setAction(_ => this.showDialogCreateGenerator = true),
    ),
    new NavItem('theme', 'Theme', null, 'sun').setClass('right icon')
      .setAction(e => this.toggleTheme()),
    new NavItem(null, 'Account', null, 'user').setClass('right no-padding-left').children(
      new NavItem('account_manage', 'Manage my account', '/account', 'wrench'),
      new NavItem('sign_out', 'Sign out', null, 'sign-out')
        .setAction(_ => this._authService.signOut().subscribe({
          next: () => this._authService.redirectTo("/")
        })),
    ),
  ];
}
