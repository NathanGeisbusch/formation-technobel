/** Liste des produits */
const produits = {
    chips1:  {nom: 'Chips 1',  qtt: 1, prix: 1.99, img: 'img/01.webp'},
    chips2:  {nom: 'Chips 2',  qtt: 1, prix: 2.99, img: 'img/02.webp'},
    chips3:  {nom: 'Chips 3',  qtt: 1, prix: 3.99, img: 'img/03.webp'},
    bonbon1: {nom: 'Bonbon 1', qtt: 1, prix: 1.99, img: 'img/04.webp'},
    bonbon2: {nom: 'Bonbon 2', qtt: 1, prix: 2.99, img: 'img/05.webp'},
    bonbon3: {nom: 'Bonbon 3', qtt: 1, prix: 3.99, img: 'img/06.webp'},
    eau:     {nom: 'Eau',      qtt: 1, prix: 1.99, img: 'img/07.webp'},
    nrg1:    {nom: 'NRG 1',    qtt: 1, prix: 2.99, img: 'img/08.webp'},
    nrg2:    {nom: 'NRG 2',    qtt: 1, prix: 3.99, img: 'img/09.webp'},
    soda:    {nom: 'Soda',     qtt: 1, prix: 4.99, img: 'img/10.webp'},
};

/** Liste des produits dans le distributeur */
const distributeur = [
    structuredClone(produits.chips1), // A1
    structuredClone(produits.chips2), // A2
    structuredClone(produits.chips3), // A3

    structuredClone(produits.bonbon1), // B1
    structuredClone(produits.bonbon2), // B2
    structuredClone(produits.bonbon3), // B3

    structuredClone(produits.eau),  // C1
    structuredClone(produits.nrg1), // C2
    structuredClone(produits.nrg2), // C3

    structuredClone(produits.soda), // D1
    null, // D2
    null, // D3
];

/** Boite de dialogue permettant de modifier un produit dans le distributeur */
const dialogModifierProduit = document.getElementById('dialog-modifier-produit');

/**
 * Emplacement du produit en cours de modification
 * @type {number}
 */
let modifiedProductIndex = null;

/**
 * Affiche la boite de dialogue pour modifier le produit sélectionné
 * @param {number} index >= 0 && < distributeur.length
 */
function modifierProduit(index) {
    modifiedProductIndex = index;
    const nom = document.getElementById('modifier-produit-nom');
    const prix = document.getElementById('modifier-produit-prix');
    const qtt = document.getElementById('modifier-produit-qtt');
    if(distributeur[index]) {
        nom.value = Array.from(nom.children).find(e => e.textContent === distributeur[index].nom).value;
        prix.value = distributeur[index].prix;
        qtt.value = distributeur[index].qtt;
    } else {
        nom.value = 'chips1';
        prix.value = produits.chips1.prix;
        qtt.value = 1;
    }
    dialogModifierProduit.showModal();
}

/**
 * Interprète le code d'emplacement du produit sur le pavé numérique
 * et affiche le produit obtenu si l'emplacement est correct.
 */
function achat() {
    const code = document.querySelector('.affichage-code-choix').value;
    if(/^[A-F][0-9]$/.test(code)) {
        const code1 = code.charCodeAt(0) - 'A'.charCodeAt(0);
        const code2 = code.charCodeAt(1) - '1'.charCodeAt(0);
        if(code1 < 0 || code1 > 5) return;
        if(code2 < 0 || code2 > 9) return;
        const index = (code1*3) + code2;
        if(index < 0 || index >= distributeur.length) return;
        const produit = distributeur[index];
        if(!produit) return;
        produit.qtt -= 1;
        const img = document.querySelector('.produit-obtenu img');
        img.classList.remove('hide');
        img.src = produit.img;
        if(produit.qtt === 0) {
            distributeur[index] = null;
            const td = Array.from(document.querySelectorAll('.produits td'))[index];
            td.querySelector('.img-container').classList.add('empty');
        }
        else {
            const td = Array.from(document.querySelectorAll('.produits td'))[index];
            td.querySelector('img').src = produit.img;
            td.querySelector('.qtt').textContent = produit.qtt;
        }
    }
}

/**
 * Ajoute un caractère au code d'emplacement du produit sur le pavé numérique
 * sinon réinitialise le code du pavé numérique.
 */
