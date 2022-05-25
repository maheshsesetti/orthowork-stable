import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Output from './output';
import OutputDetail from './output-detail';
import OutputUpdate from './output-update';
import OutputDeleteDialog from './output-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={OutputUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={OutputUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={OutputDetail} />
      <ErrorBoundaryRoute path={match.url} component={Output} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={OutputDeleteDialog} />
  </>
);

export default Routes;
