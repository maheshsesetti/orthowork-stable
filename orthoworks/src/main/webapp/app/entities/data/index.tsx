import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Data from './data';
import DataDetail from './data-detail';
import DataUpdate from './data-update';
import DataDeleteDialog from './data-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={DataUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={DataUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={DataDetail} />
      <ErrorBoundaryRoute path={match.url} component={Data} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={DataDeleteDialog} />
  </>
);

export default Routes;
