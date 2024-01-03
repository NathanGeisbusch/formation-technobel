import Framework7 from '../libs/f7/framework7-bundle';
import logoBeco from '../svg/logo-beco-full.svg';

const HomeComponent = (props, {$h}) => {
	const randomTipIndex = Math.floor(Math.random()*tips.length);

	const paragraphs = [
		{icon: 'lightbulb', title: 'Astuce du jour', content: tips[randomTipIndex]},
		{icon: 'info_circle', title: 'Les Low-Tech', content: defLowTech},
		{icon: 'rocket', title: 'Notre objectif', content: ourGoal},
	];

	return () => $h/*html*/`
		<div>
			${paragraphs.map((p,i) => $h/*html*/`
				<div class=${'card card-raised'+(i===0?' daily-tip':'')}>
					<div class="card-header">
						<span><i class="icon f7-icons">${p.icon}</i> ${p.title}</span>
					</div>
					<div class="card-content card-content-padding line-break">${p.content}</div>
				</div>
			`)}
			<div class="block flex center">
				<svg-container class="logo logo-home" innerHTML=${logoBeco}></svg-container>
			</div>
		</div>
	`;
};

(Framework7 as any).registerComponent('page-home', HomeComponent);

const defLowTech = `\
Les low-tech (ou basses technologies) désignent des solutions techniques simples, \
accessibles, durables et peu coûteuses qui visent à répondre à des besoins humains \
fondamentaux tout en minimisant leur impact sur l'environnement.\n
Contrairement aux technologies de pointe (high-tech) qui nécessitent souvent \
des ressources énergétiques élevées, des matériaux rares et complexes, \
ainsi que des compétences spécialisées pour leur conception, \
les low-tech se caractérisent par leur simplicité, leur convivialité \
et leur capacité à être mises en œuvre localement.`;

const ourGoal = `\
Nous avons créé cette application dans le but de promouvoir \
une consommation responsable de matériel informatique.\n
Nous souhaitons sensibiliser les utilisateurs sur l'impact environnemental \
de leurs choix technologiques, en fournissant des conseils pratiques \
pour prolonger la durée de vie de leurs appareils, \
réduire leur empreinte carbone numérique \
et encourager le recyclage responsable des équipements électroniques.\n
Notre application vise à guider les utilisateurs vers des décisions éclairées \
pour une utilisation plus durable et respectueuse de l'environnement.`;

const tips = [
	`Éteignez et débranchez les chargeurs, ordinateurs portables et \
	autres appareils électroniques lorsqu'ils ne sont pas utilisés.\n
	Même lorsqu'ils ne sont pas en charge, les appareils branchés \
	peuvent toujours consommer de l'énergie en mode veille, ce qui contribue \
	au gaspillage d'énergie.`,

	`Au lieu de constamment mettre à niveau, \
	tirez le meilleur parti de vos appareils actuels.\n
	Une maintenance régulière, \
	des mises à jour logicielles et un stockage approprié peuvent prolonger \
	considérablement la durée de vie des ordinateurs portables, des smartphones \
	et des tablettes.`,

	`Envisagez d'acheter des appareils électroniques d'occasion remis à neuf \
	ou certifiés lorsqu'il est temps de procéder à une mise à niveau.\n
	Ces produits ont été testés et restaurés pour fonctionner comme neufs, \
	réduisant les déchets électroniques et économisant les ressources.`,

	`Lorsque votre appareil rencontre des problèmes mineurs, explorez les options \
	de réparation avant de le remplacer immédiatement.\n
	Les ateliers de réparation locaux ou les kits de réparation \
	de bricolage peuvent souvent résoudre des problèmes tels que des écrans fissurés, \
	des boutons défectueux ou des remplacements de batterie.`,

	`Ajustez les paramètres d'alimentation de vos appareils pour donner la priorité \
	à l'efficacité énergétique.\n
	Assombrissez votre écran, activez les modes d'économie d'énergie et configurez \
	les paramètres de veille pour économiser l'énergie et prolonger la durée de vie \
	de la batterie.`,

	`Réduisez le temps passé devant l'écran \
	en vous engageant dans des activités hors ligne comme lire des livres physiques, \
	jouer à des jeux de société ou passer du temps à l'extérieur.\n
	Cela permet non seulement d'économiser de l'énergie, mais favorise également \
	un équilibre plus sain entre la technologie et les expériences du monde réel.`,

	`Organisez et supprimez régulièrement les fichiers, \
	photos et applications inutiles de vos appareils.\n
	L'élimination de l'encombrement numérique libère non seulement \
	de l'espace de stockage, mais contribue également \
	à des performances plus fluides de l'appareil.`,

	`Si vous êtes un lecteur assidu, envisagez d'utiliser des liseuses \
	ou des tablettes pour accéder à des livres et des documents numériques, \
	ce qui réduira la demande de publications sur papier.`,

	`Collaborez avec votre famille, vos amis ou vos collègues pour partager des appareils \
	électroniques rarement utilisés, tels que des imprimantes ou des projecteurs.\n
	Emprunter des articles en cas de besoin réduit le nombre total d'appareils utilisés.`,

	`Lorsque vos produits informatiques arrivent en fin de vie, \
	recyclez-les correctement.\n
	Recherchez des programmes locaux de recyclage \
	des déchets électroniques ou des lieux de dépôt pour vous assurer \
	que vos appareils sont éliminés de manière écologique.`,

	`Rationalisez l'utilisation de vos applications en examinant et \
	en désinstallant régulièrement les applications \
	dont vous n'avez plus besoin.\n
	Moins d'applications signifie moins de \
	consommation d'énergie et des performances de l'appareil \
	potentiellement plus rapides.`,

	`Optez pour des solutions low-tech telles que des ordinateurs portables \
	ou des tableaux blancs réutilisables pour la prise de notes et le brainstorming, \
	réduisant ainsi le besoin d'utiliser constamment des appareils numériques.`,
];
