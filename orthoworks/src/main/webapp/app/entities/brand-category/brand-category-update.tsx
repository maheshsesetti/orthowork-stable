import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities as getBrandCategories } from 'app/entities/brand-category/brand-category.reducer';
import { IBrand } from 'app/shared/model/brand.model';
import { getEntities as getBrands } from 'app/entities/brand/brand.reducer';
import { getEntity, updateEntity, createEntity, reset } from './brand-category.reducer';
import { IBrandCategory } from 'app/shared/model/brand-category.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BrandCategoryUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const brandCategories = useAppSelector(state => state.brandCategory.entities);
  const brands = useAppSelector(state => state.brand.entities);
  const brandCategoryEntity = useAppSelector(state => state.brandCategory.entity);
  const loading = useAppSelector(state => state.brandCategory.loading);
  const updating = useAppSelector(state => state.brandCategory.updating);
  const updateSuccess = useAppSelector(state => state.brandCategory.updateSuccess);
  const handleClose = () => {
    props.history.push('/brand-category' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getBrandCategories({}));
    dispatch(getBrands({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...brandCategoryEntity,
      ...values,
      brands: mapIdList(values.brands),
      parent: brandCategories.find(it => it.id.toString() === values.parent.toString()),
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
          ...brandCategoryEntity,
          parent: brandCategoryEntity?.parent?.id,
          brands: brandCategoryEntity?.brands?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="orthoworksApp.brandCategory.home.createOrEditLabel" data-cy="BrandCategoryCreateUpdateHeading">
            <Translate contentKey="orthoworksApp.brandCategory.home.createOrEditLabel">Create or edit a BrandCategory</Translate>
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
                  id="brand-category-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('orthoworksApp.brandCategory.description')}
                id="brand-category-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('orthoworksApp.brandCategory.sortOrder')}
                id="brand-category-sortOrder"
                name="sortOrder"
                data-cy="sortOrder"
                type="text"
              />
              <ValidatedField
                label={translate('orthoworksApp.brandCategory.dateAdded')}
                id="brand-category-dateAdded"
                name="dateAdded"
                data-cy="dateAdded"
                type="date"
              />
              <ValidatedField
                label={translate('orthoworksApp.brandCategory.dateModified')}
                id="brand-category-dateModified"
                name="dateModified"
                data-cy="dateModified"
                type="date"
              />
              <ValidatedField
                id="brand-category-parent"
                name="parent"
                data-cy="parent"
                label={translate('orthoworksApp.brandCategory.parent')}
                type="select"
              >
                <option value="" key="0" />
                {brandCategories
                  ? brandCategories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('orthoworksApp.brandCategory.brand')}
                id="brand-category-brand"
                data-cy="brand"
                type="select"
                multiple
                name="brands"
              >
                <option value="" key="0" />
                {brands
                  ? brands.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/brand-category" replace color="info">
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

export default BrandCategoryUpdate;
