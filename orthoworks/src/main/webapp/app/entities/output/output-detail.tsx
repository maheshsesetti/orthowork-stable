import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './output.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const OutputDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const outputEntity = useAppSelector(state => state.output.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="outputDetailsHeading">
          <Translate contentKey="orthoworksApp.output.detail.title">Output</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{outputEntity.id}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="orthoworksApp.output.date">Date</Translate>
            </span>
          </dt>
          <dd>{outputEntity.date ? <TextFormat value={outputEntity.date} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="result">
              <Translate contentKey="orthoworksApp.output.result">Result</Translate>
            </span>
          </dt>
          <dd>{outputEntity.result}</dd>
          <dt>
            <Translate contentKey="orthoworksApp.output.transaction">Transaction</Translate>
          </dt>
          <dd>{outputEntity.transaction ? outputEntity.transaction.title : ''}</dd>
        </dl>
        <Button tag={Link} to="/output" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/output/${outputEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OutputDetail;
