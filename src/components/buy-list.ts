import './buy-detail';
import {badges, products} from './buy-data';
import logoBeco from '../svg/logo-beco.svg';

export default (props, { $f7, $h }) => {
	const goToDetail = (productId) => {
		$f7.views[0].router.navigate('/buy/detail', 
			{animate: true, transition: 'f7-cover', history: true, props: {productId}}
		);
	};

	return () => $h/*html*/`
		<div class="page noscroll">
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
					<div class="title">Résultats</div>
					<div class="right">
						<svg-container class="logo" innerHTML=${logoBeco}></svg-container>
					</div>
				</div>
			</div>
			${/* Page Content */''}
			<div class="page-content flex-col-center-h">
				<div class="list media-list list-outline-ios list-strong-ios list-dividers-ios">
					<ul id="buy-list-logos">
						${products.map((p,i) => $h/*html*/`
							<li>
								<a class="item-link" @click=${_ => goToDetail(i)}>
									<div class="item-content">
										<div class="item-media">
											<img style="border-radius: 8px" src=${p.img} width="72"/>
										</div>
										<div class="item-inner">
											<div class="item-title-row">
												<div class="item-title">${p.name}</div>
												<div class="item-after">${p.price}</div>
											</div>
											<div class="item-text svg-badge">
												${p.badges.map(b => $h/*html*/`
													<span class=${badges[b].iconClass}>
														<svg-container innerHTML=${badges[b].svg}></svg-container>
													</span>
												`)}
											</div>
										</div>
									</div>
								</a>
							</li>
						`)}
					</ul>
				</div>
			</div>
		</div>
	`;
}