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