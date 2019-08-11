import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import { Router, Route, IndexRoute, browserHistory } from 'react-router';
import reduxThunk from 'redux-thunk';

import App from './components/app';
import Signin from './components/auth/signin';
import Signup from './components/auth/signup';
import Signout from './components/auth/signout';
import requireAuth from './components/auth/require_authentication';
import Feature from "./components/feature";
import Welcome from "./components/welcome";
import reducers from './reducers';
import { AUTH_USER } from "./actions/types";

const createStoreWithMiddlewares = applyMiddleware(reduxThunk)(createStore);
const store = createStoreWithMiddlewares(reducers);

const token = localStorage.getItem('token');

if(token) {
	store.dispatch({
		type: AUTH_USER
	});
}

const mainComponent = (
	<Provider store={store}>
		<Router history={browserHistory}>
			<Route path="/" component={App}>
				<IndexRoute component={Welcome}/>
				<Route path="signin" component={Signin}/>
				<Route path="signup" component={Signup}/>
				<Route path="signout" component={Signout}/>
				<Route path="feature" component={requireAuth(Feature)}/>
			</Route>
		</Router>
	</Provider>
);

ReactDOM.render(mainComponent, document.querySelector('.container'));
