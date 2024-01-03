export class DOMElement {
	/** @type {Node} */
	element;

	/**
	 * @param {string|''|Node} element Élément html ou le nom de cet élément (ex. div, span, ...) (si vide, crée un DocumentFragment)
	 * @param {object} props Les propriétés de l'élément (ex. className, textContent, ...) 
	 */
	constructor(element, props=null) {
		if(element instanceof Node) this.element = element;
		else if(typeof element === 'string') this.element = element.includes('-') ?
			customElements.get(element) : document.createElement(element);
		else this.element = document.createDocumentFragment();
		if(props) for(let prop in props) {
			const value = props[prop];
			if(value !== undefined && value !== null) this.element[prop] = value;
		}
	}

	/**
	 * Définit la valeur des propriétés pour cet élément.
	 * @param {*} props Les propriétés de l'élément (ex. className, textContent, ...) 
	 * @returns {DOMElement} this
	 */
	props(props) {
		for(let prop in props) {
			const value = props[prop];
			if(value !== undefined && value !== null) this.element[prop] = value;
		}
		return this;
	}

	/**
	 * Définit la valeur d'un attribut pour cet élément.
	 * @param {string} key Le nom de l'attribut
	 * @param {string} value La valeur de l'attribut
	 * @returns {DOMElement} this
	 */
	attr(key, value) {
		this.element.setAttribute(key, value);
		return this;
	}

	/**
	 * Ajoute un event listener pour cet élément.
	 * @param {string} eventName Le nom de l'event
	 * @param {EventListenerOrEventListenerObject} callback La fonction à exécuter pour cet event
	 * @param {AddEventListenerOptions} options Les options du listener
	 * @returns {DOMElement} this
	 */
	listen(eventName, callback, options=null) {
		if(options) this.element.addEventListener(eventName, callback, options);
		else this.element.addEventListener(eventName, callback);
		return this;
	}

	/**
	 * Ajoute une référence vers cet élément dans un CustomElement.
	 * Accessible via: this.$ref.key
	 * @param {string} key Nom de la référence
	 * @param {CustomElement|ShadowElement} customElement CustomElement dans lequel assigner la référence
	 * @returns {DOMElement} this
	 */
	ref(key, customElement) {
		customElement.$ref[key] = this.element;
		return this;
	}

	/**
	 * Ajoute une référence vers cet élément dans un CustomElement.
	 * Plusieurs éléments peuvent avoir le même nom de référence.
	 * Accessible via: this.$refs.key
	 * @param {string} key Nom de la référence
	 * @param {CustomElement|ShadowElement} customElement CustomElement dans lequel assigner la référence
	 * @returns {DOMElement} this
	 */
	refs(key, customElement) {
		if(!(customElement.$refs[key] instanceof Array)) customElement.$refs[key] = [];
		customElement.$refs[key].push(this.element);
		return this;
	}

	/**
	 * Ajoute des éléments enfants à cet élément.
	 * @param  {...H} children 
	 * @returns {DOMElement} this
	 */
	add(...children) {
		for(const child of children) {
			if(!child) continue;
			if(Array.isArray(child)) {
				for(const elem of child) this.element.appendChild(elem.element);
			}
			else this.element.appendChild(child.element);
		}
		return this;
	}

	/**
	 * Génère des éléments enfants à partir d'une liste de valeurs
	 * @param {Array} arr Une liste sur laquelle itérer
	 * @param {(value: any, index: number, array: any[]) => any} mapCallback Callback à appeler pour chaque élément
	 * @returns {DOMElement} this
	 */
	map(arr, mapCallback) {
		this.element.append(...arr.map(mapCallback).map(e => e.element));
		return this;
	}

	/**
	 * Génère un nombre défini d'éléments enfants
	 * @param {number} amount Le nombre d'éléments à générer
	 * @param {(index: number) => any} forEachCallback Callback à appeler pour chaque élément
	 * @returns {DOMElement} this
	 */
	repeat(amount, forEachCallback) {
		for(let i = 0; i < amount; i++) this.element.appendChild(forEachCallback(i).element);
		return this;
	}

	/**
	 * N'effectue le rendu de l'élément que si la condition est vraie.
	 * @param {() => boolean} condition 
	 * @returns {DOMElement} this si la condition est vraie, null sinon
	 */
	if(condition) {
		return condition() ? this : null;
	}

	/**
	 * 
	 * @param {Node} node Le Node HTML dans lequel ajouter cet élément.
	 * @param {boolean} append true si l'élément doit être ajouté à node,
	 * false si il doit remplacer le contenu de node
	 * @returns Le Node HTML de cette instance
	 */
	render(node=null, append=false) {
		if(node) {
			if(!append) node.innerHTML = '';
			node.appendChild(this.element);
		}
		return this.element;
	}

	/**
	 * Attribue un id à l'élément.
	 * @param {string} id Id à attribuer
	 * @returns {DOMElement} this
	 */
	id(id) {
		this.element.id = id;
		return this;
	}

	/**
	 * Attribue une ou des classes à l'élément.
	 * @param {string} className Classe(s) à attribuer à l'élément (séparées par des espaces)
	 * @returns {DOMElement} this
	 */
	class(className) {
		this.element.className = className;
		return this;
	}

	/**
	 * Attribue des classes à l'élément.
	 * @param {string[]} classes Classe(s) à attribuer à l'élément
	* @returns {DOMElement} this
	 */
	classes(...classes) {
		this.element.classList.add(classes);
		return this;
	}

	/**
	 * Assigne un texte en tant que contenu de l'élément.
	 * @param {string} text Texte à assigner
	 * @returns {DOMElement} this
	 */
	text(text) {
		this.element.textContent = text;
		return this;
	}

	/**
	 * Cache l'élément via l'attribut hidden.
	 * @param {boolean} hidden Si l'élément doit être caché
	 * @returns {DOMElement} this
	 */
	hide(hidden=true) {
		this.element.hidden = hidden;
		return this;
	}

	/**
	 * Définit la valeur de l'élément (dans le cadre d'un champ de formulaire)
	 * @param {string|number} value
	 * @returns {DOMElement} this
	 */
	value(value) {
		this.element.value = value;
		return this;
	}

	/**
	 * Définit la valeur minimum, maximum, et l'incrément (dans le cadre d'un champ de formulaire)
	 * @param {number} min
	 * @param {number} max
	 * @param {number} step
	 * @returns {DOMElement} this
	 */
	range(min, max, step=1) {
		this.element.min = min;
		this.element.max = max;
		this.element.step = step;
		return this;
	}

	/**
	 * Coche l'élément (dans le cadre d'un radiobutton ou d'une checkbox)
	 * @param {boolean} checked Si l'élément doit être coché
	 * @returns {DOMElement} this
	 */
	checked(checked=true) {
		this.element.checked = checked;
		return this;
	}

	/**
	 * Définit les attributs de l'élément (dans le cadre d'un champ de formulaire)
	 * @param {string} pattern
	 * @param {number} maxLength
	 * @param {number} minLength
	 * @returns {DOMElement} this
	 */
	validation(pattern, maxLength, minLength) {
		this.element.pattern = pattern;
		if(maxLength !== undefined || maxLength !== null) this.element.maxLength = maxLength;
		if(minLength !== undefined || minLength !== null) this.element.minLength = minLength;
		return this;
	}

	/**
	 * Définit si le champ doit être obligatoire.
	 * @param required Si le champ doit être obligatoire
	 * @returns {DOMElement} this
	 */
	required(required=true) {
		this.element.required = required;
		return this;
	}

	/**
	 * Définit si le champ doit être désactivé.
	 * @param required Si le champ doit être désactivé
	 * @returns {DOMElement} this
	 */
	disabled(disabled=true) {
		this.element.disabled = disabled;
		return this;
	}

	/**
	 * Définit le nom de l'élément.
	 * @param name Nom à attribuer
	 * @returns {DOMElement} this
	 */
	name(name) {
		this.element.name = name;
		return this;
	}

	/**
	 * Applique le style CSS à l'élément.
	 * @param {string} style Le style CSS à appliquer
	 * @returns {DOMElement} this
	 */
	style(style) {
		this.element.style = style;
		return this;
	}

	/**
	 * Définit si l'élément est déplaçable.
	 * @param {boolean} draggable Si l'élément est déplaçable
	 * @returns {DOMElement} this
	 */
	draggable(draggable=true) {
		this.element.draggable = draggable;
		return this;
	}

	/**
	 * Définit la valeur d'un dataset pour cet élément.
	 * @param {string} key Le nom du dataset
	 * @param {string} value La valeur du dataset
	 * @returns {DOMElement} this
	 */
	data(key, value) {
		this.element.dataset[key] = value;
		return this;
	}

	/**
	 * Cherche un élément qui correspond au critère de sélection.
	 * @param {string} selector Sélecteur
	 * @returns {HTMLElement}
	 */
	find(selector) {
		return this.element.querySelector(selector);
	}

	/**
	 * Cherche les éléments qui correspondent au critère de sélection.
	 * @param {string} selector Sélecteur
	 * @returns {HTMLElement}
	 */
	findAll(selector) {
		return Array.from(this.element.querySelectorAll(selector));
	}
}

