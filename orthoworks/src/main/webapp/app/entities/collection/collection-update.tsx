import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IArt } from 'app/shared/model/art.model';
import { getEntities as getArts } from 'app/entities/art/art.reducer';
import { getEntity, updateEntity, createEntity, reset } from './collection.reducer';
import { ICollection } from 'app/shared/model/collection.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { CollectionType } from 'app/shared/model/enumerations/collection-type.model';
import { AuctionType } from 'app/shared/model/enumerations/auction-type.model';
import { Currency } from 'app/shared/model/enumerations/currency.model';
import { Slider } from '@mui/material';

function rangeText(value: number) {
  return `${value}`;
}

export const CollectionUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const arts = useAppSelector(state => state.art.entities);
  const collectionEntity = useAppSelector(state => state.collection.entity);
  const loading = useAppSelector(state => state.collection.loading);
  const updating = useAppSelector(state => state.collection.updating);
  const updateSuccess = useAppSelector(state => state.collection.updateSuccess);
  const collectionTypeValues = Object.keys(CollectionType);
  const auctionTypeValues = Object.keys(AuctionType);
  const currencyValues = Object.keys(Currency);
  const userAccount = useAppSelector(state => state.authentication.account);

  const handleClose = () => {
    props.history.push('/collection' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getArts({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...collectionEntity,
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
          collectionType: 'IMAGE',
          auctionType: 'FLAT',
          currency: 'INR',
          ...collectionEntity,
        };

  const [range, setValue] = React.useState<number[]>([20, 37]);

  const sliderChange = (event: Event, newValue: number | number[]) => {
    setValue(newValue as number[]);
  };      

  const featureCount = [1];

  const addFeature = () => {
    featureCount.push(featureCount[featureCount.length - 1] + 1);
  }

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="orthoworksApp.collection.home.createOrEditLabel" data-cy="CollectionCreateUpdateHeading">
            {isNew ? 
            <Translate contentKey="orthoworksApp.collection.home.createLabel">Create a Collection</Translate>
            : <Translate contentKey="orthoworksApp.collection.home.editLabel">Edit a collection</Translate>}
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
                  id="collection-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('orthoworksApp.collection.name')}
                id="collection-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('orthoworksApp.collection.title')}
                id="collection-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('orthoworksApp.collection.count')}
                id="collection-count"
                name="count"
                data-cy="count"
                type="text"
                validate={{
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  max: { value: 10000, message: translate('entity.validation.max', { max: 10000 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('orthoworksApp.collection.collectionType')}
                id="collection-collectionType"
                name="collectionType"
                data-cy="collectionType"
                type="select"
              >
                {collectionTypeValues.map(collectionType => (
                  <option value={collectionType} key={collectionType}>
                    {translate('orthoworksApp.CollectionType.' + collectionType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('orthoworksApp.collection.auctionType')}
                id="collection-auctionType"
                name="auctionType"
                data-cy="auctionType"
                type="select"
              >
                {auctionTypeValues.map(auctionType => (
                  <option value={auctionType} key={auctionType}>
                    {translate('orthoworksApp.AuctionType.' + auctionType)}
                  </option>
                ))}
              </ValidatedField>
              <Slider
                getAriaLabel={() => 'Range'}
                value={range}
                onChange={sliderChange}
                min={0}
                max={9999999}
                valueLabelDisplay="auto"
                getAriaValueText={rangeText}
              />
              <ValidatedField
                label={translate('orthoworksApp.collection.minRange')}
                id="collection-minRange"
                name="minRange"
                data-cy="minRange"
                readOnly
                value={range[0]}
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('orthoworksApp.collection.maxRange')}
                id="collection-maxRange"
                name="maxRange"
                data-cy="maxRange"
                readOnly
                value={range[1]}
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('orthoworksApp.collection.currency')}
                id="collection-currency"
                name="currency"
                data-cy="currency"
                type="select"
              >
                {currencyValues.map(currency => (
                  <option value={currency} key={currency}>
                    {translate('orthoworksApp.Currency.' + currency)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('orthoworksApp.collection.owner')}
                id="collection-owner"
                name="owner"
                data-cy="owner"
                readOnly
                value={userAccount.login}
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                  label={translate('orthoworksApp.collection.features')}
                  id="collection-features"
                  name={`feature`}
                  data-cy="feature"
                  type="text"
                  validate={{
                    required: { value: true, message: translate('entity.validation.required') },
                  }}
                />
              {featureCount.map((i) => {
                <ValidatedField
                  label={translate('orthoworksApp.collection.feature')}
                  id="collection-features"
                  name={`feature-${i}`}
                  data-cy="feature"
                  type="text"
                  validate={{
                    required: { value: true, message: translate('entity.validation.required') },
                  }}
                />
              })}
              <Button onClick={addFeature} id="add" color="white">
                <FontAwesomeIcon icon="plus" />
              </Button>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/collection" replace color="info">
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
              <Button color="secondary" id="publish-entity" data-cy="entityCreatePublishButton" type="submit" disabled={updating}>
                <Translate contentKey="entity.action.publish">Publish</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CollectionUpdate;
