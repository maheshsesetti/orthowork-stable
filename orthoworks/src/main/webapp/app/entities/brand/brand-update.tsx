import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IBrandCategory } from 'app/shared/model/brand-category.model';
import { getEntities as getBrandCategories } from 'app/entities/brand-category/brand-category.reducer';
import { getEntity, updateEntity, createEntity, reset } from './brand.reducer';
import { IBrand } from 'app/shared/model/brand.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { BrandStatus } from 'app/shared/model/enumerations/brand-status.model';
import { Size } from 'app/shared/model/enumerations/size.model';

export const BrandUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const brandCategories = useAppSelector(state => state.brandCategory.entities);
  const brandEntity = useAppSelector(state => state.brand.entity);
  const loading = useAppSelector(state => state.brand.loading);
  const updating = useAppSelector(state => state.brand.updating);
  const updateSuccess = useAppSelector(state => state.brand.updateSuccess);
  const brandStatusValues = Object.keys(BrandStatus);
  const sizeValues = Object.keys(Size);
  const handleClose = () => {
    props.history.push('/brand' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getBrandCategories({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...brandEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          status: 'AVAILABLE',
          brandSize: 'S',
          ...brandEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="orthoworksApp.brand.home.createOrEditLabel" data-cy="BrandCreateUpdateHeading">
            <Translate contentKey="orthoworksApp.brand.home.createOrEditLabel">Create or edit a Brand</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="brand-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('orthoworksApp.brand.title')}
                id="brand-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('orthoworksApp.brand.keywords')}
                id="brand-keywords"
                name="keywords"
                data-cy="keywords"
                type="text"
              />
              <ValidatedField
                label={translate('orthoworksApp.brand.description')}
                id="brand-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedBlobField
                label={translate('orthoworksApp.brand.image')}
                id="brand-image"
                name="image"
                data-cy="image"
                isImage
                accept="image/*"
              />
              <ValidatedField
                label={translate('orthoworksApp.brand.rating')}
                id="brand-rating"
                name="rating"
                data-cy="rating"
                type="text"
              />
              <ValidatedField
                label={translate('orthoworksApp.brand.status')}
                id="brand-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {brandStatusValues.map(brandStatus => (
                  <option value={brandStatus} key={brandStatus}>
                    {translate('orthoworksApp.BrandStatus.' + brandStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('orthoworksApp.brand.price')}
                id="brand-price"
                name="price"
                data-cy="price"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('orthoworksApp.brand.brandSize')}
                id="brand-brandSize"
                name="brandSize"
                data-cy="brandSize"
                type="select"
              >
                {sizeValues.map(size => (
                  <option value={size} key={size}>
                    {translate('orthoworksApp.Size.' + size)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('orthoworksApp.brand.dateAdded')}
                id="brand-dateAdded"
                name="dateAdded"
                data-cy="dateAdded"
                type="date"
              />
              <ValidatedField
                label={translate('orthoworksApp.brand.dateModified')}
                id="brand-dateModified"
                name="dateModified"
                data-cy="dateModified"
                type="date"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/brand" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default BrandUpdate;