/** Crée une instance de la classe DOMElement */
export function $(element, props) { return new DOMElement(element, props); }

/**
 * Crée un TextNode.
 * @param {string} text Le texte du TextNode
 * @returns {DOMElement}
 */
export function $text(text) {
	return new DOMElement(document.createTextNode(text));
}

export function $a(href=null, text=null, title=null, rel=null, target=null, download=null) {
	return new DOMElement('a', {href, textContent: text, title, rel, target, download});
}
export function $abbr(text, title) {
	return new DOMElement('abbr', {textContent: text, title});
}
export function $address() { return new DOMElement('address'); }
export function $area(shape=null, coords=null, href=null, title=null, rel=null, target=null, download=null) {
	return new DOMElement('area', {shape, coords, href, title, rel, target, download});
}
export function $article() { return new DOMElement('article'); }
export function $aside() { return new DOMElement('aside'); }
export function $audio(src, controls=null, autoplay=null, loop=null, muted=null, volume=null, poster=null) {
	return new DOMElement('audio', {src, controls, autoplay, loop, muted, volume, poster});
}
export function $b(text=null) {
	return new DOMElement('b', {textContent: text});
}
export function $bdi(text, dir=null) {
	return new DOMElement('b', {textContent: text, dir});
}
export function $bdo(text, dir=null) {
	return new DOMElement('b', {textContent: text, dir});
}
export function $blockquote(cite=null) {
	return new DOMElement('blockquote', {cite});
}
export function $body() { return new DOMElement('body'); }
export function $br() { return new DOMElement('br'); }
export function $button(text=null, type=null, disabled=null, name=null, value=null) {
	return new DOMElement('button', {textContent: text, type, disabled, name, value});
}
export function $canvas(width=null, height=null) {
	return new DOMElement('canvas', {width, height});
}
export function $caption() { return new DOMElement('caption'); }
export function $cite(text=null) {
	return new DOMElement('cite', {textContent: text});
}
export function $code(text=null) {
	return new DOMElement('code', {textContent: text});
}
export function $col(span=null) {
	return new DOMElement('col', {span});
}
export function $colgroup(span=null) {
	return new DOMElement('colgroup', {span});
}
export function $data(value=null) {
	return new DOMElement('data', {value});
}
export function $datalist() { return new DOMElement('datalist'); }
export function $dd(text=null) {
	return new DOMElement('dd', {textContent: text});
}
export function $del(text=null) {
	return new DOMElement('del', {textContent: text});
}
export function $details() { return new DOMElement('details'); }

