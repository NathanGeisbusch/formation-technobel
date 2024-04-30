# Implémentation d'un composant web de dessin

![](doc/demo.webp?raw=true)


## Contexte

Le but de l'exercice était l'utilisation des évènements en Javascript. J'ai choisi de le réaliser sous la forme d'une zone de dessin pour exploiter les différents évènements liés au clavier et aux mouvements de la souris.

## DOM

Pour la réalisation de ce petit exercice, j'ai créé une [librairie](https://github.com/NathanGeisbusch/formation-technobel/blob/drawing-canvas/src/libs/custom-element.js) facilitant la création du DOM HTML en Javascript.

```js
this.$(
    $h1('Dessin'),
    $div().class('container').add(
        $div().class('layer hide').add($svg()),
        $canvas(720,480)
        .listen('mousedown', ev => this.startDrawing(ev))
        .listen('mousemove', ev => this.draw(ev))
        .listen('mouseup',    _ => this.stopDrawing())
        .listen('mouseout',   _ => this.stopDrawing()),
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
```

Et j'ai créé un décorateur pour enregistrer les composants HTML personnalisés.

```ts
export function Component(tag) {
    return function(target) {
        customElements.define(tag, target);
    }
}

@Component('drawing-canvas')
class DrawingCanvas extends ShadowElement {}
```

## Pug

Pug permet de générer des pages HTML statiques à partir d'une syntaxe plus concise, et permet également d'importer d'autres fichiers ainsi que d'injecter des variables d'environnement et exécuter du code Javascript à la compilation. J'ai utilisé cette syntaxe pour la création de mon [CV](https://github.com/NathanGeisbusch/formation-technobel/tree/cv).

```pug
doctype html
html(lang="fr")
    head
        meta(charset="UTF-8")
        meta(name="viewport", content="width=device-width, initial-scale=1.0")
        title #{TITLE}
        style !{CSS}
    body
        drawing-canvas
        script !{JS}
```

## Compilation

J'ai également créé une [autre librairie](https://github.com/NathanGeisbusch/formation-technobel/blob/drawing-canvas/bundler.js)
afin d'avoir un outil plus simple, léger et rapide que d'autres solutions tel Webpack pour générer un seul fichier contenant l'entièreté de mon application et ainsi la partager facilement à des fins de démonstration (voir [CV](https://github.com/NathanGeisbusch/formation-technobel/tree/cv) et [Mockup Beco](https://github.com/NathanGeisbusch/formation-technobel/tree/mockup-beco)).

Elle utilise directement les compilateurs Typescript, SCSS et Pug, et inclue un mode "watch" permettant de redéclencher la compilation de l'application à chaque fichier modifié (ne recompile que les fichiers concernés et ne réécrit pas les fichiers de sortie inchangés).

```js
const {Bundler, PugLoader, TsLoader, ScssLoader} = require('./bundler.js');

const SRC = './src/';
const DIST = './dist/';

new Bundler().add(
    new PugLoader(SRC+'index.pug')
        .outputHtml(DIST+'index.html')
        .data('TITLE', 'Dessin')
        .data('JS', new TsLoader(SRC+'index.ts').decorators())
        .data('CSS', new ScssLoader(SRC+'index.scss'))
).build();
```
