import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './brand.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BrandDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const brandEntity = useAppSelector(state => state.brand.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="brandDetailsHeading">
          <Translate contentKey="orthoworksApp.brand.detail.title">Brand</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{brandEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="orthoworksApp.brand.title">Title</Translate>
            </span>
          </dt>
          <dd>{brandEntity.title}</dd>
          <dt>
            <span id="keywords">
              <Translate contentKey="orthoworksApp.brand.keywords">Keywords</Translate>
            </span>
          </dt>
          <dd>{brandEntity.keywords}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="orthoworksApp.brand.description">Description</Translate>
            </span>
          </dt>
          <dd>{brandEntity.description}</dd>
          <dt>
            <span id="image">
              <Translate contentKey="orthoworksApp.brand.image">Image</Translate>
            </span>
          </dt>
          <dd>
            {brandEntity.image ? (
              <div>
                {brandEntity.imageContentType ? (
                  <a onClick={openFile(brandEntity.imageContentType, brandEntity.image)}>
                    <img src={`data:${brandEntity.imageContentType};base64,${brandEntity.image}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {brandEntity.imageContentType}, {byteSize(brandEntity.image)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="rating">
              <Translate contentKey="orthoworksApp.brand.rating">Rating</Translate>
            </span>
          </dt>
          <dd>{brandEntity.rating}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="orthoworksApp.brand.status">Status</Translate>
            </span>
          </dt>
          <dd>{brandEntity.status}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="orthoworksApp.brand.price">Price</Translate>
            </span>
          </dt>
          <dd>{brandEntity.price}</dd>
          <dt>
            <span id="brandSize">
              <Translate contentKey="orthoworksApp.brand.brandSize">Brand Size</Translate>
            </span>
          </dt>
          <dd>{brandEntity.brandSize}</dd>
          <dt>
            <span id="dateAdded">
              <Translate contentKey="orthoworksApp.brand.dateAdded">Date Added</Translate>
            </span>
          </dt>
          <dd>{brandEntity.dateAdded ? <TextFormat value={brandEntity.dateAdded} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="dateModified">
              <Translate contentKey="orthoworksApp.brand.dateModified">Date Modified</Translate>
            </span>
          </dt>
          <dd>
            {brandEntity.dateModified ? <TextFormat value={brandEntity.dateModified} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/brand" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/brand/${brandEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BrandDetail;
