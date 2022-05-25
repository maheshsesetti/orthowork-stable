import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Art from './art';
import ArtDetail from './art-detail';
import ArtUpdate from './art-update';
import ArtDeleteDialog from './art-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ArtUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ArtUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ArtDetail} />
      <ErrorBoundaryRoute path={match.url} component={Art} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ArtDeleteDialog} />
  </>
);

export default Routes;
