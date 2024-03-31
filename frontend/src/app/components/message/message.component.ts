import {Component, Input} from '@angular/core';
import {Message} from "primeng/api";
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrl: './message.component.scss',
  standalone: true,
  imports: [
    NgIf,
    NgClass
  ]
})
export class MessageComponent {
  @Input() public message?: Message;
  @Input() public showIcon: boolean = false;

  protected get messageSeverityClass() {
    return 'p-message-' + this.message!.severity;
  }

  protected get messageIconClass() {
    let icon = '';
    switch(this.message!.severity) {
      case 'success': icon = 'pi pi-check'; break;
      case 'info': icon = 'pi pi-info-circle'; break;
      case 'warn': icon = 'pi pi-exclamation-triangle'; break;
      case 'error': icon = 'pi pi-times-circle'; break;
    }
    return `${icon} ${this.messageSeverityClass}`;
  }
}
