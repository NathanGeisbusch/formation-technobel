import {Marked, marked} from "marked";
import DOMPurify from "dompurify";

// Custom marked instance
const markedDoc = function() {
  const result = new Marked();
  const renderer = new marked.Renderer();
  renderer.link = renderer.image = () => '';
  renderer.html = (html) => {
    if(html.includes('<img')) return '';
    if(html.includes('<a')) return '';
    if(html.includes('<link')) return '';
    return html;
  };
  result.setOptions({
    renderer: renderer
  });
  return result;
}();

export function markdownToHtml(markdown: string): string {
  return DOMPurify.sanitize(markedDoc.parse(markdown) as string);
}
