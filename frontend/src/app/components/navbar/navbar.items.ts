import {MenuItem, MenuItemCommandEvent} from "primeng/api";

export class NavItem implements MenuItem {
  public id?: string;
  public label?: string;
  public routerLink?: string;
  public icon?: string;
  public styleClass?: string;
  public command?: (event: MenuItemCommandEvent) => void;
  public items: MenuItem[] = [];

  public constructor(
		id?: string | null,
		label?: string | null,
		routerLink?: string | null,
		icon?: string,
	) {
		if(id) this.id = id;
		if(label) this.label = label;
		if(routerLink) this.routerLink = routerLink;
		if(icon) this.setIcon(icon);
	}

  public setIcon(icon: string) {
		this.icon = `pi pi-fw pi-${icon}`;
	}

  public setClass(className: string) {
		this.styleClass = className;
		return this;
	}

  public setAction(callback: (event: MenuItemCommandEvent) => void) {
		this.command = callback;
		return this;
	}

  public children(...children: NavItem[]) {
		this.items = this.items.concat(children);
		return this;
	}
}
