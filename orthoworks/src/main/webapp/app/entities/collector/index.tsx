import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Collector from './collector';
import CollectorDetail from './collector-detail';
import CollectorUpdate from './collector-update';
import CollectorDeleteDialog from './collector-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CollectorUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CollectorUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CollectorDetail} />
      <ErrorBoundaryRoute path={match.url} component={Collector} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CollectorDeleteDialog} />
  </>
);

export default Routes;
