import './buy-list';
import Framework7 from '../libs/f7/framework7-bundle';

const BuyComponent = (props, {$f7, $update, $h}) => {
	const choices = [
		{value: 'pc', name: 'Ordinateur', icon: 'material:laptop_chromebook'},
		{value: 'tablet', name: 'Tablette', icon: 'material:tablet_android'},
		{value: 'smartphone', name: 'Smartphone', icon: 'material:phone_android'},
		{value: 'phone', name: 'Téléphone fixe', icon: 'material:fax'},
		{value: 'smartwatch', name: 'Montre connectée', icon: 'material:watch'},
		{value: 'peripheral', name: 'Périphérique', icon: 'material:printer'},
	];
	let type = choices[0].value;

	const nextPage = () => {
		const type = $f7.form.convertToData('#form-buy-step1')['type'];
		$f7.views[0].router.navigate('/buy/list', 
			{animate: true, transition: 'f7-cover', history: true, props: {type}}
		);
	}

	const handleSmartSelectChange = (event) => {
		const selectedValue = event.target.value;
		console.log('Selected value:', selectedValue);
		type = selectedValue;
		$update();
	};

	//PC
	const vdomPC = [$h/*html*/`
		<li key="pc">
			<a class="item-link smart-select smart-select-init"
			data-close-on-select="true" data-css-class="smart-select-no-icon">
				<select name="utilisation">
					<option value="bureautique">Bureautique</option>
					<option value="réseaux">Réseaux sociaux</option>
					<option value="photo">Photo / Video</option>
					<option value="montage">Montage</option>
					<option value="oldgame">Jeu ancien</option>
					<option value="newgame">Jeu récent</option>
				</select>
				<div class="item-content">
					<div class="item-inner">
						<div class="item-title">Utilisation</div>
					</div>
				</div>
			</a>
		</li>
	`, $h/*html*/`
		<li key="pc">
			<a class="item-link smart-select smart-select-init" data-open-in="sheet"
			data-sheet-close-link-text="Fermer">
				<select name="os">
					<option value="windows" selected="selected">Windows</option>
					<option value="mac">Mac</option>
					<option value="linux">Linux</option>
				</select>
				<div class="item-content">
					<div class="item-inner">
						<div class="item-title">Système d'exploitation</div>
					</div>
				</div>
			</a>
		</li>
	`, $h/*html*/`
		<li key="pc">
			<div class="item-content item-input item-content-slider">
				<div class="item-inner">
					<div class="item-title">
						<span>Budget</span>
						<small> (€)</small>
					</div>
					<div class="item-input-wrap block slider-block">
						<div class="range-slider range-slider-init" data-min="0" data-max="2500"
						data-label="true" data-step="10" @rangeChange=${e=>undefined}
						data-dual="true" data-value-left="380" data-value-right="750"
						data-scale="true" data-scale-steps="10"/>
					</div>
				</div>
			</div>
		</li>
	`, $h/*html*/`
		<li key="pc">
			<div class="item-content item-input item-content-slider">
				<div class="item-inner">
					<div class="item-title">
						<span>Fréquence d'utilisation</span>
						<small> (jours par semaine)</small>
					</div>
					<div class="item-input-wrap block slider-block">
						<div class="range-slider range-slider-init" data-min="0" data-max="7"
						data-label="true" data-step="1" @rangeChange=${e=>undefined}
						data-scale="true" data-scale-steps="7"/>
					</div>
				</div>
			</div>
		</li>
	`];

	//SMARTPHONE
	const vdomSP = [$h/*html*/`
		<li key="smartphone">
			<a class="item-link smart-select smart-select-init"
			data-close-on-select="true" data-css-class="smart-select-no-icon">
				<select name="utilisation">
					<option value="réseaux" selected="selected">Réseaux sociaux</option>
					<option value="photo">Photo / Video</option>
					<option value="mobilegame">Jeu</option>
				</select>
				<div class="item-content">
					<div class="item-inner">
						<div class="item-title">Utilisation</div>
					</div>
				</div>
			</a>
		</li>
	`, $h/*html*/`
		<li key="smartphone">
			<a class="item-link smart-select smart-select-init" data-open-in="sheet"
			data-sheet-close-link-text="Fermer">
				<select name="os">
					<option value="android" selected="selected">Android</option>
					<option value="ios">iOS</option>
					<option value="other">Autre</option>
				</select>
				<div class="item-content">
					<div class="item-inner">
						<div class="item-title">Système d'exploitation</div>
					</div>
				</div>
			</a>
		</li>
	`, $h/*html*/`
		<li key="smartphone">
			<a class="item-link smart-select smart-select-init" data-open-in="sheet"
			data-sheet-close-link-text="Fermer">
				<select name="touches">
					<option value="tactile" selected="selected">Tactile</option>
					<option value="big_buttons">Grandes touches</option>
				</select>
				<div class="item-content">
					<div class="item-inner">
						<div class="item-title">Touches</div>
					</div>
				</div>
			</a>
		</li>
	`, $h/*html*/`
		<li key="smartphone">
			<a class="item-link smart-select smart-select-init" data-open-in="sheet"
			data-sheet-close-link-text="Fermer">
				<select name="protections" multiple>
					<option value="antichoc">Étui antichoc</option>
					<option value="étanche">Étanchéité</option>
					<option value="anti-rayure">Revêtements anti-rayures</option>
					<option value="température">Résistance aux températures extrêmes</option>
					<option value="anti-magnétique">Blindage contre les interférences magnétiques</option>
				</select>
				<div class="item-content">
					<div class="item-inner">
						<div class="item-title">Protections</div>
					</div>
				</div>
			</a>
		</li>
	`, $h/*html*/`
		<li key="smartphone">
			<div class="item-content">
				<div class="item-inner">
					<div class="item-title">Clapet</div>
					<div class="item-after">
						<label class="toggle toggle-init">
							<input type="checkbox" name="clapet"/>
							<i class="toggle-icon"></i>
						</label>
					</div>
				</div>
			</div>
		</li>
	`, $h/*html*/`
		<li key="smartphone">
			<div class="item-content">
				<div class="item-inner">
					<div class="item-title">Accès internet</div>
					<div class="item-after">
						<label class="toggle toggle-init">
							<input type="checkbox" name="internet"/>
							<i class="toggle-icon"></i>
						</label>
					</div>
				</div>
			</div>
		</li>
	`, $h/*html*/`
		<li key="smartphone">
			<div class="item-content item-input item-content-slider">
				<div class="item-inner">
					<div class="item-title">
						<span>Budget</span>
						<small> (€)</small>
					</div>
					<div class="item-input-wrap block slider-block">
						<div class="range-slider range-slider-init" data-min="0" data-max="1000"
						data-label="true" data-step="10" @rangeChange=${e=>undefined}
						data-dual="true" data-value-left="380" data-value-right="750"
						data-scale="true" data-scale-steps="10"/>
					</div>
				</div>
			</div>
		</li>
	`, $h/*html*/`
		<li key="smartphone">
			<div class="item-content item-input item-content-slider">
				<div class="item-inner">
					<div class="item-title">
						<span>Capacité minimum</span>
						<small> (gigaoctets)</small>
					</div>
					<div class="item-input-wrap block slider-block">
						<div class="range-slider range-slider-init" data-min="0" data-max="1024"
						data-label="true" data-step="8" data-value="0" @rangeChange=${e=>undefined}
						data-scale="true" data-scale-steps="8" data-scale-sub-steps="4"/>
					</div>
				</div>
			</div>
		</li>
	`];

	return () => $h/*html*/`
		<div>
			<form id="form-buy-step1" class="list list-strong-ios list-dividers-ios list-outline-ios">
				<ul>
					${/*Type*/''}
					<li>
						<a id="smart-select-buy-type" class="item-link smart-select smart-select-init"
						data-close-on-select="true" data-css-class="smart-select-no-icon">
							<select name="type" @change=${handleSmartSelectChange}>
								${choices.map(c => $h/*html*/`
									<option value=${c.value} data-option-icon=${c.icon}>${c.name}</option>
								`)}
							</select>
							<div class="item-content">
								<div class="item-inner">
									<div class="item-title">Type de matériel</div>
								</div>
							</div>
						</a>
					</li>
					${type === 'pc' ? vdomPC : type === 'smartphone' ? vdomSP : $h/*html*/`<li/>`}
				</ul>
			</form>
			<div class="block">
				<button class="button button-tonal" @click=${nextPage}>Rechercher</button>
			</div>
		</div>
	`;
};

(Framework7 as any).registerComponent('page-buy', BuyComponent);
