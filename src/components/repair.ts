import './repair-tips';
import './repair-map';
import Framework7 from '../libs/f7/framework7-bundle';

const RepairComponent = (props, {$f7, $h}) => {
	const choices = [
		{value: 'pc', name: 'Ordinateur', icon: 'laptop_chromebook'},
		{value: 'tablet', name: 'Tablette', icon: 'tablet_android'},
		{value: 'smartphone', name: 'Smartphone', icon: 'phone_android'},
		{value: 'phone', name: 'Téléphone fixe', icon: 'fax'},
		{value: 'smartwatch', name: 'Montre connectée', icon: 'watch'},
		{value: 'peripheral', name: 'Périphérique', icon: 'printer'},
	];

	const chooseType = type => {
		$f7.dialog.create({
			text: 'Savez-vous la cause de la panne ?',
			closeByBackdropClick: true,
			cssClass: 'dialog-repair-failure-cause',
			buttons: [
				{
					text: 'Oui',
					onClick: () => $f7.views[0].router.navigate('/repair/tips', 
						{animate: true, transition: 'f7-cover', history: true, props: {type}}
					),
				},
				{
					text: 'Non',
					onClick: () => $f7.views[0].router.navigate('/repair/map',
						{animate: true, transition: 'f7-cover', history: true}
					),
				},
			]
		}).open();
	}

	return () => $h/*html*/`
		<div>
			<div class="block-title">Type de matériel</div>
			<div id="form-repair-step1" class="list list-strong-ios list-dividers-ios list-outline-ios">
				<ul>
					${choices.map(c => $h/*html*/`
						<li>
							<a class="item-link item-content" @click=${() => chooseType(c.value)}>
								<div class="item-media">
									<span class="material-icons">${c.icon}</span>
								</div>
								<div class="item-inner">
									<div class="item-title">${c.name}</div>
								</div>
							</a>
						</li>
						`)}
				</ul>
			</div>
		</div>
		`;
};

(Framework7 as any).registerComponent('page-repair', RepairComponent);
