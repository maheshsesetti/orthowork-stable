import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './feature.reducer';
import { IFeature } from 'app/shared/model/feature.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Feature = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const featureList = useAppSelector(state => state.feature.entities);
  const loading = useAppSelector(state => state.feature.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="feature-heading" data-cy="FeatureHeading">
        <Translate contentKey="orthoworksApp.feature.home.title">Features</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="orthoworksApp.feature.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="orthoworksApp.feature.home.createLabel">Create new Feature</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {featureList && featureList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="orthoworksApp.feature.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.feature.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.feature.mandatory">Mandatory</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.feature.collection">Collection</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {featureList.map((feature, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${feature.id}`} color="link" size="sm">
                      {feature.id}
                    </Button>
                  </td>
                  <td>{feature.name}</td>
                  <td>{feature.mandatory ? 'true' : 'false'}</td>
                  <td>{feature.collection ? <Link to={`collection/${feature.collection.name}`}>{feature.collection.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${feature.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${feature.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${feature.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="orthoworksApp.feature.home.notFound">No Features found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Feature;
