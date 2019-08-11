import axios from 'axios';
import { url } from '../helpers/url';

export const FETCH_POSTS = 'FETCH_POSTS';
export const FETCH_SINGLE_POST = 'FETCH_SINGLE_POST';
export const CREATE_POST = 'CREATE_POST';
export const DELETE_POST = 'DELETE_POST';

export function fetchPosts() {
	const request = axios.get(url('/posts'));

	return {
		type: FETCH_POSTS,
		payload: request
	}
}

export function createPost(values, callback) {
	const request = axios.post(url('/posts'), values).then(callback);

	return {
		type: CREATE_POST,
		payload: request
	}
}

export function fetchSinglePost(id) {
	const request = axios.get(url(`/posts/${id}`));

	return {
		type: FETCH_SINGLE_POST,
		payload: request
	}
}

export function deletePost(id, callback) {
	axios.delete(url(`/posts/${id}`)).then(callback);

	return {
		type: DELETE_POST,
		payload: id
	}
}
