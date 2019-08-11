import { combineReducers } from 'redux';
import { reducer as form } from "redux-form";
import auth from './auth';
import message from './message';

const rootReducer = combineReducers({
  form,
	auth,
	message
});

export default rootReducer;
