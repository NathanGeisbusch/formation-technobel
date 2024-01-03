import logoBeco from '../svg/logo-beco.svg';
import * as img from '../assets/img';

export default (props, { $f7, $onMounted, $update, $h }) => {
	let mapSelected = false;
	let mapSelectedValue = null;
	let repairMap = img.repair_map_1_webp;
	let descData = {
		title: 'Réparateur professionnel', tags: ['PC', 'Laptop', 'Écran'],
		town: mapSelectedValue ? mapSelectedValue.split(' ')[1] : '',
	};

	$onMounted(() => {
		// Searchbar Autocomplete
		const autocompleteSearchbar = $f7.autocomplete.create({
			openIn: 'dropdown',
			inputEl: '#searchbar-repair-map input[type="search"]',
			dropdownPlaceholderText: 'Essayer "6700" ou "Arlon"',
			source: function(query, render) {
				const results = [];
				if(query.length === 0) { render(results); return; }
				// Find matched items
				for(var i = 0; i < towns.length; i++) {
					if(towns[i].toLowerCase().indexOf(query.toLowerCase()) >= 0) results.push(towns[i]);
				}
				render(results);
			}
		});

		const searchbar = $f7.searchbar.create({
			el: '#searchbar-repair-map',
			customSearch: true,
			on: {
				search: function(sb, query) {
					mapSelected = towns.includes(query);
					console.log(query, mapSelected);
					mapSelectedValue = mapSelected ? query : null;
					repairMap = mapSelected ? img.repair_map_2_webp : img.repair_map_1_webp;
					$update();
				}
			}
		})
	});

	const openDesc = () => {
		if(!mapSelected) return;
		$f7.sheet.open('#repair-map-desc', true);
		$update();
	}

	return () => $h/*html*/`
		<div class="page">
			${/* Navbar */''}
			<div class="navbar fix-border">
				<div class="navbar-bg"></div>
				<div class="navbar-inner">
					${/* Navbar Icons */''}
					<div class="left">
						<a class="link back">
							<i class="icon icon-back"></i>
							<span class="if-not-md">Back</span>
						</a>
					</div>
					<div class="title">Carte des repair cafés</div>
					<div class="right">
						<svg-container class="logo" innerHTML=${logoBeco}></svg-container>
					</div>
					${/* Sub-Navbar */''}
					<div class="subnavbar">
						<form class="searchbar" id="searchbar-repair-map">
							<div class="searchbar-inner">
								<div class="searchbar-input-wrap">
									<input type="search" placeholder="Entrez votre code postal ou votre ville"/>
									<i class="searchbar-icon"></i>
									<span class="input-clear-button"></span>
								</div>
								<span class="searchbar-disable-button">Annuler</span>
							</div>
						</form>
					</div>
				</div>
			</div>
			${/* Model Desc */''}
			<div id="repair-map-desc" class="sheet-modal">
				${/* Bottom toolbar for top sheet */''}
				<div class="toolbar toolbar-bottom">
					<div class="toolbar-inner">
						<div class="left" style="margin-left: 20px; font-size: 16px;">
							Dépannage informatique ${descData.town}
						</div>
						<div class="right">
							<a href="#" class="link sheet-close">Fermer</a>
						</div>
					</div>
				</div>
				${/* Sheet Modal Inner */''}
				<div class="sheet-modal-inner">
					${/* Sheet Modal content */''}
					<div class="block block-strong-ios block-outline-ios marginV24">
						<span><i class="icon f7-icons">info_circle</i> ${descData.title}</span>
					</div>
					<div class="block block-strong-ios block-outline-ios marginV24">
						<span><i class="icon f7-icons">placemark</i> ${mapSelectedValue}, Belgique</span>
					</div>
					<div class="block block-strong-ios block-outline-ios marginV24">
						${descData.tags.map(t => $h/*html*/`
							<div class="chip">
								<div class="chip-label">${t}</div>
							</div>
							`)}
					</div>
				</div>
			</div>
			${/* Page Content */''}
			<div class="page-content" style="overflow: hidden;">
					<img src=${repairMap} class="repair-map" onClick=${openDesc}/>
			</div>
		</div>
		`;
};

const towns = [
	'6700 Arlon',
	'6700 Bonnert',
	'6700 Heinsch',
	'6700 Toernich',
	'6717 Attert',
	'6717 Nobressart',
	'6717 Nothomb',
	'6717 Thiaumont',
	'6717 Tontelange',
	'6720 Habay',
	'6720 Habay-la-Neuve',
	'6720 Hachy',
	'6721 Anlier',
	'6723 Habay-la-Vieille',
	'6724 Houdemont',
	'6724 Rulles',
	'6740 Étalle',
	'6780 Hondelange',
	'6780 Messancy',
	'6780 Wolkrange',
	'6790 Aubange',
	'6791 Athus',
	'6792 Halanzy',
	'6792 Rachecourt',
	'6800 Libramont',
	'6800 Recogne',
	'6810 Chiny',
	'6810 Izel',
	'6810 Jamoigne',
	'6820 Florenville',
	'6830 Bouillon',
	'6860 Léglise',
	'6880 Bertrix',
	'6887 Herbeumont',
	'6890 Libin',
	'6890 Transinne',
	'6900 Marche-en-Famenne',
];
