import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import promise from 'redux-promise';

import reducers from './reducers';

import Post from './containers/post';
import PostsIndex from './containers/posts_index';
import CreatePostForm from './containers/create_post_form';

const createStoreWithMiddleware = applyMiddleware(promise)(createStore);

const getLayout = () => {
	return (
		<Provider store={createStoreWithMiddleware(reducers)}>
			<BrowserRouter>
				<div>
					<Switch>
						<Route path="/posts/new" component={CreatePostForm}/>
						<Route path="/posts/:id" component={Post}/>
						<Route path="/" component={PostsIndex}/>
					</Switch>
				</div>
			</BrowserRouter>
		</Provider>
	)
};

ReactDOM.render(getLayout(), document.querySelector('.container'));
