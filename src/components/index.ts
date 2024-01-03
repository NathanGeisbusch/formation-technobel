import './home';
import './repair';
import './buy';
import logoBeco from '../svg/logo-beco.svg';

let openLoaderOpened = false;
let selectedTab = 'empty';

export default (props, { $update, $f7, $h, $onMounted }) => {
	let data = [];

	const setTab = (newItem) => {
		selectedTab = newItem;
		$f7.tab.show('#tab-'+newItem);
		$f7.sheet.close('.menu-sheet-top', true);
		$update();
	}

	const openLoader = () => {
		openLoaderOpened = true;
		$f7.dialog.preloader('Chargement...');
		setTimeout(() => {
			data = ['hello world'];
			setTab('home');
			$f7.dialog.close();
			$update();
		}, 1000);
	}

	const openRepairMap = action => {
		$f7.sheet.close('.menu-sheet-top', true);
		$update();
		$f7.views[0].router.navigate('/repair/map',
			{animate: true, transition: 'f7-cover', history: true}
		);
	}

	$onMounted(() => {
		if(!openLoaderOpened) openLoader();
		else setTab(selectedTab);
	});

	return () => $h/*html*/`
	<div class="page">
			${/* Navbar */''}
			<div class="navbar fix-border">
				<div class="navbar-bg"></div>
				<div class="navbar-inner">
					${/* Navbar Icons */''}
					<div class="left">
						<svg-container class="logo" innerHTML=${logoBeco}></svg-container>
					</div>
					<div class="title"></div>
					<div class="right">
						<a class="link icon-only searchbar-enable" data-searchbar=".searchbar">
							<i class="icon f7-icons">search</i>
						</a>
						<a class="link sheet-open" data-sheet=".menu-sheet-top">
							<i class="icon f7-icons">line_horizontal_3</i>
						</a>
					</div>
					${/* Searchbar */''}
					<form class="searchbar searchbar-expandable searchbar-init"
					data-search-container=".search-list" data-search-in=".item-title">
						<div class="searchbar-inner">
							<div class="searchbar-input-wrap">
								<input type="search" placeholder="Recherche" />
								<i class="searchbar-icon"></i>
								<span class="input-clear-button"></span>
							</div>
							<span class="searchbar-disable-button">Annuler</span>
						</div>
					</form>
				</div>
			</div>

			${/* Tabs header */''}
			<div class="toolbar tabbar-icons toolbar-bottom toolbar-actions fix-border">
				<div class="toolbar-inner">
					<a href="#tab-home" class="tab-link" @click=${()=>setTab('home')}>
						<i class="icon f7-icons">house</i>
						<span class="tabbar-label">Accueil</span>
					</a>
					<a href="#tab-repair" class="tab-link" @click=${()=>setTab('repair')}>
						<i class="icon f7-icons">wrench</i>
						<span class="tabbar-label">Réparation</span>
					</a>
					<a href="#tab-buy" class="tab-link" @click=${()=>setTab('buy')}>
						<i class="icon f7-icons">money_euro</i>
						<span class="tabbar-label">Achat</span>
					</a>
					<a class="tab-link" @click=${()=>undefined&&setTab('settings')}>
						<i class="icon f7-icons">gear_alt</i>
						<span class="tabbar-label">Options</span>
					</a>
				</div>
			</div>

			${/* Tabs content */''}
			<div class="tabs">
				<div class="page-content tab tab-active" id="tab-empty">
					<div class="card card-raised skeleton-text">
						<div class="card-header">Card Header</div>
						<div class="card-content card-content-padding">
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi
							lobortis et massa ac interdum. Cras consequat felis at consequat hendrerit.
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi
							lobortis et massa ac interdum. Cras consequat felis at consequat hendrerit.
						</div>
					</div>
					<div class="card card-raised skeleton-text">
						<div class="card-header">Card Header</div>
						<div class="card-content card-content-padding">
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi
							lobortis et massa ac interdum. Cras consequat felis at consequat hendrerit.
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi
							lobortis et massa ac interdum. Cras consequat felis at consequat hendrerit.
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi
							lobortis et massa ac interdum. Cras consequat felis at consequat hendrerit.
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi
							lobortis et massa ac interdum. Cras consequat felis at consequat hendrerit.
						</div>
					</div>
				</div>
				<div class="page-content tab" id="tab-home">
					<page-home/>
				</div>
				<div class="page-content tab" id="tab-repair">
					<page-repair/>
				</div>
				<div class="page-content tab" id="tab-buy">
					<page-buy/>
				</div>
				<div class="page-content tab" id="tab-settings">
					<div class="block">
						<p>Options</p>
					</div>
				</div>
			</div>

			${/*  Modal Hamburger Menu */''}
			<div class="sheet-modal sheet-modal-top menu-sheet-top">
				<div class="sheet-modal-inner">
					<div class="page-content">
							<div class="list list-strong-ios list-outline-ios media-list menu-list fix">
								<ul>
									${/* Menu Accueil */''}
									<li>
										<a class=${"flex item-content item-link "+(selectedTab === 'home' ? 'item-selected' : '')}
										@click=${()=>setTab('home')}>
											<div class="item-media">
												<i class="icon f7-icons">house</i>
											</div>
											<div class="item-inner">
												<div class="item-title-wrap">
													<div class="item-title">Accueil</div>
												</div>
												<div class="item-subtitle">Diverses informations sur les low-tech.</div>
											</div>
										</a>
									</li>
									${/* Menu Réparation */''}
									<li>
										<a class=${"flex item-content item-link "+(selectedTab === 'repair' ? 'item-selected' : '')}
										@click=${()=>setTab('repair')}>
											<div class="item-media">
												<i class="icon f7-icons">wrench</i>
											</div>
											<div class="item-inner">
												<div class="item-title-wrap">
													<div class="item-title">Réparation</div>
												</div>
												<div class="item-subtitle">Un objet à réparer ? Suivez nos conseils !</div>
											</div>
										</a>
									</li>
									${/* Menu Achat */''}
									<li>
										<a class=${"flex item-content item-link "+(selectedTab === 'buy' ? 'item-selected' : '')}
										@click=${()=>setTab('buy')}>
											<div class="item-media">
												<i class="icon f7-icons">money_euro</i>
											</div>
											<div class="item-inner">
												<div class="item-title-wrap">
													<div class="item-title">Achat</div>
												</div>
												<div class="item-subtitle">Vérifiez s'il existe une alternative plus écologique !</div>
											</div>
										</a>
									</li>
									${/* Menu Forum */''}
									<li>
										<a class=${"flex item-content item-link "+(selectedTab === 'forum' ? 'item-selected' : '')}
										@click=${()=>undefined}>
											<div class="item-media">
												<i class="icon f7-icons">person_3</i>
											</div>
											<div class="item-inner">
												<div class="item-title-wrap">
													<div class="item-title">Forum</div>
												</div>
												<div class="item-subtitle">Échangez avec d'autre personnes !</div>
											</div>
										</a>
									</li>
									${/* Menu Carte Repair Cafés */''}
									<li>
										<a class=${"flex item-content item-link "+(selectedTab === 'forum' ? 'item-selected' : '')}
										@click=${openRepairMap}>
											<div class="item-media">
												<i class="icon f7-icons">map</i>
											</div>
											<div class="item-inner">
												<div class="item-title-wrap">
													<div class="item-title">Carte des repair cafés</div>
												</div>
												<div class="item-subtitle">Cherchez des repair cafés dans votre région !</div>
											</div>
										</a>
									</li>
									${/* Menu Options */''}
									<li>
										<a class=${"flex item-content item-link "+(selectedTab === 'settings' ? 'item-selected' : '')}
										@click=${()=>undefined&&setTab('settings')}>
											<div class="item-media">
												<i class="icon f7-icons">gear_alt</i>
											</div>
											<div class="item-inner">
												<div class="item-title-wrap">
													<div class="item-title">Options</div>
												</div>
												<div class="item-subtitle">Personnaliser votre expérience !</div>
											</div>
										</a>
									</li>
								</ul>
							</div>
					</div>
				</div>
			</div>

		</div>
	`;
};