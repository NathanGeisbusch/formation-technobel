import * as img from './assets/img';

const template = document.getElementById('app-content') as HTMLTemplateElement;
const iframe = document.getElementById('smartphone-page') as HTMLIFrameElement;
iframe.contentDocument.open();
iframe.contentDocument.write(template.innerHTML);
iframe.contentDocument.close();
const scriptNew = document.createElement('script');
const scriptOld = iframe.contentDocument.querySelector('script');
scriptOld.remove();
scriptNew.innerHTML = scriptOld.innerHTML;
iframe.contentDocument.body.append(scriptNew);

const linkFavicon = document.createElement('link');
linkFavicon.rel = 'icon';
linkFavicon.href = img.favicon_png;
document.head.appendChild(linkFavicon);