export function $dfn(text, title) {
	return new DOMElement('dfn', {textContent: text, title});
}
export function $dialog() { return new DOMElement('dialog'); }
export function $div(text=null) {
	return new DOMElement('div', {textContent: text});
}
export function $dl() { return new DOMElement('dl'); }
export function $dt(text=null) {
	return new DOMElement('dt', {textContent: text});
}
export function $em(text=null) {
	return new DOMElement('em', {textContent: text});
}
export function $embed(src, type=null, width=null, height=null) {
	return new DOMElement('embed', {src, type, width, height});
}
export function $fieldset(name=null, disabled=null, form=null) {
	return new DOMElement('fieldset', {name, disabled, form});
}
export function $figure() { return new DOMElement('figure'); }
export function $figcaption(text) {
	return new DOMElement('figcaption', {textContent: text});
}
export function $footer() { return new DOMElement('footer'); }
export function $form(name=null, action=null, method=null, enctype=null, target=null, autocomplete=null, novalidate=null) {
	return new DOMElement('form', {name, action, method, enctype, target, autocomplete, novalidate});
}
export function $h1(text) {
	return new DOMElement('h1', {textContent: text});
}
export function $h2(text) {
	return new DOMElement('h2', {textContent: text});
}
export function $h3(text) {
	return new DOMElement('h3', {textContent: text});
}
export function $h4(text) {
	return new DOMElement('h4', {textContent: text});
}
export function $h5(text) {
	return new DOMElement('h5', {textContent: text});
}
export function $h6(text) {
	return new DOMElement('h6', {textContent: text});
}
export function $head() { return new DOMElement('head'); }
export function $header() { return new DOMElement('header'); }
export function $hgroup() { return new DOMElement('hgroup'); }
export function $hr() { return new DOMElement('hr'); }
export function $html() { return new DOMElement('html'); }
export function $i(text=null) {
	return new DOMElement('i', {textContent: text});
}
export function $iframe(src, width=null, height=null) {
	return new DOMElement('iframe', {src, width, height});
}
export function $img(src, width=null, height=null) {
	return new DOMElement('img', {src, width, height});
}
export function $input(type, name=null, placeholder=null, multiple=null, accept=null) {
	return new DOMElement('input', {type, name, placeholder, multiple, accept});
}
export function $ins(text) {
	return new DOMElement('ins', {textContent: text});
}
export function $kbd(text) {
	return new DOMElement('kbd', {textContent: text});
}
export function $label(text=null, inputId=null) {
	return new DOMElement('label', {textContent: text, for: inputId});
}
export function $legend(text) {
	return new DOMElement('legend', {textContent: text});
}
export function $li(text=null) {
	return new DOMElement('li', {textContent: text});
}
export function $link(href, rel=null, type=null, media=null, sizes=null) {
	return new DOMElement('link', {href, rel, type, media, sizes});
}
export function $main() { return new DOMElement('main'); }
export function $map(name=null) {
	return new DOMElement('map', {name});
}
export function $mark(text=null) {
	return new DOMElement('mark', {textContent: text});
}
export function $menu() { return new DOMElement('menu'); }
export function $meta(charset=null, name=null, content=null, httpEquiv=null, property=null) {
	return new DOMElement('meta', {charset, name, content, httpEquiv, property});
}
export function $meter(value, min, max, low=null, high=null, optimum=null) {
	return new DOMElement('meter', {value, min, max, low, high, optimum});
}
export function $nav() { return new DOMElement('nav'); }
export function $object(data=null, type=null, width=null, height=null) {
	return new DOMElement('object', {data, type, width, height});
}
export function $ol() {
	return new DOMElement('ol');
}
export function $optgroup(label, disabled=null) {
	return new DOMElement('optgroup', {label, disabled});
}
export function $option(label, value, selected=null, disabled=null) {
	return new DOMElement('option', {label, value, selected, disabled});
}
export function $output(text=null, inputId=null, name=null) {
	return new DOMElement('output', {textContent: text, for: inputId, name});
}
export function $p(text=null) { return new DOMElement('p', {textContent: text}); }
export function $picture() { return new DOMElement('picture'); }
export function $pre(text=null) {
	return new DOMElement('pre', {textContent: text});
}
export function $progress(value, max) {
	return new DOMElement('progress', {value, max});
}
export function $q(text=null, cite=null) {
	return new DOMElement('q', {textContent: text, cite});
}
export function $rp(text) {
	return new DOMElement('rp', {textContent: text});
}
export function $rt(text) {
	return new DOMElement('rt', {textContent: text});
}
export function $ruby(text) {
	return new DOMElement('ruby', {textContent: text});
}
export function $s(text=null) {
	return new DOMElement('s', {textContent: text});
}
export function $samp(text) {
	return new DOMElement('samp', {textContent: text});
}
export function $script(src, type=null, defer=null, async=null) {
	return new DOMElement('script', {src, type, defer, async});
}
export function $search() { return new DOMElement('search'); }
export function $section() { return new DOMElement('section'); }
export function $select(name=null, multiple=null, required=null, disabled=null) {
	return new DOMElement('select', {name, multiple, required, disabled});
}
export function $slot(name=null) {
	return new DOMElement('slot', {name});
}
export function $small(text=null) {
	return new DOMElement('small', {textContent: text});
}
export function $source(src, srcset=null, type=null, media=null, width=null, height=null, sizes=null) {
	return new DOMElement('source', {src, srcset, type, media, width, height, sizes});
}
export function $span(text) {
	return new DOMElement('span', {textContent: text});
}
export function $strong(text=null) {
	return new DOMElement('strong', {textContent: text});
}
export function $style(media=null) {
	return new DOMElement('style', {media});
}
export function $svg() { return new DOMElement('svg'); }
export function $sub(text=null) {
	return new DOMElement('sub', {textContent: text});
}
export function $summary(text=null) {
	return new DOMElement('summary', {textContent: text});
}
export function $sup(text=null) {
	return new DOMElement('sup', {textContent: text});
}
export function $table() { return new DOMElement('table'); }
export function $tbody() { return new DOMElement('tbody'); }
export function $td(text=null, colSpan=null, rowSpan=null) {
	return new DOMElement('td', {textContent: text, colSpan, rowSpan});
}
export function $template(shadowrootmode=null) {
	return new DOMElement('template', {shadowrootmode});
}
export function $textarea(name=null, rows=null, cols=null, placeholder=null, required=null, disabled=null, readonly=null) {
	return new DOMElement('textarea', {name, rows, cols, placeholder, required, disabled, readonly});
}
export function $tfoot() { return new DOMElement('tfoot'); }
export function $th(text=null, colSpan=null, rowSpan=null) {
	return new DOMElement('th', {textContent: text, colSpan, rowSpan});
}
export function $thead() { return new DOMElement('thead'); }
export function $time(datetime=null) {
	return new DOMElement('time', {datetime});
}
export function $title(text) {
	return new DOMElement('title', {textContent: text});
}
export function $tr() { return new DOMElement('tr'); }
export function $track(src, type=null, kind=null, srclang=null, label=null, isDefault=null) {
	return new DOMElement('track', {src, type, kind, srclang, label, default: isDefault});
}
export function $u(text=null) {
	return new DOMElement('u', {textContent: text});
}
export function $ul() { return new DOMElement('ul'); }
export function $var(text) {
	return new DOMElement('var', {textContent: text});
}
export function $video(src, controls=null, autoplay=null, loop=null, muted=null, width=null, height=null) {
	return new DOMElement('video', {src, controls, autoplay, loop, muted, width, height});
}
export function $wbr() { return new DOMElement('wbr'); }

