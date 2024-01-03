import Framework7 from './libs/f7/framework7-bundle';
import PageIndex from './components/index';
import PageRepairTips from './components/repair-tips';
import PageRepairMap from './components/repair-map';
import PageBuyList from './components/buy-list';
import PageBuyDetail from './components/buy-detail';

const app: any = new Framework7({
	el: '#app',
	name: 'App',
	panel: {swipe: true},
	url: 'http://localhost:8000/',
	routes: [
		{path: '/', component: PageIndex},
		{path: '/repair/tips', component: PageRepairTips},
		{path: '/repair/map', component: PageRepairMap},
		{path: '/buy/list', component: PageBuyList},
		{path: '/buy/detail', component: PageBuyDetail},
  ],
	sheet: {
		closeByBackdropClick: true,
		closeByOutsideClick: true,
		closeOnEscape: true,
		backdrop: true,
	},
});
app.views.create('.view-main');
