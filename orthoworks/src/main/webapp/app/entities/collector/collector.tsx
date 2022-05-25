import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './collector.reducer';
import { ICollector } from 'app/shared/model/collector.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Collector = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const collectorList = useAppSelector(state => state.collector.entities);
  const loading = useAppSelector(state => state.collector.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="collector-heading" data-cy="CollectorHeading">
        <Translate contentKey="orthoworksApp.collector.home.title">Collectors</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="orthoworksApp.collector.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="orthoworksApp.collector.home.createLabel">Create new Collector</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {collectorList && collectorList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="orthoworksApp.collector.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.collector.firstName">First Name</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.collector.lastName">Last Name</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.collector.gender">Gender</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.collector.email">Email</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.collector.phone">Phone</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.collector.addressLine1">Address Line 1</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.collector.addressLine2">Address Line 2</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.collector.city">City</Translate>
                </th>
                <th>
                  <Translate contentKey="orthoworksApp.collector.country">Country</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {collectorList.map((collector, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${collector.id}`} color="link" size="sm">
                      {collector.id}
                    </Button>
                  </td>
                  <td>{collector.firstName}</td>
                  <td>{collector.lastName}</td>
                  <td>
                    <Translate contentKey={`orthoworksApp.Gender.${collector.gender}`} />
                  </td>
                  <td>{collector.email}</td>
                  <td>{collector.phone}</td>
                  <td>{collector.addressLine1}</td>
                  <td>{collector.addressLine2}</td>
                  <td>{collector.city}</td>
                  <td>{collector.country}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${collector.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${collector.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${collector.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="orthoworksApp.collector.home.notFound">No Collectors found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Collector;
