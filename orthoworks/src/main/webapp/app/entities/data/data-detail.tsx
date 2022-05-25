import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './data.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const DataDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const dataEntity = useAppSelector(state => state.data.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="dataDetailsHeading">
          <Translate contentKey="orthoworksApp.data.detail.title">Data</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{dataEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="orthoworksApp.data.name">Name</Translate>
            </span>
          </dt>
          <dd>{dataEntity.name}</dd>
          <dt>
            <span id="file">
              <Translate contentKey="orthoworksApp.data.file">File</Translate>
            </span>
          </dt>
          <dd>
            {dataEntity.file ? (
              <div>
                {dataEntity.fileContentType ? (
                  <a onClick={openFile(dataEntity.fileContentType, dataEntity.file)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {dataEntity.fileContentType}, {byteSize(dataEntity.file)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="orthoworksApp.data.transaction">Transaction</Translate>
          </dt>
          <dd>{dataEntity.transaction ? dataEntity.transaction.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/data" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/data/${dataEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DataDetail;
