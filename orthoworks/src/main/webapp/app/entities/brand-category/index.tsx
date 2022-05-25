import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import BrandCategory from './brand-category';
import BrandCategoryDetail from './brand-category-detail';
import BrandCategoryUpdate from './brand-category-update';
import BrandCategoryDeleteDialog from './brand-category-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BrandCategoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BrandCategoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BrandCategoryDetail} />
      <ErrorBoundaryRoute path={match.url} component={BrandCategory} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={BrandCategoryDeleteDialog} />
  </>
);

export default Routes;
