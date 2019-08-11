import { combineReducers } from 'redux';
import BooksReducer from './books';
import ActiveBookReducer from './active_book';

const rootReducer = combineReducers({
	books: BooksReducer,
	activeBook: ActiveBookReducer
});

export default rootReducer;
