import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './feature.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FeatureDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const featureEntity = useAppSelector(state => state.feature.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="featureDetailsHeading">
          <Translate contentKey="orthoworksApp.feature.detail.title">Feature</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{featureEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="orthoworksApp.feature.name">Name</Translate>
            </span>
          </dt>
          <dd>{featureEntity.name}</dd>
          <dt>
            <span id="mandatory">
              <Translate contentKey="orthoworksApp.feature.mandatory">Mandatory</Translate>
            </span>
          </dt>
          <dd>{featureEntity.mandatory ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="orthoworksApp.feature.collection">Collection</Translate>
          </dt>
          <dd>{featureEntity.collection ? featureEntity.collection.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/feature" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/feature/${featureEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FeatureDetail;
