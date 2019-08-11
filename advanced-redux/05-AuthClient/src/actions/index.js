import axios from 'axios';
import { browserHistory } from 'react-router';
import { AUTH_ERROR, AUTH_USER, UNAUTH_USER, FETCH_MESSAGE } from "./types";

const API_URL = 'http://localhost:3090';

export function signupUser({ email, password }) {
	return function(dispatch) {
		const url = `${API_URL}/signup`;
		axios.post(url, { email, password })
			.then(response => {
				localStorage.setItem('token', response.data.token);
				dispatch({ type: AUTH_USER });
				browserHistory.push('/feature')
			})
			.catch(err => {
				const { data: { error }, status } = err.response;
				status >= 500
					? dispatch(authError('There was an error creating your account. Please try again later...'))
					: dispatch(authError(error));
			});
	}
}

export function signinUser({ email, password }) {
	return function(dispatch) {
		const url = `${API_URL}/signin`;
		axios.post(url, { email, password })
			.then(response => {
				localStorage.setItem('token', response.data.token);
				dispatch({ type: AUTH_USER });
				browserHistory.push('/feature')
			})
			.catch(err => {
				const { data: { error }, status } = err.response;
				status >= 500
					? dispatch(authError('There was an error trying to sign you up. Please try again later...'))
					: dispatch(authError('Incorrect email or password.'));
			});
	}
}

export function authError(error) {
	return {
		type: AUTH_ERROR,
		payload: error
	}
}

export function signoutUser() {
	localStorage.removeItem('token');
	return {
		type: UNAUTH_USER
	}
}

export function fetchSecretCode() {
	return function(dispatch) {
		axios.get(API_URL, { headers: { authorization: localStorage.getItem('token') } })
			.then(response => {
				dispatch({
					type: FETCH_MESSAGE,
					payload: response.data.message
				})
			})
	}
}
