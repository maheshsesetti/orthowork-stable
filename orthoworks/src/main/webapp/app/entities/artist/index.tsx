import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Artist from './artist';
import ArtistDetail from './artist-detail';
import ArtistUpdate from './artist-update';
import ArtistDeleteDialog from './artist-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ArtistUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ArtistUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ArtistDetail} />
      <ErrorBoundaryRoute path={match.url} component={Artist} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ArtistDeleteDialog} />
  </>
);

export default Routes;
