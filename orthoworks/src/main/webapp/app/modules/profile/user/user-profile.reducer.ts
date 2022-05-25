import axios from 'axios';
import { createAsyncThunk, createSlice, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { IUser, defaultValue } from 'app/shared/model/user.model';
import { serializeAxiosError } from 'app/shared/reducers/reducer.utils';

const initialState = {
  loading: false,
  errorMessage: null,
  users: [] as ReadonlyArray<IUser>,
  authorities: [] as any[],
  user: defaultValue,
  updating: false,
  updateSuccess: false,
  totalItems: 0,
};

const adminUrl = 'api/admin/users';

// Async Actions

export const getUser = createAsyncThunk(
  'userManagement/fetch_user',
  async (id: string) => {
    const requestUrl = `${adminUrl}/${id}`;
    return axios.get<IUser>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export type UserProfileState = Readonly<typeof initialState>;

export const UserProfileSlice = createSlice({
  name: 'userManagement',
  initialState: initialState as UserProfileState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(getUser.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload.data;
      })
      .addCase(getUser.pending, state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addCase(getUser.rejected, (state, action) => {
        state.loading = false;
        state.updating = false;
        state.updateSuccess = false;
        state.errorMessage = action.error.message;
      });
  },
});

export const { reset } = UserProfileSlice.actions;

// Reducer
export default UserProfileSlice.reducer;
