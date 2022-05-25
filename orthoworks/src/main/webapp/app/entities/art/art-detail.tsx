import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './art.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ArtDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const artEntity = useAppSelector(state => state.art.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="artDetailsHeading">
          <Translate contentKey="orthoworksApp.art.detail.title">Art</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{artEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="orthoworksApp.art.name">Name</Translate>
            </span>
          </dt>
          <dd>{artEntity.name}</dd>
          <dt>
            <span id="handle">
              <Translate contentKey="orthoworksApp.art.handle">Handle</Translate>
            </span>
          </dt>
          <dd>{artEntity.handle}</dd>
          <dt>
            <span id="assetType">
              <Translate contentKey="orthoworksApp.art.assetType">Asset Type</Translate>
            </span>
          </dt>
          <dd>{artEntity.assetType}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="orthoworksApp.art.type">Type</Translate>
            </span>
          </dt>
          <dd>{artEntity.type}</dd>
          <dt>
            <Translate contentKey="orthoworksApp.art.collection">Collection</Translate>
          </dt>
          <dd>
            {artEntity.collections
              ? artEntity.collections.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.title}</a>
                    {artEntity.collections && i === artEntity.collections.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/art" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/art/${artEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ArtDetail;
