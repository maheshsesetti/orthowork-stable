import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './collection.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from '../feature/feature.reducer';

export const CollectionDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const loading = useAppSelector(state => state.feature.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  },[]);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const collectionEntity = useAppSelector(state => state.collection.entity);
  const { match } = props;
  const featureList = useAppSelector(state => state.feature.entities).filter(feature => feature.collection.id === collectionEntity.id);
  
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="collectionDetailsHeading">
          <Translate contentKey="orthoworksApp.collection.detail.title">Collection</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{collectionEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="orthoworksApp.collection.name">Name</Translate>
            </span>
          </dt>
          <dd>{collectionEntity.name}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="orthoworksApp.collection.title">Title</Translate>
            </span>
          </dt>
          <dd>{collectionEntity.title}</dd>
          <dt>
            <span id="count">
              <Translate contentKey="orthoworksApp.collection.count">Count</Translate>
            </span>
          </dt>
          <dd>{collectionEntity.count}</dd>
          <dt>
            <span id="collectionType">
              <Translate contentKey="orthoworksApp.collection.collectionType">Collection Type</Translate>
            </span>
          </dt>
          <dd>{collectionEntity.collectionType}</dd>
          <dt>
            <span id="auctionType">
              <Translate contentKey="orthoworksApp.collection.auctionType">Auction Type</Translate>
            </span>
          </dt>
          <dd>{collectionEntity.auctionType}</dd>
          <dt>
            <span id="minRange">
              <Translate contentKey="orthoworksApp.collection.minRange">Min Range</Translate>
            </span>
          </dt>
          <dd>{collectionEntity.minRange}</dd>
          <dt>
            <span id="maxRange">
              <Translate contentKey="orthoworksApp.collection.maxRange">Max Range</Translate>
            </span>
          </dt>
          <dd>{collectionEntity.maxRange}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="orthoworksApp.collection.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{collectionEntity.currency}</dd>
          <dt>
            <span id="owner">
              <Translate contentKey="orthoworksApp.collection.owner">Owner</Translate>
            </span>
          </dt>
          <dd>{collectionEntity.owner}</dd>
          <dt>
            <span id="features">
              <Translate contentKey="orthoworksApp.collection.features">Features</Translate>
            </span>
          </dt>
          {featureList && featureList.length > 0 ? (
            featureList.map((feature,i) =>
              <dd key={`feature-${i}`}>{i+1}. {feature.name} - {feature.mandatory ? "Mandatory": "Optional"}</dd>)
          ): (
            !loading && (
              <div className="alert alert-warning">
                <Translate contentKey="orthoworksApp.feature.home.notFound">No Features found</Translate>
              </div>
            )
          )}
        </dl>
        <Button tag={Link} to="/collection" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/collection/${collectionEntity.name}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CollectionDetail;
