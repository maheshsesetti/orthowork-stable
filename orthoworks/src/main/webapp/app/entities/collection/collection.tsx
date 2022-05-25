import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { byteSize, Translate, getSortState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './collection.reducer';
import { ICollection } from 'app/shared/model/collection.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import './collection.scss';

export const Collection = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE, 'id'), props.location.search)
  );

  const collectionList = useAppSelector(state => state.collection.entities);
  const loading = useAppSelector(state => state.collection.loading);
  const totalItems = useAppSelector(state => state.collection.totalItems);
  const userAccount = useAppSelector(state => state.authentication.account);
  const isAdmin = hasAnyAuthority(userAccount.authorities,[AUTHORITIES.ADMIN]);

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
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (props.location.search !== endURL) {
      props.history.push(`${props.location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

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

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const { match } = props;

  return (
    <div>
      <h2 id="collection-heading" data-cy="CollectionHeading">
        <Translate contentKey="orthoworksApp.collection.home.title">Collections</Translate>
        <div className="d-flex justify-content-center">
          <Link to={`${match.url}/new`} className="me-2 btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <Translate contentKey="orthoworksApp.collection.home.createLabel">Create new Collection</Translate>
          </Link>
          <Link to={`${match.url}/new-vip-nft`} className="me-2 btn vip-nft btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <Translate contentKey="orthoworksApp.collection.home.createVIPNFTLabel">VIP NFTs</Translate>
          </Link>
          <Link to={`${match.url}/new-event-ticket`} className="me-2 btn event-ticket btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <Translate contentKey="orthoworksApp.collection.home.createEventTicketLabel">Event Ticket</Translate>
          </Link>
          <Link to={`${match.url}/new-discount`} className="me-2 btn discount btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <Translate contentKey="orthoworksApp.collection.home.createDiscountsLabel">Discounts</Translate>
          </Link>
          <Button color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
          </Button>
        </div>
      </h2>
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
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${collection.name}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      { userAccount.login === collection.owner ? (
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
                      { userAccount.login === collection.owner || isAdmin ? (
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
      {totalItems ? (
        <div className={collectionList && collectionList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Collection;
