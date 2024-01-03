import logoBeco from '../svg/logo-beco.svg';

export default (props, { $h }) => {
	console.log(props.type);

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
					<div class="title">Solutions courantes</div>
					<div class="right">
						<svg-container class="logo" innerHTML=${logoBeco}></svg-container>
					</div>
				</div>
			</div>
			${/* Page Content */''}
			<div class="page-content">
				<div class="list list-strong list-outline-ios list-dividers-ios inset-md accordion-list">
					<ul>
						${tips.map(p => $h/*html*/`
							<li class="accordion-item">
								<a class="item-link item-content">
									<div class="item-inner">
										<div class="item-title">${p.title}</div>
									</div>
								</a>
								<div class="accordion-item-content">
									<div class="block">
										<p>${p.content}</p>
									</div>
								</div>
							</li>
						`)}
					</ul>
				</div>
			</div>
		</div>
	`;
};

const tips = [
	{title: 'Redémarrage', content: `\
		Parfois, un simple redémarrage peut résoudre de nombreux problèmes logiciels temporaires.\
	`},
	{title: 'Vérification des câbles et connexions', content: `\
		Assurez-vous que tous les câbles et les connexions sont bien branchés et en bon état.\
	`},
	{title: 'Scan antivirus et anti-malware', content: `\
		Effectuez une analyse complète à l'aide d'un logiciel antivirus et anti-malware pour détecter \
		et supprimer les éventuelles menaces.\
	`},
	{title: 'Mise à jour des pilotes', content: `\
		Mettez à jour les pilotes de votre matériel, notamment la carte graphique, la carte mère, le Wi-Fi, etc.\n
		Cela peut résoudre des problèmes de performances ou de compatibilité.\
	`},
	{title: 'Nettoyage du disque dur', content: `\
		Libérez de l'espace sur votre disque dur en supprimant les fichiers inutiles et temporaires.\n
		Cela peut améliorer les performances générales.\
	`},
	{title: 'Défragmentation du disque dur', content: `\
		Si vous utilisez un disque dur traditionnel (non un SSD), \
		la défragmentation peut améliorer l'efficacité de l'accès aux fichiers.\
	`},
	{title: 'Réparation du système d\'exploitation', content: `\
		Sur Windows, vous pouvez utiliser l'outil de réparation de démarrage \
		ou l'outil "SFC /scannow" pour réparer les fichiers système corrompus.\
	`},
	{title: 'Mode sans échec', content: `\
		Démarrez votre ordinateur en mode sans échec pour isoler les problèmes logiciels ou matériels.\n
		Cela désactive les programmes et pilotes tiers.\
	`},
	{title: 'Restauration du système', content: `\
		Si le problème est apparu récemment, restaurez votre système à un point antérieur \
		où tout fonctionnait correctement.\
	`},
	{title: 'Réinitialisation d\'usine', content: `\
		Si les problèmes persistent et que vous avez sauvegardé vos données, \
		vous pourriez envisager de réinitialiser votre ordinateur aux paramètres d'usine.\
	`},
	{title: 'Test de la mémoire RAM', content: `\
		Utilisez des outils de diagnostic de mémoire pour vérifier si la RAM est défectueuse.\
	`},
	{title: 'Diagnostic du disque dur', content: `\
		Vérifiez l'état de votre disque dur à l'aide d'outils comme CrystalDiskInfo.\n
		Un disque défaillant peut provoquer des erreurs et des plantages.\
	`},
	{title: 'Problèmes de surchauffe', content: `\
		Assurez-vous que votre ordinateur ne surchauffe pas en nettoyant les ventilateurs \
		et les dissipateurs thermiques, et en surveillant les températures avec des logiciels appropriés.\
	`},
	{title: 'Assistance professionnelle', content: `\
		Si vous n'êtes pas sûr de la réparation à effectuer ou si le problème est matériel, \
		consultez un professionnel de la réparation d'ordinateurs.\
	`},
];