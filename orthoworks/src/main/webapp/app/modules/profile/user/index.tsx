import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';
import PrivateRoute from 'app/shared/auth/private-route';

import UserProfilePage from './user-profile';
import UserAdminPage from './user-admin';

const Routes = ({ match }) => (
  <>
    <Switch>
      <PrivateRoute exact path={`${match.url}/:name/admin`} component={UserAdminPage}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:name/display`} component={UserProfilePage}/>
      </Switch>
  </>
);

export default Routes;
