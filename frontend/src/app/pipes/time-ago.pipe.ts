import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeAgo',
  standalone: true,
})
export class TimeAgoPipe implements PipeTransform {
  public transform(value: Date|string): string {
    if(!value) return '';
    if(typeof value === 'string') {
      value = new Date(value);
      if(isNaN(value.valueOf())) return '';
    }
    const now = new Date();
    const seconds = Math.floor((now.getTime() - value.getTime()) / 1000);
    let interval = Math.floor(seconds / 31536000);
    if(interval < 0) return '';
    if(interval >= 1) {
      return interval === 1 ? interval + ' year ago' : interval + ' years ago';
    }
    interval = Math.floor(seconds / 2592000);
    if(interval >= 1) {
      return interval === 1 ? interval + ' month ago' : interval + ' months ago';
    }
    interval = Math.floor(seconds / 86400);
    if(interval >= 1) {
      return interval === 1 ? interval + ' day ago' : interval + ' days ago';
    }
    interval = Math.floor(seconds / 3600);
    if(interval >= 1) {
      return interval === 1 ? interval + ' hour ago' : interval + ' hours ago';
    }
    interval = Math.floor(seconds / 60);
    if(interval >= 1) {
      return interval === 1 ? interval + ' minute ago' : interval + ' minutes ago';
    }
    return Math.floor(seconds) === 1 ? Math.floor(seconds) + ' second ago' : Math.floor(seconds) + ' seconds ago';
  }
}