import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Transaction from './transaction';
import Address from './address';
import Data from './data';
import Customer from './customer';
import Collection from './collection';
import Art from './art';
import Output from './output';
import Collector from './collector';
import Notification from './notification';
import Invoice from './invoice';
import Artist from './artist';
import Brand from './brand';
import BrandCategory from './brand-category';
import Feature from './feature';
import Incentive from './incentives';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}transaction`} component={Transaction} />
      <ErrorBoundaryRoute path={`${match.url}address`} component={Address} />
      <ErrorBoundaryRoute path={`${match.url}data`} component={Data} />
      <ErrorBoundaryRoute path={`${match.url}customer`} component={Customer} />
      <ErrorBoundaryRoute path={`${match.url}collection`} component={Collection} />
      <ErrorBoundaryRoute path={`${match.url}art`} component={Art} />
      <ErrorBoundaryRoute path={`${match.url}output`} component={Output} />
      <ErrorBoundaryRoute path={`${match.url}collector`} component={Collector} />
      <ErrorBoundaryRoute path={`${match.url}notification`} component={Notification} />
      <ErrorBoundaryRoute path={`${match.url}invoice`} component={Invoice} />
      <ErrorBoundaryRoute path={`${match.url}artist`} component={Artist} />
      <ErrorBoundaryRoute path={`${match.url}brand`} component={Brand} />
      <ErrorBoundaryRoute path={`${match.url}brand-category`} component={BrandCategory} />
      <ErrorBoundaryRoute path={`${match.url}feature`} component={Feature} />
      <ErrorBoundaryRoute path={`${match.url}incentives`} component={Incentive} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
