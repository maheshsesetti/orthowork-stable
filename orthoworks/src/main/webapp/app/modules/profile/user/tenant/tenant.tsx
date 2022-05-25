import React, { useEffect, useState } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Row, Badge, Table, Button } from 'reactstrap';
import { Translate, TextFormat, getSortState } from 'react-jhipster';

import { APP_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { languages } from 'app/config/translation';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUser } from 'app/modules/profile/user/user-profile.reducer';
import { getEntities } from './tenant.reducer';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

export const TenantPage = (props: RouteComponentProps<{ name: string }>) => {
  const dispatch = useAppDispatch();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE, 'id'), props.location.search)
  );

  const user = useAppSelector(state => state.userManagement.user);
  const collectionList = useAppSelector(state => state.tenant.entities).filter(collection => collection.owner === user.login);
  const loading = useAppSelector(state => state.tenant.loading);
  const isAdmin = hasAnyAuthority(user.authorities,[AUTHORITIES.ADMIN]);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      })
    );
  };

  const sortEntities = () => {
    getAllEntities();
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    dispatch(getUser(props.match.params.name));
  }, []);
  
  useEffect(() => {
    const params = new URLSearchParams(props.location.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [props.location.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };
  
  const { match } = props;

  return (
    <div>
      <h2>
        <Translate contentKey="userManagement.detail.title">User</Translate> [<strong>{user.login}</strong>]
      </h2>
      <Row size="md">
        <dl className="jh-entity-details">
          <dt>
            <Translate contentKey="userManagement.login">Login</Translate>
          </dt>
          <dd>
            <span>{user.login}</span>&nbsp;
            {user.activated ? (
              <Badge color="success">
                <Translate contentKey="userManagement.activated">Activated</Translate>
              </Badge>
            ) : (
              <Badge color="danger">
                <Translate contentKey="userManagement.deactivated">Deactivated</Translate>
              </Badge>
            )}
          </dd>
          <dt>
            <Translate contentKey="userManagement.firstName">First Name</Translate>
          </dt>
          <dd>{user.firstName}</dd>
          <dt>
            <Translate contentKey="userManagement.lastName">Last Name</Translate>
          </dt>
          <dd>{user.lastName}</dd>
          <dt>
            <Translate contentKey="userManagement.email">Email</Translate>
          </dt>
          <dd>{user.email}</dd>
          <dt>
            <Translate contentKey="userManagement.langKey">Lang Key</Translate>
          </dt>
          <dd>{user.langKey ? languages[user.langKey].name : undefined}</dd>
          <dt>
            <Translate contentKey="userManagement.createdBy">Created By</Translate>
          </dt>
          <dd>{user.createdBy}</dd>
          <dt>
            <Translate contentKey="userManagement.createdDate">Created Date</Translate>
          </dt>
          <dd>{user.createdDate ? <TextFormat value={user.createdDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid /> : null}</dd>
          <dt>
            <Translate contentKey="userManagement.lastModifiedBy">Last Modified By</Translate>
          </dt>
          <dd>{user.lastModifiedBy}</dd>
          <dt>
            <Translate contentKey="userManagement.lastModifiedDate">Last Modified Date</Translate>
          </dt>
          <dd>
            {user.lastModifiedDate ? (
              <TextFormat value={user.lastModifiedDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />
            ) : null}
          </dd>
        </dl>
      </Row>
      <div className="table-responsive">
        {collectionList && collectionList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="orthoworksApp.collection.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="orthoworksApp.collection.title">Title</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('collectionType')}>
                  <Translate contentKey="orthoworksApp.collection.collectionType">Collection Type</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('auctionType')}>
                  <Translate contentKey="orthoworksApp.collection.auctionType">Auction Type</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('currency')}>
                  <Translate contentKey="orthoworksApp.collection.currency">Currency</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('owner')}>
                  <Translate contentKey="orthoworksApp.collection.owner">Owner</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('features')}>
                  <Translate contentKey="orthoworksApp.collection.features">Features</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {collectionList.map((collection, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${collection.name}`} color="link" size="sm">
                      {collection.id}
                    </Button>
                  </td>
                  <td>{collection.title}</td>
                  <td>
                    <Translate contentKey={`orthoworksApp.CollectionType.${collection.collectionType}`} />
                  </td>
                  <td>
                    <Translate contentKey={`orthoworksApp.AuctionType.${collection.auctionType}`} />
                  </td>
                  <td>
                    <Translate contentKey={`orthoworksApp.Currency.${collection.currency}`} />
                  </td>
                  <td>{collection.owner}</td>
                  <td>{collection.features}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${collection.name}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      { user.login === collection.owner ? (
                        <Button
                          tag={Link}
                          to={`${match.url}/${collection.name}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                          color="primary"
                          size="sm"
                          data-cy="entityEditButton"
                        >
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                      ) : null}
                      { user.login === collection.owner || isAdmin ? (
                        <Button
                          tag={Link}
                          to={`${match.url}/${collection.name}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                          color="danger"
                          size="sm"
                          data-cy="entityDeleteButton"
                        >
                          <FontAwesomeIcon icon="trash" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      ) : null}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
          ) : (
            !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="orthoworksApp.collection.home.notFound">No Collections found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default TenantPage;
