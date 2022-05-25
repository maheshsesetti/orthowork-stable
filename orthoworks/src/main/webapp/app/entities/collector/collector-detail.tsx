import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './collector.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CollectorDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const collectorEntity = useAppSelector(state => state.collector.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="collectorDetailsHeading">
          <Translate contentKey="orthoworksApp.collector.detail.title">Collector</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{collectorEntity.id}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="orthoworksApp.collector.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{collectorEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="orthoworksApp.collector.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{collectorEntity.lastName}</dd>
          <dt>
            <span id="gender">
              <Translate contentKey="orthoworksApp.collector.gender">Gender</Translate>
            </span>
          </dt>
          <dd>{collectorEntity.gender}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="orthoworksApp.collector.email">Email</Translate>
            </span>
          </dt>
          <dd>{collectorEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="orthoworksApp.collector.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{collectorEntity.phone}</dd>
          <dt>
            <span id="addressLine1">
              <Translate contentKey="orthoworksApp.collector.addressLine1">Address Line 1</Translate>
            </span>
          </dt>
          <dd>{collectorEntity.addressLine1}</dd>
          <dt>
            <span id="addressLine2">
              <Translate contentKey="orthoworksApp.collector.addressLine2">Address Line 2</Translate>
            </span>
          </dt>
          <dd>{collectorEntity.addressLine2}</dd>
          <dt>
            <span id="city">
              <Translate contentKey="orthoworksApp.collector.city">City</Translate>
            </span>
          </dt>
          <dd>{collectorEntity.city}</dd>
          <dt>
            <span id="country">
              <Translate contentKey="orthoworksApp.collector.country">Country</Translate>
            </span>
          </dt>
          <dd>{collectorEntity.country}</dd>
        </dl>
        <Button tag={Link} to="/collector" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/collector/${collectorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CollectorDetail;
