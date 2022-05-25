import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Feature from './incentive';
import FeatureDetail from '../feature/feature-detail';
import FeatureUpdate from '../feature/feature-update';
import FeatureDeleteDialog from '../feature/feature-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FeatureUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FeatureUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FeatureDetail} />
      <ErrorBoundaryRoute path={match.url} component={Feature} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FeatureDeleteDialog} />
  </>
);

export default Routes;
