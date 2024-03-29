import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import { Router, Route, browserHistory } from 'react-router';

import App from './components/app';
import Resources from './components/resources';
import requireAuth from './components/require_authentication';
import reducers from './reducers';

const createStoreWithMiddleware = applyMiddleware()(createStore);

const cmp = (
	<Provider store={createStoreWithMiddleware(reducers)}>
		<Router history={browserHistory}>
			<Route path="/" component={App}>
				<Route path="resources" component={requireAuth(Resources)}/>
			</Route>
		</Router>
	</Provider>
);

ReactDOM.render(cmp, document.querySelector('.container'));