/**
 * Enregistre un CustomElement ou un ShadowElement pour être utilisable dans le dom.
 * @param {string} tag
 */
export function Component(tag) {
	return function(target) {
		customElements.define(tag, target);
	}
}

/** HTMLElement personnalisé avec gestion de la création du dom */
export class CustomElement extends HTMLElement {
	$ref = {}
	$refs = {};

	connectedCallback() {
		this.innerHTML = '';
		this.$render();
	}

	disconnectedCallback() {
		this.$ref = null;
		this.$refs = null;
		this.innerHTML = '';
	}

	/**
	 * Méthode appelée lorsque l'on veut effectuer le rendu du contenu de l'élément.
	 * On y utilisera la méthode $().
	 * $render() est appelé automatiquement lorsque l'élément est ajouté au dom.
	*/
	$render() {}

	/**
	 * Effectue le rendu des élements en paramètre.
	 * @param {...DOMElement} domElements Éléments à ajouter au dom
	 */
	$(...domElements) {
		this.innerHTML = '';
		for(const dom of domElements) this.appendChild(dom.element);
	}

	/**
	 * Émet un évènement de type CustomEvent.
	 * Les données pourront être récupérées via: event.detail
	 * @param {string} eventName Nom de l'event
	 * @param {object} data Données à transmettre
	 */
	$emit(eventName, data) {
		this.dispatchEvent(new CustomEvent(eventName, {detail: data}));
	}
}

