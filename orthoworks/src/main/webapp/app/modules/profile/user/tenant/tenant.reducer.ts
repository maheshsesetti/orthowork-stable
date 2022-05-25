import { createAsyncThunk, isFulfilled, isPending } from "@reduxjs/toolkit";
import { defaultValue, ICollection } from "app/shared/model/collection.model";
import { createEntitySlice, EntityState, IQueryParams } from "app/shared/reducers/reducer.utils";
import axios from "axios";

const initialState: EntityState<ICollection> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/collections';

export const getEntities = createAsyncThunk('tenant/fetch_entity_list', async ({ page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
    return axios.get<ICollection[]>(requestUrl);
  });

export const TenantSlice = createEntitySlice({
  name: 'tenant',
  initialState,
  extraReducers(builder) {
    builder
    .addMatcher(isFulfilled(getEntities), (state, action) => {
      const { data, headers } = action.payload;

      return {
        ...state,
        loading: false,
        entities: data,
        totalItems: parseInt(headers['x-total-count'], 10),
      };
    })
    .addMatcher(isPending(getEntities), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.loading = true;
    });
  },
});

export const { reset } = TenantSlice.actions;

// Reducer
export default TenantSlice.reducer;