import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './brand-category.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BrandCategoryDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const brandCategoryEntity = useAppSelector(state => state.brandCategory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="brandCategoryDetailsHeading">
          <Translate contentKey="orthoworksApp.brandCategory.detail.title">BrandCategory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{brandCategoryEntity.id}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="orthoworksApp.brandCategory.description">Description</Translate>
            </span>
          </dt>
          <dd>{brandCategoryEntity.description}</dd>
          <dt>
            <span id="sortOrder">
              <Translate contentKey="orthoworksApp.brandCategory.sortOrder">Sort Order</Translate>
            </span>
          </dt>
          <dd>{brandCategoryEntity.sortOrder}</dd>
          <dt>
            <span id="dateAdded">
              <Translate contentKey="orthoworksApp.brandCategory.dateAdded">Date Added</Translate>
            </span>
          </dt>
          <dd>
            {brandCategoryEntity.dateAdded ? (
              <TextFormat value={brandCategoryEntity.dateAdded} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="dateModified">
              <Translate contentKey="orthoworksApp.brandCategory.dateModified">Date Modified</Translate>
            </span>
          </dt>
          <dd>
            {brandCategoryEntity.dateModified ? (
              <TextFormat value={brandCategoryEntity.dateModified} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="orthoworksApp.brandCategory.parent">Parent</Translate>
          </dt>
          <dd>{brandCategoryEntity.parent ? brandCategoryEntity.parent.id : ''}</dd>
          <dt>
            <Translate contentKey="orthoworksApp.brandCategory.brand">Brand</Translate>
          </dt>
          <dd>
            {brandCategoryEntity.brands
              ? brandCategoryEntity.brands.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.title}</a>
                    {brandCategoryEntity.brands && i === brandCategoryEntity.brands.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/brand-category" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/brand-category/${brandCategoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BrandCategoryDetail;