/** HTMLElement personnalisé avec gestion de la création du dom (et avec un shadowRoot) */
export class ShadowElement extends HTMLElement {
	$ref = {}
	$refs = {};

	constructor() {
		super();
		this.attachShadow({mode: "open"});
	}

	connectedCallback() {
		this.shadowRoot.innerHTML = '';
		this.$render();
	}

	disconnectedCallback() {
		this.$ref = null;
		this.$refs = null;
		this.shadowRoot.innerHTML = '';
	}

	/**
	 * Méthode appelée lorsque l'on veut effectuer le rendu du contenu de l'élément.
	 * On y utilisera la méthode $().
	 * $render() est appelé automatiquement lorsque l'élément est ajouté au dom.
	*/
	$render() {}

	/**
	 * Effectue le rendu des élements en paramètre.
	 * @param {...DOMElement} domElements Éléments à ajouter au dom
	 */
	$(...domElements) {
		this.shadowRoot.innerHTML = '';
		for(const dom of domElements) this.shadowRoot.appendChild(dom.element);
	}

	/**
	 * Ajoute un style à cet élément.
	 * @param {string} style Feuille de style CSS à ajouter
	 */
	$style(style) {
		const stylesheet = new CSSStyleSheet();
		stylesheet.replaceSync(style);
		this.shadowRoot.adoptedStyleSheets.push(stylesheet);
	}

	/**
	 * Émet un évènement de type CustomEvent.
	 * Les données pourront être récupérées via: event.detail
	 * @param {string} eventName Nom de l'event
	 * @param {object} data Données à transmettre
	 */
	$emit(eventName, data) {
		this.dispatchEvent(new CustomEvent(eventName, {detail: data}));
	}
}
