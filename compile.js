const {Bundler, FontLoader, ImgLoader, PugLoader, TsLoader, ScssLoader} = require('./bundler.js');

const SRC = './src/';
const DIST = './dist/';

new Bundler().add(
	new FontLoader(SRC+'fonts/**/*.{ttf,woff,woff2}')
		.outputScss(SRC+'assets/fonts.scss'),
	new ImgLoader(SRC+'img/**/*.{png,jpg,jpeg,gif,webp,avif}')
		.outputScss(SRC+'assets/img.scss')
		.outputTs(SRC+'assets/img.ts'),
	new PugLoader(SRC+'mockup.pug')
		.outputHtml(DIST+'mockup.html')
		.data('JS', new TsLoader(SRC+'mockup.ts').minify(true))
		.data('CSS', new ScssLoader(SRC+'mockup.scss'))
		.data('APP', new PugLoader(SRC+'app.pug')
			//.outputHtml(DIST+'app.html')
			.data('JS', new TsLoader(SRC+'app.ts').minify(true))
			.data('CSS', new ScssLoader(SRC+'app.scss'))
		),
).watch('./src/**/*', ['src/assets', 'src/assets/**/*']);
