import {badges, products, productTechnicalSheet} from './buy-data';
import logoBeco from '../svg/logo-beco.svg';

export default (props, { $h }) => {
	const product = products[props.productId];

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
					<div class="title">${product.name}</div>
					<div class="right">
						<svg-container class="logo" innerHTML=${logoBeco}></svg-container>
					</div>
				</div>
			</div>
			${/* Page Content */''}
			<div class="page-content">
				<div class="block flex center relative">
					<img src=${product.img} style="width:80%"/>
					<div class="detail-price">${product.price}</div>
				</div>
				<div class="block flex-col">
					<div class="card card-raised svg-badge detail">
						${product.badges.length !== 0 && $h/*html*/`
							<div class="card-content card-content-padding line-break">
								${product.badges.map(b => $h/*html*/`
									<div>
										<span class=${badges[b].iconClass}>
											<svg-container innerHTML=${badges[b].svg}></svg-container>
										</span>
										<span class="badge-label">${badges[b].name}</span>
									</div>
								`)}
							</div>
						`}
					</div>
					<div class="technical-sheet">
						${productTechnicalSheet.map(ts => $h/*html*/`
							<div class="card card-raised">
								<div class="card-header">
									<span><i class="icon f7-icons">square_list</i> ${ts.component}</span>
								</div>
								<div class="card-content card-content-padding line-break">
									<ul>
										${ts.properties.map(p => $h/*html*/`
											<li>${p.key}: ${p.value}</li>
										`)}
									</ul>
								</div>
							</div>
						`)}
					</div>
					<button class="button button-tonal">Aller sur le site web du vendeur</button>
				</div>
			</div>
		</div>
	`;
}