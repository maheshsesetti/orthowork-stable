import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/transaction">
      <Translate contentKey="global.menu.entities.transaction" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/address">
      <Translate contentKey="global.menu.entities.address" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/data">
      <Translate contentKey="global.menu.entities.data" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/customer">
      <Translate contentKey="global.menu.entities.customer" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/collection">
      <Translate contentKey="global.menu.entities.collection" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/art">
      <Translate contentKey="global.menu.entities.art" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/output">
      <Translate contentKey="global.menu.entities.output" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/collector">
      <Translate contentKey="global.menu.entities.collector" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/notification">
      <Translate contentKey="global.menu.entities.notification" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/invoice">
      <Translate contentKey="global.menu.entities.invoice" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/artist">
      <Translate contentKey="global.menu.entities.artist" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/brand">
      <Translate contentKey="global.menu.entities.brand" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/brand-category">
      <Translate contentKey="global.menu.entities.brandCategory" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/feature">
      <Translate contentKey="global.menu.entities.feature" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
