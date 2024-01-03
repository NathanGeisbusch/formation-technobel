import cat from './svg/cat.svg';
import {
	CustomElement, ShadowElement, Component, $, $text,
	$a, $abbr, $address, $area, $article, $aside, $audio, $b, $bdi, $bdo,
	$blockquote, $body, $br, $button, $canvas, $caption, $cite, $code, $col,
	$colgroup, $data, $datalist, $dd, $del, $details, $dfn, $dialog, $div,
	$dl, $dt, $em, $embed, $fieldset, $figure, $figcaption, $footer, $form,
	$h1, $h2, $h3, $h4, $h5, $h6, $head, $header, $hgroup, $hr, $html,
	$i, $iframe, $img, $input, $ins, $kbd, $label, $legend, $li, $link, $main, $map,
	$mark, $menu, $meta, $meter, $nav, $object, $ol, $optgroup, $option, $output,
	$p, $picture, $pre, $progress, $q, $rp, $rt, $ruby, $s, $samp, $script,
	$search, $section, $select, $slot, $small, $source, $span, $strong, $style, $svg,
	$sub, $summary, $sup, $table, $tbody, $td, $template, $textarea, $tfoot, $th,
	$thead, $time, $title, $tr, $track, $u, $ul, $var, $video, $wbr,
} from './libs/custom-element';

@Component('drawing-canvas')
class DrawingCanvas extends ShadowElement {
	isDrawing = false;
	canvas: HTMLCanvasElement = null;
	context: CanvasRenderingContext2D = null;
	calque: HTMLDivElement = null;

	startDrawing(ev) {
		const {left, top} = this.canvas.getBoundingClientRect();
		this.isDrawing = true;
		this.context.beginPath();
		this.context.lineTo(ev.clientX - left, ev.clientY - top);
	}

	draw(ev) {
		if(!this.isDrawing) return;
		const {left, top} = this.canvas.getBoundingClientRect();
		this.context.lineTo(ev.clientX - left, ev.clientY - top);
		this.context.stroke();
	}

	stopDrawing() {
		this.isDrawing = false;
		this.context.closePath();
	}

	clear() {
		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
	}

	toggleLayer() {
		this.calque.classList.toggle('hide');
	}

	connectedCallback() {
		super.connectedCallback();
		document.addEventListener("keypress", event => { if(event.key === 'c') this.clear() });
		document.addEventListener("keypress", event => { if(event.key === 'a') this.toggleLayer() });
		document.addEventListener("keydown", event => { if(event.key === 'b') this.context.strokeStyle = 'blue'; });
		document.addEventListener("keydown", event => { if(event.key === 'r') this.context.strokeStyle = 'red'; });
		document.addEventListener("keydown", event => { if(event.key === 'v') this.context.strokeStyle = 'green'; });
		document.addEventListener("keydown", event => { if(event.key === 'o') this.context.strokeStyle = 'orange'; });
		document.addEventListener("keydown", event => { if(event.key === 'm') this.context.strokeStyle = 'purple'; });
		document.addEventListener("keyup", _ => { this.context.strokeStyle = 'black'; });
	}

	$render() {
		this.$(
			$h1('Dessin'),
			$div().class('container').add(
				$div().class('calque hide').add($svg()),
				$canvas(720,480)
				.listen('mousedown', ev => this.startDrawing(ev))
				.listen('mousemove', ev => this.draw(ev))
				.listen('mouseup', _ => this.stopDrawing())
				.listen('mouseout', _ => this.stopDrawing()),
			),
			$p().add(
				$text('La touche "c" permet de réinitialiser la zone de dessin.'),
				$br(),
				$text('La touche "r" enfoncée permet de dessiner en rouge.'),
				$br(),
				$text('La touche "v" enfoncée permet de dessiner en vert.'),
				$br(),
				$text('La touche "b" enfoncée permet de dessiner en bleu.'),
				$br(),
				$text('La touche "o" enfoncée permet de dessiner en orange.'),
				$br(),
				$text('La touche "m" enfoncée permet de dessiner en mauve.'),
				$br(),
				$text('La touche "a" permet d\'afficher/cacher le calque.'),
			),
		);

		this.$style(/*css*/`
			:host {
				width: 100%; height: 100%;
				display: flex; flex-direction: column;
				justify-content: center; align-items: center;
				background-color: #2e2e2e; color: #e2e2e2;
			}
			canvas {border: 1px solid black; background-color: #c4c4c4;}
			p {font-size: 1.25rem}
			.container {position: relative}
			.calque {
				width: 720px; height: 480px;
				display: flex; justify-content: center; align-items: center;
				position: absolute; top: 0; left: 0; opacity: .5;
				pointer-events: none;
			}
			svg {max-width: 720px; max-height: 450px;}
			.hide {visibility: hidden;}
		`);

		this.canvas = this.shadowRoot.querySelector('canvas');
		this.context = this.canvas.getContext('2d');
		this.context.lineWidth = 4;
		this.calque = this.shadowRoot.querySelector('.calque');
		(this.calque.firstChild as HTMLElement).outerHTML = cat;
	}
}
