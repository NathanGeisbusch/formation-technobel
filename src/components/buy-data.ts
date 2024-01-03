import choixBecoSvg from '../svg/choix-beco-tick.svg';
import choixCommuSvg from '../svg/choix-commu.svg';
import bonusEconoSvg from '../svg/economic.svg';
import fairTradeSvg from '../svg/fair-trade.svg';
import ecoEcoSvg from '../svg/green-economic.svg';
import ecoSvg from '../svg/eco-plant.svg';
import bonusPerfSvg from '../svg/perf-speedometer.svg';
import produitReconditionneSvg from '../svg/recycle.svg';
import bonusRepairSvg from '../svg/repairing-icon.svg';
import * as img from '../assets/img';

export const logos = [
	{id: 'choix-beco', svg: choixBecoSvg, name: 'Coup de coeur BECO', iconClass: "badge color-purple"},
	{id: 'choix-commu', svg: choixCommuSvg, name: 'Coup de coeur de la communauté', iconClass: "badge color-slateblue"},
	{id: 'bonus-econo', svg: bonusEconoSvg, name: 'Bonus économie', iconClass: "badge color-blue"},
	{id: 'fair-trade', svg: fairTradeSvg, name: 'Issu du commerce équitable', iconClass: "badge color-orange"},
	{id: 'eco-eco', svg: ecoEcoSvg, name: 'Meilleur rapport économique-écologique', iconClass: "badge color-teal"},
	{id: 'bonus-ecolo', svg: ecoSvg, name: 'Bonus écologique', iconClass: "badge color-green"},
	{id: 'bonus-perf', svg: bonusPerfSvg, name: 'Bonus performance', iconClass: "badge color-yellow"},
	{id: 'prod-recond', svg: produitReconditionneSvg, name: 'Produit reconditionné', iconClass: "badge color-beige"},
	{id: 'bonus-repair', svg: bonusRepairSvg, name: 'Bonus réparabilité', iconClass: "badge color-gray"},
];

export const badges = {};
for(const logo of logos) badges[logo.id] = logo;

export const products = [
	{img: img.produit1_jpg, name: 'EcoTech Workstation', price: '150 €', badges: ['choix-beco', 'fair-trade']},
	{img: img.produit4_jpg, name: 'EssentialCores System', price: '175 €', badges: ['choix-commu', 'eco-eco']},
	{img: img.produit2_jpg, name: 'BioSense Computer', price: '200 €', badges: ['fair-trade', 'bonus-repair', 'prod-recond']},
	{img: img.produit5_jpg, name: 'UltraBasix Pro', price: '25 €', badges: ['bonus-ecolo', 'bonus-econo', 'prod-recond']},
	{img: img.produit3_jpg, name: 'GreenWave Machine', price: '350 €', badges: ['bonus-perf']},
];

export const productTechnicalSheet = [
	{component: 'Processeur', properties: [
		{key: 'Nom', value: 'EcoCore X1'},
		{key: 'Nombre de coeurs', value: '4'},
		{key: 'Fréquence', value: '1,2 GHz'},
		{key: 'Cache', value: '32 Mo de mémoire cache L3'},
		{key: 'Consommation d\'énergie', value: '15 watts'},
	]},
	{component: 'Carte graphique', properties: [
		{key: 'Nom', value: 'GreenWave G1'},
		{key: 'VRAM', value: '2 Go GDDR3'},
		{key: 'Fréquence', value: '800 MHz'},
		{key: 'Consommation d\'énergie', value: '30 watts'},
	]},
	{component: 'Carte mère', properties: [
		{key: 'Nom', value: 'EnergySaver E-Board'},
		{key: 'Format', value: 'Micro-ATX'},
		{key: 'Prise en charge CPU', value: 'Socket LGA 1700'},
		{key: 'Emplacements RAM', value: '4 x DDR5'},
		{key: 'Connecteurs PCIe', value: '3 x PCIe 5.0 x16'},
		{key: 'Stockage', value: '6 x ports SATA 6 Gb/s,\n3 x M.2 avec support NVMe'},
		{key: 'Ports USB', value: '2x USB-A 3.0, 4x USB-C 3.1'},
		{key: 'Consommation d\'énergie', value: '10 watts'},
	]},
	{component: 'Mémoire RAM', properties: [
		{key: 'Nom', value: 'EcoBoost PowerLite DDR4'},
		{key: 'Capacité', value: '8 Go (2 x 4 Go)'},
		{key: 'Vitesse', value: '1600 MHz'},
		{key: 'Latence', value: 'CL16'},
		{key: 'Consommation d\'énergie', value: '1.2 watts'},
	]},
	{component: 'SSD', properties: [
		{key: 'Nom', value: 'GreenDrive S2'},
		{key: 'Capacité', value: '128 Go'},
		{key: 'Interface', value: 'NVMe M.2'},
		{key: 'Vitesse de lecture', value: '2500 Mo/s'},
		{key: 'Vitesse de écriture', value: '2150 Mo/s'},
		{key: 'Consommation d\'énergie', value: '1.2 watts'},
	]},
	{component: 'Disque dur', properties: [
		{key: 'Nom', value: 'EcoSpin E1'},
		{key: 'Capacité', value: '1 To'},
		{key: 'Interface', value: 'SATA 6 Gb/s'},
		{key: 'Vitesse de rotation', value: '5400 tr/min'},
		{key: 'Vitesse de lecture', value: '120 Mo/s'},
		{key: 'Vitesse de écriture', value: '100 Mo/s'},
		{key: 'Consommation d\'énergie', value: '6 watts'},
		{key: 'Technologie anti-chocs', value: 'ShockGuard Pro'},
	]},
];