function pushBtn(value) {
    const code = document.querySelector('.affichage-code-choix');
    const nom = document.querySelector('.affichage-nom-produit');
    const prix = document.querySelector('.affichage-prix-produit');
    if(value === 'E' || value === 'F') {
        code.value = ''; nom.value = ''; prix.value = '';
        return;
    }
    if(code.value.length === 2) {
        code.value = ''; nom.value = ''; prix.value = '';
    }
    if(code.value.length === 0) {
        if(value === 'A' || value === 'B' || value === 'C' || value === 'D') {
            code.value = value;
            nom.value = ''; prix.value = '';
            return;
        }
    }
    if(code.value.length === 1) {
        if(value === '1' || value === '2' || value === '3') {
            code.value += value;
            if(/^[A-F][0-9]$/.test(code.value)) {
                const code1 = code.value.charCodeAt(0) - 'A'.charCodeAt(0);
                const code2 = code.value.charCodeAt(1) - '1'.charCodeAt(0);
                if(code1 < 0 || code1 > 5) return;
                if(code2 < 0 || code2 > 9) return;
                const index = (code1*3) + code2;
                if(index < 0 || index >= distributeur.length) return;
                const produit = distributeur[index];
                if(!produit) return;
                nom.value = produit.nom;
                prix.value = produit.prix+' €';
            }
            else {
                code.value = ''; nom.value = ''; prix.value = '';
            }
        }
    }
}

/** Initialisation des produits dans l'HTML */
(function() {
    // liste de nom des produits dans la boite de dialogue de modification
    const nomsProduits = document.getElementById('modifier-produit-nom');
    Object.entries(produits).forEach(p => {
        const option = document.createElement('option');
        option.value = p[0];
        option.textContent = p[1].nom;
        nomsProduits.append(option);
    });

    // liste de produits dans le distributeur
    const tableBody = document.querySelector('.produits tbody');
    tableBody.innerHTML = '';
    let tr = document.createElement('tr');
    for(let i = 0; i < distributeur.length; i++) {
        const produit = distributeur[i];
        const td = document.createElement('td');
        const imgC = document.createElement('div');
        imgC.className = 'img-container';
        imgC.onclick = () => modifierProduit(i);
        td.append(imgC);
        const qtt = document.createElement('div');
        const img = document.createElement('img');
        qtt.className = 'qtt';
        if(produit) {
            img.src = produit.img;
            qtt.textContent = produit.qtt;
        }
        else imgC.classList.add('empty');
        imgC.append(img, qtt);
        tr.append(td);
        if(i%3 === 2 || i === distributeur.length-1) {
            tableBody.append(tr);
            tr = document.createElement('tr');
        }
    }
})();

/** Applique les valeurs par défaut du produit sélectionné dans le formulaire de modification */
document.getElementById('modifier-produit-nom').addEventListener('input', event => {
    const value = event.target.value;
    const prix = document.getElementById('modifier-produit-prix');
    const qtt = document.getElementById('modifier-produit-qtt');
    prix.value = produits[value].prix;
    qtt.value = produits[value].qtt;
});

/** Valide la modificateur du produit */
document.getElementById('valider-modification-produit').addEventListener('click', _ => {
    // Modifie les données du produit le distributeur.
    const nom = document.getElementById('modifier-produit-nom');
    const prix = document.getElementById('modifier-produit-prix');
    const qtt = document.getElementById('modifier-produit-qtt');
    const nouveauProduit = structuredClone(produits[nom.value]);
    nouveauProduit.prix = +prix.value;
    nouveauProduit.qtt = +qtt.value;
    distributeur[modifiedProductIndex] = nouveauProduit;

    // Modifie l'affichage du produit dans le distributeur (html).
    const td = Array.from(document.querySelectorAll('.produits td'))[modifiedProductIndex];
    if(nouveauProduit.qtt === 0) td.querySelector('.img-container').classList.add('empty');
    else td.querySelector('.img-container').classList.remove('empty');
    td.querySelector('img').src = nouveauProduit.img;
    td.querySelector('.qtt').textContent = nouveauProduit.qtt;

    // Réinitialise l'affichage de la sélection de produit (pavé numérique)
    // et ferme la boite de dialogue de modification du produit.
    const displayCode = document.querySelector('.affichage-code-choix');
    const displayNom = document.querySelector('.affichage-nom-produit');
    const displayPrix = document.querySelector('.affichage-prix-produit');
    displayCode.value = displayNom.value = displayPrix.value = '';
    dialogModifierProduit.close();
});

/** Annule la modification du produit */
document.getElementById('annuler-modification-produit').addEventListener("click", _ => dialogModifierProduit.close());

/** Retire le produit obtenu de l'affichage lorsque l'on clique dessus */
document.querySelector('.produit-obtenu img').addEventListener('click', ev => ev.target.classList.add('hide'));
