import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from 'app/modules/administration/user-management/user-management.reducer';
import register from 'app/modules/account/register/register.reducer';
import activate from 'app/modules/account/activate/activate.reducer';
import password from 'app/modules/account/password/password.reducer';
import settings from 'app/modules/account/settings/settings.reducer';
import passwordReset from 'app/modules/account/password-reset/password-reset.reducer';
// prettier-ignore
import transaction from 'app/entities/transaction/transaction.reducer';
// prettier-ignore
import address from 'app/entities/address/address.reducer';
// prettier-ignore
import data from 'app/entities/data/data.reducer';
// prettier-ignore
import customer from 'app/entities/customer/customer.reducer';
// prettier-ignore
import collection from 'app/entities/collection/collection.reducer';
// prettier-ignore
import tenant from 'app/modules/profile/user/tenant/tenant.reducer';
// prettier-ignore
import art from 'app/entities/art/art.reducer';
// prettier-ignore
import output from 'app/entities/output/output.reducer';
// prettier-ignore
import collector from 'app/entities/collector/collector.reducer';
// prettier-ignore
import notification from 'app/entities/notification/notification.reducer';
// prettier-ignore
import invoice from 'app/entities/invoice/invoice.reducer';
// prettier-ignore
import artist from 'app/entities/artist/artist.reducer';
// prettier-ignore
import brand from 'app/entities/brand/brand.reducer';
// prettier-ignore
import brandCategory from 'app/entities/brand-category/brand-category.reducer';
// prettier-ignore
import feature from 'app/entities/feature/feature.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  transaction,
  address,
  data,
  customer,
  collection,
  tenant,
  art,
  output,
  collector,
  notification,
  invoice,
  artist,
  brand,
  brandCategory,
  feature,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
