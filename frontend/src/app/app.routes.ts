import { Routes } from '@angular/router';
import {PageHomeComponent} from "./pages/page-home/page-home.component";
import {PageRedirectComponent} from "./pages/page-redirect/page-redirect.component";
import {PageChangePasswordComponent} from "./pages/page-change-password/page-change-password.component";
import {PageAccountComponent} from "./pages/page-account/page-account.component";
import {authUserGuard} from "./guards/auth.user.guard";
import {PageNotFoundComponent} from "./pages/page-not-found/page-not-found.component";
import {PageParsersPublicComponent} from "./pages/page-parsers-public/page-parsers-public.component";
import {PageParsersBookmarkedComponent} from "./pages/page-parsers-bookmarked/page-parsers-bookmarked.component";
import {PageGeneratorsPublicComponent} from "./pages/page-generators-public/page-generators-public.component";
import {
  PageGeneratorsBookmarkedComponent
} from "./pages/page-generators-bookmarked/page-generators-bookmarked.component";
import {PageParsersOwnComponent} from "./pages/page-parsers-own/page-parsers-own.component";
import {PageGeneratorsOwnComponent} from "./pages/page-generators-own/page-generators-own.component";
import {PageSessionsComponent} from "./pages/page-sessions/page-sessions.component";
import {PageEditGeneratorComponent} from "./pages/page-edit-generator/page-edit-generator.component";
import {PageTryGeneratorComponent} from "./pages/page-try-generator/page-try-generator.component";
import {PageSharedGeneratorComponent} from "./pages/page-shared-generator/page-shared-generator.component";
import {PageTryParserComponent} from "./pages/page-try-parser/page-try-parser.component";
import {PageSharedParserComponent} from "./pages/page-shared-parser/page-shared-parser.component";
import {PageEditParserComponent} from "./pages/page-edit-parser/page-edit-parser.component";
import {PageEditSessionComponent} from "./pages/page-edit-session/page-edit-session.component";

export const routes: Routes = [
  {path: '', component: PageHomeComponent},
  {path: 'redirect', component: PageRedirectComponent},
  {path: 'change-password/:token', component: PageChangePasswordComponent},
  {path: 'account', component: PageAccountComponent, canActivate: [authUserGuard]},
  {path: 'parsers', component: PageParsersPublicComponent},
  {path: 'parsers/bookmarked', component: PageParsersBookmarkedComponent, canActivate: [authUserGuard]},
  {path: 'parsers/own', component: PageParsersOwnComponent, canActivate: [authUserGuard]},
  {path: 'parsers/try/:id', component: PageTryParserComponent},
  {path: 'parsers/shared/:id', component: PageSharedParserComponent},
  {path: 'parsers/edit/:id', component: PageEditParserComponent, canActivate: [authUserGuard]},
  {path: 'generators', component: PageGeneratorsPublicComponent},
  {path: 'generators/bookmarked', component: PageGeneratorsBookmarkedComponent, canActivate: [authUserGuard]},
  {path: 'generators/own', component: PageGeneratorsOwnComponent, canActivate: [authUserGuard]},
  {path: 'generators/try/:id', component: PageTryGeneratorComponent},
  {path: 'generators/shared/:id', component: PageSharedGeneratorComponent},
  {path: 'generators/edit/:id', component: PageEditGeneratorComponent, canActivate: [authUserGuard]},
  {path: 'sessions', component: PageSessionsComponent, canActivate: [authUserGuard]},
  {path: 'sessions/:id', component: PageEditSessionComponent, canActivate: [authUserGuard]},
  {path: '**', component: PageNotFoundComponent},
];
