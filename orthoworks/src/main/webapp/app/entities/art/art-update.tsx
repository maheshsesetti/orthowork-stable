import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ICollection } from 'app/shared/model/collection.model';
import { getEntities as getCollections } from 'app/entities/collection/collection.reducer';
import { getEntity, updateEntity, createEntity, reset } from './art.reducer';
import { IArt } from 'app/shared/model/art.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AssetType } from 'app/shared/model/enumerations/asset-type.model';
import { Type } from 'app/shared/model/enumerations/type.model';

export const ArtUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const collections = useAppSelector(state => state.collection.entities);
  const artEntity = useAppSelector(state => state.art.entity);
  const loading = useAppSelector(state => state.art.loading);
  const updating = useAppSelector(state => state.art.updating);
  const updateSuccess = useAppSelector(state => state.art.updateSuccess);
  const assetTypeValues = Object.keys(AssetType);
  const typeValues = Object.keys(Type);
  const handleClose = () => {
    props.history.push('/art' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getCollections({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...artEntity,
      ...values,
      collections: mapIdList(values.collections),
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
          assetType: 'IMAGE',
          type: 'PHYGITAL',
          ...artEntity,
          collections: artEntity?.collections?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="orthoworksApp.art.home.createOrEditLabel" data-cy="ArtCreateUpdateHeading">
            <Translate contentKey="orthoworksApp.art.home.createOrEditLabel">Create or edit a Art</Translate>
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
                  id="art-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('orthoworksApp.art.name')}
                id="art-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                }}
              />
              <ValidatedField
                label={translate('orthoworksApp.art.handle')}
                id="art-handle"
                name="handle"
                data-cy="handle"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 2, message: translate('entity.validation.minlength', { min: 2 }) },
                }}
              />
              <ValidatedField
                label={translate('orthoworksApp.art.assetType')}
                id="art-assetType"
                name="assetType"
                data-cy="assetType"
                type="select"
              >
                {assetTypeValues.map(assetType => (
                  <option value={assetType} key={assetType}>
                    {translate('orthoworksApp.AssetType.' + assetType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label={translate('orthoworksApp.art.type')} id="art-type" name="type" data-cy="type" type="select">
                {typeValues.map(type => (
                  <option value={type} key={type}>
                    {translate('orthoworksApp.Type.' + type)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('orthoworksApp.art.collection')}
                id="art-collection"
                data-cy="collection"
                type="select"
                multiple
                name="collections"
              >
                <option value="" key="0" />
                {collections
                  ? collections.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/art" replace color="info">
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

export default ArtUpdate;
