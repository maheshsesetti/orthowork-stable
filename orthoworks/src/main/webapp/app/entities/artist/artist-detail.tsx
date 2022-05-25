import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './artist.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ArtistDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const artistEntity = useAppSelector(state => state.artist.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="artistDetailsHeading">
          <Translate contentKey="orthoworksApp.artist.detail.title">Artist</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{artistEntity.id}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="orthoworksApp.artist.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{artistEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="orthoworksApp.artist.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{artistEntity.lastName}</dd>
          <dt>
            <span id="gender">
              <Translate contentKey="orthoworksApp.artist.gender">Gender</Translate>
            </span>
          </dt>
          <dd>{artistEntity.gender}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="orthoworksApp.artist.email">Email</Translate>
            </span>
          </dt>
          <dd>{artistEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="orthoworksApp.artist.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{artistEntity.phone}</dd>
          <dt>
            <span id="addressLine1">
              <Translate contentKey="orthoworksApp.artist.addressLine1">Address Line 1</Translate>
            </span>
          </dt>
          <dd>{artistEntity.addressLine1}</dd>
          <dt>
            <span id="addressLine2">
              <Translate contentKey="orthoworksApp.artist.addressLine2">Address Line 2</Translate>
            </span>
          </dt>
          <dd>{artistEntity.addressLine2}</dd>
          <dt>
            <span id="city">
              <Translate contentKey="orthoworksApp.artist.city">City</Translate>
            </span>
          </dt>
          <dd>{artistEntity.city}</dd>
          <dt>
            <span id="country">
              <Translate contentKey="orthoworksApp.artist.country">Country</Translate>
            </span>
          </dt>
          <dd>{artistEntity.country}</dd>
        </dl>
        <Button tag={Link} to="/artist" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/artist/${artistEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ArtistDetail;
